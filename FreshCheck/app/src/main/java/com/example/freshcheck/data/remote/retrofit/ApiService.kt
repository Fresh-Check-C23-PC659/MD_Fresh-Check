package com.example.freshcheck.data.remote.retrofit

import com.example.freshcheck.data.remote.response.Response
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/")
    suspend fun uploadFile(
        @Part filePart: MultipartBody.Part
    ): Response
}
