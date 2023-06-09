package com.example.freshcheck.data

import com.example.freshcheck.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import retrofit2.http.Multipart

class Repository private constructor(private val apiService: ApiService) {
    suspend fun sendPicture(file: MultipartBody.Part) {

    }

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService
        ): Repository = instance ?: synchronized(this) {
            instance ?: Repository(apiService)
        }.also { instance = it }
    }
}