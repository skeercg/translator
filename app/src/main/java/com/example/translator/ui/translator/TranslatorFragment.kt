package com.example.translator.ui.translator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.translator.R
import com.example.translator.databinding.FragmentTranslatorBinding


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
        binding.sourceTextFieldLabel.visibility = View.GONE
        binding.targetTextFieldLabel.visibility = View.GONE
        binding.copySourceTextButton.visibility = View.GONE
        binding.copyTargetTextButton.visibility = View.GONE

        binding.copyTargetTextButton.setOnClickListener {
            copyToClipboard(viewModel.targetText.value)
        }
        binding.copySourceTextButton.setOnClickListener {
            copyToClipboard(viewModel.sourceText.value)
        }

        binding.sourceLanguage.text = setLanguage(viewModel.sourceLanguage)
        binding.targetLanguage.text = setLanguage(viewModel.targetLanguage)
        binding.sourceTextFieldLabel.text = setLanguage(viewModel.sourceLanguage)
        binding.targetTextFieldLabel.text = setLanguage(viewModel.targetLanguage)

        binding.sourceTextField.addTextChangedListener(setTextWatcher())
        binding.sourceTextField.onFocusChangeListener = setOnFocusChangeListener()

        binding.swapLanguageButton.setOnClickListener {
            swapLanguage()
        }

        viewModel.targetText.observe(viewLifecycleOwner) { content ->
            binding.targetTextField.setText(content)
            if (content.isNullOrEmpty()) {
                binding.divider.visibility = View.GONE
            } else {
                binding.divider.visibility = View.VISIBLE
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
    }

    private fun setTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.sourceText.value = s.toString()
                viewModel.translate()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun setOnFocusChangeListener(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val showLabels = viewModel.sourceText.value?.isNotEmpty()
                if (showLabels != null && showLabels) {
                    binding.sourceTextFieldLabel.visibility = View.VISIBLE
                    binding.targetTextFieldLabel.visibility = View.VISIBLE
                    binding.copySourceTextButton.visibility = View.VISIBLE
                    binding.copyTargetTextButton.visibility = View.VISIBLE
                }

                val inputMethodManager =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
            } else {
                binding.sourceTextFieldLabel.visibility = View.GONE
                binding.targetTextFieldLabel.visibility = View.GONE
                binding.copySourceTextButton.visibility = View.GONE
                binding.copyTargetTextButton.visibility = View.GONE
            }
        }
    }

    private fun copyToClipboard(text: String?) {
        val clipboardManager =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Translation", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}