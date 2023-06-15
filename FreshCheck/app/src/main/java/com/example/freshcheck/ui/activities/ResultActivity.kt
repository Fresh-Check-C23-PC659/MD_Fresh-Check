package com.example.freshcheck.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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


            val boldName = "<b><br><i>${name?.replaceFirstChar { it.uppercaseChar() }}<i></b>"
            val boldPrediction = "<b><br><i>${prediction?.replaceFirstChar { it.uppercaseChar() }}<i></b>"
            binding.nameTextView.text = HtmlCompat.fromHtml("Vegetable/Fruit Name :$boldName", HtmlCompat.FROM_HTML_MODE_COMPACT)
            binding.predictionTextView.text = HtmlCompat.fromHtml("Level :$boldPrediction", HtmlCompat.FROM_HTML_MODE_COMPACT)


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
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.ivResultFragment)
        }
    }
}
