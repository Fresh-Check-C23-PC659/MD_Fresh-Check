package com.example.freshcheck.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.freshcheck.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            forgotPassword.setOnClickListener { toForgotPassword() }
            tvToRegister.setOnClickListener { toRegister() }
            btnLogin.setOnClickListener { handleLogin() }
        }
    }

    private fun handleLogin() {
        binding.apply {
            val email = edEmailLogin.text.toString()
            val password = edPasswordLogin.text.toString()

            if (email.isEmpty()){
                edEmailLogin.error = "Email Must Be Filled In"
                edEmailLogin.requestFocus()
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edPasswordLogin.error = "Invalid Email"
                edPasswordLogin.requestFocus()
            }

            if (password.isEmpty()){
                edPasswordLogin.error = "Password must be filled in"
                edPasswordLogin.requestFocus()
            }

            loginFirebase(email,password)
        }
    }

    private fun toRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun toForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun loginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Welcome $email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}