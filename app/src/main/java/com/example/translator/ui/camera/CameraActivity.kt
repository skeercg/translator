package com.example.translator.ui.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.translator.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}