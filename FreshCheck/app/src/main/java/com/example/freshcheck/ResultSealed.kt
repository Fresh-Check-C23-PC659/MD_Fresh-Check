package com.example.freshcheck

sealed class ResultSealed<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultSealed<T>()
    data class Error(val error: String) : ResultSealed<Nothing>()
    object Loading : ResultSealed<Nothing>()
    object Initial : ResultSealed<Nothing>()
}

