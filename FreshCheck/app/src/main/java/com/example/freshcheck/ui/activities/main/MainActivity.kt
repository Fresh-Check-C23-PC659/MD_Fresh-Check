package com.example.freshcheck.ui.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcheck.R
import com.example.freshcheck.databinding.ActivityMainBinding
import com.example.freshcheck.databinding.FragmentDetectionBinding
import com.example.freshcheck.ui.activities.HomeActivity
import com.example.freshcheck.ui.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingDF: FragmentDetectionBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            btnStart.setOnClickListener { toLogin() }
        }

        if (isLoggedIn()) {
            toHome()
        }
    }

    private fun toHome() {
        val homeIntent = Intent(this@MainActivity, HomeActivity::class.java)
        startActivity(homeIntent)
        finish()
    }

    private fun toLogin() {
        val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun isLoggedIn(): Boolean {
        val currentUser = firebaseAuth.currentUser
        return currentUser != null
    }
}
