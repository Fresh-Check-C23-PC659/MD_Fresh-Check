package com.example.freshcheck.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcheck.databinding.ActivityProductDetailBinding
import com.example.freshcheck.ui.adapter.ViewPager2Adapter
import java.text.DecimalFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding

    private lateinit var productName: String
    private lateinit var productPrice: String
    private lateinit var productDesc: String

    private lateinit var viewPagerAdapter: ViewPager2Adapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = ViewPager2Adapter()
        binding.vpProductDetailImages.adapter = viewPagerAdapter


        val imagePaths = intent.getStringArrayExtra("productImage")?.toList()
        productName = intent.getStringExtra("productName") ?: ""
        productPrice = intent.getFloatExtra("productPrice", 0.0f).toString()
        productDesc = intent.getStringExtra("productDesc") ?: ""

        if (imagePaths != null) {
            viewPagerAdapter.setImageData(imagePaths)
        }

        updateUI()

        val closeButton = binding.ivProductDetailClose
        closeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI() {
        binding.apply {
            tvProductDetailItemName.text = productName
            tvProductDetailItemPrice.text = formatPrice(productPrice.toFloat())
            tvProductDetailItemDescription.text = productDesc.replace("\\n", "\n")

            vpProductDetailImages.adapter = viewPagerAdapter
        }
    }

    private fun formatPrice(price: Float): String {
        val format = DecimalFormat.getCurrencyInstance(Locale("id", "ID")) as DecimalFormat
        val symbols = format.decimalFormatSymbols
        symbols.currencySymbol = "Rp."
        format.decimalFormatSymbols = symbols
        return format.format(price.toDouble())
    }

    private fun handleError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}