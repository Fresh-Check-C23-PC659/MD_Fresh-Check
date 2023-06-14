package com.example.freshcheck.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshcheck.ResultSealed
import com.example.freshcheck.data.Repository
import com.example.freshcheck.data.preferences.TokenUidPreferences
import com.example.freshcheck.di.Injection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val tokenUidPreferences: TokenUidPreferences = Injection.provideTokenUidPreferences(context)

    private val _login = MutableSharedFlow<ResultSealed<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<ResultSealed<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        tokenUidPreferences.saveTokenUid(it.uid)
                        _login.emit(ResultSealed.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(ResultSealed.Error(it.message.toString()))
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