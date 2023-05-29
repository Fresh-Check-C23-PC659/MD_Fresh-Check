package com.example.freshcheck.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.freshcheck.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            tvToLogin.setOnClickListener { toLogin() }
            btnRegister.setOnClickListener { handleRegister() }
        }
    }

    private fun handleRegister() {
        binding.apply {
            val email = edEmailRegister.text.toString()
            val password = edPasswordRegister.text.toString()

            if (email.isEmpty()) {
                edEmailRegister.error = "Email Must Be Filled In"
                edEmailRegister.requestFocus()
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edEmailRegister.error = "Invalid Email"
                edEmailRegister.requestFocus()
            }

            if (password.isEmpty()) {
                edPasswordRegister.error = "Password must be filled in"
                edPasswordRegister.requestFocus()
            }

            if (password.length < 8) {
                edPasswordRegister.error = "Password at least 8 characters"
                edPasswordRegister.requestFocus()
            }
            registerFirebase(email, password)
        }

    }

    private fun toLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun registerFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Register Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}