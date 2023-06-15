package com.example.freshcheck.ui.activities

import ViewModelFactory
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.freshcheck.R
import com.example.freshcheck.ResultSealed
import com.example.freshcheck.databinding.ActivityLoginBinding
import com.example.freshcheck.ui.viewmodel.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            forgotPassword.setOnClickListener {
                setupBottomSheetDialog { email ->
                    viewModel.resetPassword(email)
                }
            }
            tvToRegister.setOnClickListener { toRegister() }
            btnLogin.setOnClickListener { handleLogin() }
        }

        lifecycleScope.launch {
            viewModel.resetPassword.collect {
                when (it) {
                    is ResultSealed.Loading -> {
                        showLoading(true)
                    }

                    is ResultSealed.Success -> {
                        val view: View = findViewById(R.id.tv_to_register)
                        showLoading(false)
                        Snackbar.make(view, "Link was sent to your email", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    is ResultSealed.Error -> {
                        val view: View = findViewById(R.id.tv_to_register)
                        showLoading(false)
                        Snackbar.make(view, "Error : ${it.error}", Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
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

    @SuppressLint("InflateParams")
    private fun setupBottomSheetDialog(onSendClick: (String) -> Unit) {
        dialog = BottomSheetDialog(this, R.style.DialogStyle)
        val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()

        val edEmail = view.findViewById<EditText>(R.id.ed_reset_password)
        val btnSend = view.findViewById<Button>(R.id.btn_send_reset_password)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_reset_password)

        btnSend.setOnClickListener {
            val email = edEmail.text.toString().trim()
            onSendClick(email)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun loginFirebase(email: String, password: String) {
        viewModel.signInWithEmailAndPassword(email, password)
        showLoading(true) // Show loading state initially

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.login.collect {
                when (it) {
                    is ResultSealed.Success -> {
                        Intent(this@LoginActivity, HomeActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        Log.d(TAG, it.data.toString())
                        showLoading(false)
                    }

                    is ResultSealed.Error -> {
                        Toast.makeText(this@LoginActivity, it.error, Toast.LENGTH_SHORT)
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
                btnLogin.visibility = View.GONE
                pbLogin.visibility = View.VISIBLE
            } else {
                btnLogin.visibility = View.VISIBLE
                pbLogin.visibility = View.GONE
            }
        }
    }
}