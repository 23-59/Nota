package com.A_23_59.hypernote.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.A_23_59.hypernote.R




val iranYekan = FontFamily(Font(R.font.iran_yekan_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.iran_yekan_bold, weight = FontWeight.Bold)
)

val pacifico = FontFamily(Font(R.font.pacifico_regular, weight = FontWeight.Normal))

// Set of Material typography styles to start with
val Typography = Typography( defaultFontFamily = iranYekan
    /* Other default text styles to override
    ,
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)