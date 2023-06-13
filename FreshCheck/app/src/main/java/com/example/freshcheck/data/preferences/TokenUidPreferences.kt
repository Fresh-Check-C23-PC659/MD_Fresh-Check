package com.example.freshcheck.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenUidPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN = stringPreferencesKey("uid")

    fun getTokenUid(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN]
        }
    }

    suspend fun saveTokenUid(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun clearTokenUid() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN)
        }
    }


    companion object {
        @Volatile
        private var INSTANCE : TokenUidPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): TokenUidPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenUidPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }

}