package com.example.freshcheck.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.freshcheck.data.Repository
import com.example.freshcheck.data.preferences.TokenUidPreferences
import com.example.freshcheck.data.remote.retrofit.ApiConfig
import com.example.freshcheck.di.Injection.dataStore

object Injection {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_preferences")

    fun provideRepository(): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }

    fun provideTokenUidPreferences(context: Context): TokenUidPreferences {
        return TokenUidPreferences.getInstance(context.dataStore)
    }
}