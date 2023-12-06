package com.A_23_59.hypernote.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController


private val DarkColorPalette = darkColors(
    primary = Green,
    primaryVariant = LighterGreen,
    surface = Color(0xFF1B1C1E)
)

private val LightColorPalette = lightColors(
    primary = Green,
    primaryVariant = Green

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun NotaTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme)
        DarkColorPalette
    else
        LightColorPalette

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            if (darkTheme) DarkColorPalette.surface else LightColorPalette.surface
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}