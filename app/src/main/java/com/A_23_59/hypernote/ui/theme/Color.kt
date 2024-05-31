package com.A_23_59.hypernote.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Green = Color(0xFF01B44B)
val LighterGreen = Color(0xFF00FF6A)
val Purple700 = Color(0xFFAA00FF)
val Gold400 = Color(0xFFFFAB00)
val Gold200 = Color(0xFFF1B500)
val warning = Color(0xFFA83815)
val elevatedSurface = Color(0xFF272727)
val darkerBlue = Color(0xFF29489B)
val lighterBlue = Color(0xFF3F599C)
val lighterYellow = Color(0xFFB38A35)
val darkerYellow = Color(0xFF8D5E00)
val darkerRed = Color(0xFFAC2133)
val lighterRed = Color(0xFFBB3A3A)
val redGradient = Brush.verticalGradient(listOf(darkerRed, elevatedSurface.copy(0.7f)), startY = -90f)
val darkBottomBar = Brush.verticalGradient(listOf(Color(0xFF634C85), Color(0xff212121)), startY = -70f)
val lightBottomBar = Brush.verticalGradient(listOf(Color(0xFFF0F0F0), Color(0xFFB3B3B3)), startY = -10f)
val goldGradient = Brush.verticalGradient(listOf(darkerYellow, elevatedSurface.copy(0.7f)), startY = -90f )
val blueGradient = Brush.verticalGradient(listOf(darkerBlue, elevatedSurface.copy(0.7f)), startY = -90f)
val greyGradient = Brush.verticalGradient(listOf(Color(0xFF3F3F3F),Color.Gray))