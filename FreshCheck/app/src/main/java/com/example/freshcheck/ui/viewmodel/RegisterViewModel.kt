package com.example.freshcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshcheck.ResultSealed
import com.example.freshcheck.data.User
import com.example.freshcheck.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _register = MutableStateFlow<ResultSealed<User>>(ResultSealed.Initial)
    val register: Flow<ResultSealed<User>> = _register

//    fun createAccountWithEmailAndPassword(user: User, password: String) {
//        runBlocking {
//            _register.emit(ResultSealed.Loading)
//        }
//        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
//            .addOnSuccessListener {
//                it.user?.let {
//                    saveUser(it.uid, user)
//                }
//            }.addOnFailureListener {
//                _register.value = ResultSealed.Error(it.message.toString())
//            }
//    }

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        viewModelScope.launch {
            _register.emit(ResultSealed.Loading)
            var loadingShown = true // Flag to track if loading state has been shown

            try {
                val authResult =
                    firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
                val createdUser = authResult.user
                createdUser?.let {
                    saveUser(it.uid, user)
                    _register.emit(ResultSealed.Success(user))
                    loadingShown = false
                } ?: run {
                    _register.emit(ResultSealed.Error("Failed to create user. User is null."))
                    loadingShown = false
                }
            } catch (e: Exception) {
                _register.emit(ResultSealed.Error(e.message.toString()))
                loadingShown = false
            } finally {
                if (loadingShown) {
                    _register.emit(ResultSealed.Loading)
                }
            }
        }
    }


    private fun saveUser(userUid: String, user: User) {
        db.collection(Constants.USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = ResultSealed.Success(user)
            }.addOnFailureListener {
                _register.value = ResultSealed.Error(it.message.toString())
            }
    }
}