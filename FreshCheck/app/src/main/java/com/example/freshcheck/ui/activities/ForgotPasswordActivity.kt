package com.example.freshcheck.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcheck.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.apply {
            btnReset.setOnClickListener { handleForgotPassword() }
        }
    }

    private fun handleForgotPassword() {
        binding.apply {
            val email = edEmailForgotPassword.text.toString().trim()

            val emailLayout = tiEmailForgotPassword

            when {
                email.isEmpty() -> {
                    emailLayout.apply {
                        helperText = "Email must be filled in"
                        requestFocus()
                    }
                    return
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailLayout.apply {
                        helperText = "Invalid Email"
                        setEndIconOnClickListener {
                            editText?.setText("")
                            helperText = null
                        }
                        requestFocus()
                    }
                    return
                }
                else -> emailLayout.helperText = null
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {

                if (it.isSuccessful) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Password Reset Email Has Been Sent",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "${it.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
}