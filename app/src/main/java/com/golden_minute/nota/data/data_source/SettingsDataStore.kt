package com.golden_minute.nota.data.data_source

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SettingsDataStore(private val context : Context) {
    private val Context.dataStore by preferencesDataStore(name = "SETTINGS_DATA_STORE")


    companion object{
        private val LANGUAGE = stringPreferencesKey("fa-ir")
        private val THEME_IS_DARK = booleanPreferencesKey("dark")
        private val SHOW_WELCOME_SCREEN = booleanPreferencesKey("welcome_screen")

    }

    suspend fun showWelcomeScreen(value:Boolean){
        context.dataStore.edit { preferences ->
            preferences[SHOW_WELCOME_SCREEN] = value
        }
    }

    suspend fun saveLanguage(language : String){
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    suspend fun saveTheme(isDark : Boolean){
        context.dataStore.edit { preferences ->
            preferences[THEME_IS_DARK] = isDark
        }
    }
    val readLanguage : Flow<String>
    get() = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "fa-ir"

    }
    val readTheme : Flow<Boolean>
    get() = context.dataStore.data.map { preferences ->
        preferences[THEME_IS_DARK]?: true
    }

    val showWelcomeScreen : Flow<Boolean>
        get() = context.dataStore.data.map { preferences ->
            preferences[SHOW_WELCOME_SCREEN]?: true
        }

}