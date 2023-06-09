package com.example.freshcheck.di

import android.content.Context
import com.example.freshcheck.data.Repository
import com.example.freshcheck.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
}