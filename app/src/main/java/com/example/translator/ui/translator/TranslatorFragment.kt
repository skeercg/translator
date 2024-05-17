package com.example.translator.ui.translator

import com.example.translator.ui.history.HistoryFragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.translator.R
import com.example.translator.databinding.FragmentTranslatorBinding
import com.example.translator.ui.camera.CameraActivity


class TranslatorFragment : Fragment() {
    private lateinit var binding: FragmentTranslatorBinding

    private val viewModel: TranslatorViewModel by viewModels { TranslatorViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTranslatorBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toggleVisibility(false)
        setOnClickListeners()

        binding.sourceLanguage.text = setLanguage(viewModel.sourceLanguage)
        binding.targetLanguage.text = setLanguage(viewModel.targetLanguage)
        binding.sourceTextFieldLabel.text = setLanguage(viewModel.sourceLanguage)
        binding.targetTextFieldLabel.text = setLanguage(viewModel.targetLanguage)

        val sourceTextFieldWatcher = TranslatorTextWatcher()
        binding.sourceTextField.addTextChangedListener(sourceTextFieldWatcher)
        binding.sourceTextField.onFocusChangeListener = setOnFocusChangeListener()

        viewModel.sourceText.observe(viewLifecycleOwner) { content ->
            binding.sourceTextField.removeTextChangedListener(sourceTextFieldWatcher)
            binding.sourceTextField.setText(content)
            binding.sourceTextField.addTextChangedListener(sourceTextFieldWatcher)
        }

        binding.historyButton.setOnClickListener {
            // Handle click on the button
            handleHistoryClick()
        }

        viewModel.targetText.observe(viewLifecycleOwner) { content ->
            binding.targetTextField.setText(content)
            if (content.isNullOrEmpty()) {
                toggleVisibility(false)
            } else {
                toggleVisibility(true)
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setLanguage(languageAbr: String): String {
        return when (languageAbr) {
            "rus" -> getString(R.string.rus)
            "eng" -> getString(R.string.eng)
            else -> throw IllegalArgumentException("Unknown Language")
        }
    }

    private fun swapLanguage() {
        val buffer: String = viewModel.sourceLanguage
        viewModel.sourceLanguage = viewModel.targetLanguage
        viewModel.targetLanguage = buffer

        binding.sourceLanguage.text = setLanguage(viewModel.sourceLanguage)
        binding.targetLanguage.text = setLanguage(viewModel.targetLanguage)
        binding.sourceTextFieldLabel.text = setLanguage(viewModel.sourceLanguage)
        binding.targetTextFieldLabel.text = setLanguage(viewModel.targetLanguage)

        binding.sourceTextField.setText(viewModel.targetText.value ?: "")
        binding.targetTextField.setText("")

        toggleVisibility(false)
        viewModel.translateText()
    }

    inner class TranslatorTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.sourceText.value = s.toString()
            viewModel.translateText()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun setOnClickListeners() {
        binding.copyTargetTextButton.setOnClickListener {
            copyToClipboard(viewModel.targetText.value)
        }
        binding.copySourceTextButton.setOnClickListener {
            copyToClipboard(viewModel.sourceText.value)
        }
        binding.swapLanguageButton.setOnClickListener {
            swapLanguage()
        }
        binding.cameraButton.setOnClickListener {
            startCameraActivity()
        }
    }

    private fun setOnFocusChangeListener(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val sourceNotEmpty = (viewModel.sourceText.value?.isNotEmpty() ?: false)
                val targetNotEmpty = (viewModel.targetText.value?.isNotEmpty() ?: false)
                if (sourceNotEmpty && targetNotEmpty) {
                    toggleVisibility(true)
                }

                val inputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
            } else {
                toggleVisibility(false)
            }
        }
    }

    private fun copyToClipboard(text: String?) {
        val clipboardManager =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Translation", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun toggleVisibility(value: Boolean) {
        binding.sourceTextFieldLabel.isVisible = value
        binding.targetTextFieldLabel.isVisible = value
        binding.copySourceTextButton.isVisible = value
        binding.copyTargetTextButton.isVisible = value
        binding.divider.isVisible = value
    }

    private val captureImageLauncher = registerForActivityResult(
        CameraActivityResultContract(),
        CameraActivityResultCallback(),
    )

    inner class CameraActivityResultCallback : ActivityResultCallback<ByteArray?> {
        override fun onActivityResult(result: ByteArray?) {
            Log.d("ACTIVITY RESULT", "$result")

            viewModel.translateImage(result)
        }
    }

    inner class CameraActivityResultContract : ActivityResultContract<Unit?, ByteArray?>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            return Intent(context, CameraActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ByteArray? {
            return intent?.getByteArrayExtra("image")
        }
    }

    private fun startCameraActivity() {
        captureImageLauncher.launch(null)
    }

    private fun handleHistoryClick() {
        // Create an instance of the AnotherFragment
        val historyFragment = HistoryFragment()

        // Get the FragmentManager
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

        // Begin the fragment transaction
        val transaction = fragmentManager.beginTransaction()

        // Replace the current fragment with AnotherFragment
        transaction.replace(R.id.fragment_translator, historyFragment)

        // Add the transaction to the back stack (optional)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }
}