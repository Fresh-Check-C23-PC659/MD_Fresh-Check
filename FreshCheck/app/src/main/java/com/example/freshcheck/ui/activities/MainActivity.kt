package com.example.freshcheck.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcheck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnStart.setOnClickListener { toLogin() }
        }
    }

    private fun toLogin() {
        val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}