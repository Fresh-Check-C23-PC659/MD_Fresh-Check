package com.example.freshcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshcheck.ResultSealed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<ResultSealed<User>>(ResultSealed.Initial)
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(ResultSealed.Loading)
        }
        db.collection("user").document(firebaseAuth.uid!!)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    viewModelScope.launch {
                        _user.emit(ResultSealed.Error(error.toString()))
                    }
                } else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(ResultSealed.Success(user))
                        }
                    }
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}