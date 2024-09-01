package com.golden_minute.nota.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golden_minute.nota.data.data_source.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(settingsDataStore: SettingsDataStore)
    :ViewModel() {
    private val _showSplashScreen = MutableStateFlow(true)
    val showSplashScreen = _showSplashScreen.asStateFlow()

    private var _startPage = MutableStateFlow("welcome_screen")
    val startPage = _startPage.asStateFlow()

    private var _languageData = mutableStateOf("fa-ir")
    val languageData: State<String> = _languageData

    private var _themeData = mutableStateOf(true)
    val themeData : State<Boolean> = _themeData

    init {
        viewModelScope.launch {
            launch {
                settingsDataStore.showWelcomeScreen.collect { showWelcomeScreen ->
                    _startPage.value = if (!showWelcomeScreen) "main_screen" else "welcome_screen"
                    delay(300)
                    _showSplashScreen.value = false
                }

            }

            launch {
                settingsDataStore.readLanguage.collect { language ->
                    _languageData.value = language

                }
            }
            launch {
                settingsDataStore.readTheme.collect{ theme ->
                    _themeData.value = theme
                }
            }



        }
    }

}