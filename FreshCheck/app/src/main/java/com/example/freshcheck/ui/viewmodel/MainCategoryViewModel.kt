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

    private val _vegetableProducts = MutableStateFlow<ResultSealed<List<Product>>>(ResultSealed.Initial)
    val vegetableProducts = _vegetableProducts.asStateFlow()

    init {
        fetchFruitProducts()
        fetchVegetableProducts()
    }

    private fun fetchFruitProducts() {
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

    private fun fetchVegetableProducts() {
        viewModelScope.launch {
            _vegetableProducts.emit(ResultSealed.Loading)
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Sayur").get()
            .addOnSuccessListener { result ->
                val vegetableProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _vegetableProducts.emit(ResultSealed.Success(vegetableProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _vegetableProducts.emit(ResultSealed.Error(it.message.toString()))
                }
            }
    }
}