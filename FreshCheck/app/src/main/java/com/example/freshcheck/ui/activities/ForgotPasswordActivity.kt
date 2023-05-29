package com.example.freshcheck.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.freshcheck.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btnReset.setOnClickListener {
                val email = edEmailReset.text.toString()
                val edtEmail = edEmailReset

                if (email.isEmpty()) {
                    edtEmail.error = "Email cannot be empty"
                    edtEmail.requestFocus()
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.error = "Invalid Email"
                    edtEmail.requestFocus()
                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {

                    if (it.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Password Reset Email Has Been Sent", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}