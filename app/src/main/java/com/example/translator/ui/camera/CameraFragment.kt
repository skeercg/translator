package com.example.translator.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.translator.databinding.FragmentCameraBinding
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var camera: CameraDevice
    private lateinit var cameraCharacteristics: CameraCharacteristics

    override fun onDestroy() {
        camera.close()
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
            binding.surfaceView.display,
            cameraCharacteristics,
            SurfaceHolder::class.java
        )

        binding.surfaceView.setAspectRatio(
            previewSize.width,
            previewSize.height
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
            val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

            captureRequestBuilder.addTarget(binding.surfaceView.holder.surface)

            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )

            session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
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
            camera = cameraDevice

            val outputConfig = OutputConfiguration(binding.surfaceView.holder.surface)
            val cameraSessionExecutor = Executors.newSingleThreadExecutor()
            val cameraSessionStateCallback = CameraSessionStateCallback()

            camera.createCaptureSession(
                SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR,
                    listOf(outputConfig),
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
        binding.captureButton.setOnClickListener {}
    }
}
