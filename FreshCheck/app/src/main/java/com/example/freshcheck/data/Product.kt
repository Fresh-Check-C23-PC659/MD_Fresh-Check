package com.example.freshcheck.data

class Product(
    val id: String,
    val name: String,
    val catergory: String,
    val price: Float,
    val description: String? = null,
    val images: List<String>
) {
    constructor(): this("0", "", "", 0f, images = emptyList())
}