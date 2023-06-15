package com.example.freshcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshcheck.ResultSealed
import com.example.freshcheck.data.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainCategoryViewModel() : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _fruitProducts = MutableStateFlow<ResultSealed<List<Product>>>(ResultSealed.Initial)
    val fruitProducts = _fruitProducts.asStateFlow()

    init {
        fetchFruitProducts()
    }

    fun fetchFruitProducts() {
        viewModelScope.launch {
            _fruitProducts.emit(ResultSealed.Loading)
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Buah").get()
            .addOnSuccessListener { result ->
                val fruitProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _fruitProducts.emit(ResultSealed.Success(fruitProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _fruitProducts.emit(ResultSealed.Error(it.message.toString()))
                }
            }
    }
}