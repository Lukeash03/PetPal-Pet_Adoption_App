package com.luke.petpal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class UserPreferences(val isDarkModeActive: Boolean)

private const val USER_PREFERENCE_NAME = "user_preferences"

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCE_NAME
)

class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val datastore: DataStore<Preferences> = context.datastore

    private object PreferencesKeys {
        val DARK_MODE: Preferences.Key<Boolean> = booleanPreferencesKey("dark_mode")
    }

    val userPreferencesFlow: Flow<UserPreferences> = datastore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val isDarkModeActive = preferences[PreferencesKeys.DARK_MODE] ?: false
            UserPreferences(isDarkModeActive)
        }

    suspend fun updateIsDarkModeActive(isDarkModeActive: Boolean) {
        datastore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = isDarkModeActive
        }
    }
}