package com.example.translator.ui.camera

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.translator.databinding.FragmentCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding

    private lateinit var cameraDevice: CameraDevice

    private lateinit var cameraCharacteristics: CameraCharacteristics

    private lateinit var cameraCaptureSession: CameraCaptureSession

    private lateinit var imageReader: ImageReader

    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    private val cameraHandler = Handler(cameraThread.looper)

    private val imageReaderThread = HandlerThread("ImageReaderThread").apply { start() }

    private val imageReaderHandler = Handler(imageReaderThread.looper)

    override fun onDestroy() {
        cameraDevice.close()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        binding.surfaceView.holder.addCallback(SurfaceViewCallback())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()

        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("MissingPermission")
    private fun startCameraSession() {
        val cameraManager = context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (cameraManager.cameraIdList.isEmpty()) {
            return
        }
        val firstCamera = cameraManager.cameraIdList.first()

        cameraManager.openCamera(firstCamera, CameraStateCallback(), null)
        cameraCharacteristics = cameraManager.getCameraCharacteristics(firstCamera)

        val previewSize = getPreviewOutputSize(
            binding.surfaceView.display, cameraCharacteristics, SurfaceHolder::class.java
        )

        binding.surfaceView.setAspectRatio(
            previewSize.width, previewSize.height
        )

        val size = cameraCharacteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!.getOutputSizes(ImageFormat.JPEG).maxByOrNull { it.height * it.width }!!

        imageReader = ImageReader.newInstance(
            size.width, size.height, ImageFormat.JPEG, 3
        )
    }

    inner class SurfaceViewCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            startCameraSession()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

        override fun surfaceDestroyed(holder: SurfaceHolder) {}
    }

    inner class CameraSessionStateCallback : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            cameraCaptureSession = session

            val captureRequestBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

            captureRequestBuilder.addTarget(binding.surfaceView.holder.surface)

            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )

            cameraCaptureSession.setRepeatingRequest(
                captureRequestBuilder.build(), null, cameraHandler
            )
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {}
    }

    inner class CameraStateCallback : CameraDevice.StateCallback() {
        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }

        override fun onOpened(cameraDevice: CameraDevice) {
            this@CameraFragment.cameraDevice = cameraDevice

            val surfaceOutputConfig = OutputConfiguration(binding.surfaceView.holder.surface)
            val imageReaderOutputConfig = OutputConfiguration(imageReader.surface)
            val cameraSessionExecutor = Executors.newSingleThreadExecutor()
            val cameraSessionStateCallback = CameraSessionStateCallback()

            this@CameraFragment.cameraDevice.createCaptureSession(
                SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR,
                    listOf(surfaceOutputConfig, imageReaderOutputConfig),
                    cameraSessionExecutor,
                    cameraSessionStateCallback,
                )
            )
        }
    }

    private fun setOnClickListeners() {
        binding.backButton.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.captureButton.setOnClickListener {
            it.isEnabled = false

            lifecycleScope.launch(Dispatchers.IO) {
                imageReader.setOnImageAvailableListener({ reader ->
                    val image = reader.acquireLatestImage()
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }

                    val intent = Intent().apply {
                        putExtra("image", bytes)
                    }
                    activity?.setResult(RESULT_OK, intent)
                    activity?.finish()

//                    val output = createFile(requireContext())
//                    FileOutputStream(output).use { fileOutputStream ->
//                        fileOutputStream.write(bytes)
//                    }
                }, imageReaderHandler)

                val captureRequest = cameraCaptureSession.device.createCaptureRequest(
                    CameraDevice.TEMPLATE_STILL_CAPTURE
                ).apply { addTarget(imageReader.surface) }

                cameraCaptureSession.capture(captureRequest.build(), null, cameraHandler)

                it.post { it.isEnabled = true }
            }
        }
    }

    private fun createFile(context: Context): File {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
        return File(context.filesDir, "IMG_${sdf.format(Date())}.jpg")
    }
}
