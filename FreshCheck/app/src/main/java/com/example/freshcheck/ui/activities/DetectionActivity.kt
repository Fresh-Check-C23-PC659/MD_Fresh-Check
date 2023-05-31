package com.example.freshcheck.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.freshcheck.databinding.ActivityDetectionBinding

class DetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}