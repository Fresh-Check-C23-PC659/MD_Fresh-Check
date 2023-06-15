package com.example.freshcheck.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.freshcheck.R
import com.example.freshcheck.databinding.ActivityResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bundle = intent.extras
        if (bundle != null) {
            val imagePath = bundle.getString("imagePath")
            val name = bundle.getString("name")
            val prediction = bundle.getString("prediction")


            GlobalScope.launch(Dispatchers.Main) {
                clearGlideCache()
                loadImage(imagePath)
            }

            Glide.with(this)
                .load(imagePath)
                .into(binding.ivResultFragment)

            binding.nameTextView.text = name
            binding.predictionTextView.text = prediction

            val backButton = binding.backButton
            backButton.setOnClickListener {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private suspend fun clearGlideCache() {
        withContext(Dispatchers.IO) {
            Glide.get(applicationContext).clearDiskCache()
        }
    }

    private suspend fun loadImage(imagePath: String?) {
        withContext(Dispatchers.Main) {
            Glide.with(applicationContext)
                .load(imagePath)
                .into(binding.ivResultFragment)
        }
    }
}
