package com.example.freshcheck.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcheck.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            forgotPassword.setOnClickListener { toForgotPassword() }
            tvToRegister.setOnClickListener { toRegister() }
            btnLogin.setOnClickListener { toHome() }
        }
    }

    private fun handleLogin() {
        binding.apply {
            val email = edEmailLogin.text.toString().trim()
            val password = edPasswordLogin.text.toString().trim()

            val emailLayout = tiEmailLogin
            val passwordLayout = tiPasswordLogin

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

            if (password.isEmpty()) {
                passwordLayout.apply {
                    helperText = "Password must be filled in"
                    requestFocus()
                }
                return
            } else {
                passwordLayout.helperText = null
            }
            hideKeyboard()
            emailLayout.clearFocus()
            passwordLayout.clearFocus()
            loginFirebase(email, password)
        }
    }

    private fun toHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView: View? = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
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
        showLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    showLoading(false)
                    Toast.makeText(this, "Welcome $email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    showLoading(false)
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                btnLogin.isEnabled = false
                pbLogin.visibility = View.VISIBLE
                vwBgDimmedLogin.visibility = View.VISIBLE
            } else {
                btnLogin.isEnabled = true
                pbLogin.visibility = View.INVISIBLE
                vwBgDimmedLogin.visibility = View.INVISIBLE
            }
        }
    }
}