package com.example.freshcheck.ui.activities

import ViewModelFactory
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
import com.example.freshcheck.data.User
import com.example.freshcheck.databinding.ActivityRegisterBinding
import com.example.freshcheck.ui.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch


private val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            tvToLogin.setOnClickListener { toLogin() }
            btnRegister.setOnClickListener { handleRegister() }
        }
    }

    private fun handleRegister() {
        binding.apply {
            val username = edUsernameRegister.text.toString().trim()
            val email = edEmailRegister.text.toString().trim()
            val password = edPasswordRegister.text.toString()

            val user = User(
                username,
                email,
            )

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
            registerFirebase(user, password)
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

    private fun registerFirebase(user: User, password: String) {
        viewModel.createAccountWithEmailAndPassword(user, password)
        lifecycleScope.launch {
            viewModel.register.collect {
                when (it) {
                    is ResultSealed.Loading -> {
                        showLoading(true)
                    }

                    is ResultSealed.Success -> {
                        Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_LONG)
                            .show()
                        Log.d(TAG, it.data.toString())
                        showLoading(false)
                        toLogin()
                    }

                    is ResultSealed.Error -> {
                        Toast.makeText(this@RegisterActivity, "Register Failed", Toast.LENGTH_LONG)
                            .show()
                        Log.e(TAG, it.error)
                        showLoading(false)
                    }

                    else -> Unit
                }
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