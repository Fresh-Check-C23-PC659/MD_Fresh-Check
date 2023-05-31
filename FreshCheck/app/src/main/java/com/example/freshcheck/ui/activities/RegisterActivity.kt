package com.example.freshcheck.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.freshcheck.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

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
            val username = edUsernameRegister.text.toString().trim()
            val email = edEmailRegister.text.toString().trim()
            val password = edPasswordRegister.text.toString().trim()

            val usernameLayout = tiUsernameRegister
            val emailLayout = tiEmailRegister
            val passwordLayout = tiPasswordRegister

            if (username.isEmpty()) {
                usernameLayout.apply {
                    helperText = "Username must be filled in"
                    requestFocus()
                }
                return
            } else {
                usernameLayout.helperText = null
            }

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
            usernameLayout.clearFocus()
            emailLayout.clearFocus()
            passwordLayout.clearFocus()
            registerFirebase(email, password)
        }

    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView: View? = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    private fun toLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun registerFirebase(email: String, password: String) {
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    showLoading(false)
                    Toast.makeText(this, "Register Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
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
                btnRegister.isEnabled = false
                pbRegister.visibility = View.VISIBLE
                vwBgDimmedRegister.visibility = View.VISIBLE
            } else {
                btnRegister.isEnabled = true
                pbRegister.visibility = View.INVISIBLE
                vwBgDimmedRegister.visibility = View.INVISIBLE
            }
        }
    }
}