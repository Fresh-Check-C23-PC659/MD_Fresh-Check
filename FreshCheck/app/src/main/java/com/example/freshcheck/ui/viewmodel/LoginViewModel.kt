package com.example.freshcheck.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshcheck.ResultSealed
import com.example.freshcheck.data.preferences.TokenUidPreferences
import com.example.freshcheck.di.Injection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(context: Context) : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val tokenUidPreferences: TokenUidPreferences =
        Injection.provideTokenUidPreferences(context)

    private val _login = MutableSharedFlow<ResultSealed<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<ResultSealed<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            var loadingShown = true

            try {
                _login.emit(ResultSealed.Loading)

                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user
                user?.let {
                    tokenUidPreferences.saveTokenUid(it.uid)
                    _login.emit(ResultSealed.Success(it))
                    loadingShown = false
                } ?: run {
                    _login.emit(ResultSealed.Error("Sign-in failed. User is null."))
                    loadingShown = false
                }
            } catch (e: Exception) {
                _login.emit(ResultSealed.Error(e.message.toString()))
                loadingShown = false
            } finally {
                if (loadingShown) {
                    _login.emit(ResultSealed.Loading)
                }
            }
        }
    }


    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(ResultSealed.Loading)

            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _resetPassword.emit(ResultSealed.Success(email))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _resetPassword.emit(ResultSealed.Error(it.message.toString()))
                    }
                }
        }
    }
}