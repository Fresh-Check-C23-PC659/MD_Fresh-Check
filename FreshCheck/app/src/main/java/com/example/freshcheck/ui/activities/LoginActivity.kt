package com.example.freshcheck.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.freshcheck.ResultSealed
import com.example.freshcheck.databinding.ActivityLoginBinding
import com.example.freshcheck.ui.viewmodel.LoginViewModel
import com.example.freshcheck.ui.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    private lateinit var auth: FirebaseAuth

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
        viewModel.signInWithEmailAndPassword(email, password)
        lifecycleScope.launch {
            viewModel.login.collect() {
                when (it) {
                    is ResultSealed.Loading -> {
                        showLoading(true)
                    }

                    is ResultSealed.Success -> {
                        showLoading(false)
                        Intent(this@LoginActivity, HomeActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        Log.d(TAG, it.data.toString())
                    }

                    is ResultSealed.Error -> {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, it.error, Toast.LENGTH_SHORT)
                            .show()
                        Log.e(TAG, it.error)
                    }

                    else -> Unit
                }
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