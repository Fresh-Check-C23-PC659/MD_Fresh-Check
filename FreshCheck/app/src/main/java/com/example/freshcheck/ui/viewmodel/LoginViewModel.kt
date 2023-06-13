package com.example.freshcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshcheck.ResultSealed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _login = MutableSharedFlow<ResultSealed<FirebaseUser>>()
    val login  = _login.asSharedFlow()

    fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(ResultSealed.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(ResultSealed.Error(it.message.toString()))
                }
            }
    }
}