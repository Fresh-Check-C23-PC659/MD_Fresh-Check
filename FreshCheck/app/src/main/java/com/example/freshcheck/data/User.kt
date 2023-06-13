package com.example.freshcheck.data

data class User(
    val username: String,
    val email: String,
) {
    constructor(): this("", "")
}
