package com.A_23_59.hypernote

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.A_23_59.hypernote.ui.theme.HyperNoteTheme
import com.A_23_59.hypernote.ui.theme.iranYekan
import com.A_23_59.hypernote.ui.theme.pacifico
import kotlinx.coroutines.Dispatchers.IO

var persianBorder by mutableStateOf(3.dp)
var englishBorder by mutableStateOf(0.dp)
var lightTheme by mutableStateOf(0.dp)
var darkTheme by mutableStateOf(3.dp)
var themeIsDark by mutableStateOf(true)

 const val TAG = "TAGS"

@Composable
fun WelcomeScreen() {

    Log.i(TAG, "CHANGING LANGUAGE TO $selectedLocale")
    Log.i(TAG, "SHOW WELCOME SCREEN TO $showWelcomeScreen")
    Log.i(TAG, "CHANGING THEME $themeIsDark")


    HyperNoteTheme(darkTheme = themeIsDark) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ConstraintLayout(Modifier.fillMaxSize()) {

                val (txtTitlePosition, txtChooseLanguagePosition,
                    persianPosition, confirmButtonPosition,
                    englishPosition, txtChooseThemePosition, darkThemePosition,
                    lightThemePosition, appLogoPosition) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.hyper_note),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .constrainAs(appLogoPosition) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top, 16.dp)
                        })

                Text(
                    text = stringResource(R.string.txt_welcome),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h4, fontFamily = if (selectedLocale=="fa-ir") iranYekan else pacifico,
                    modifier = Modifier.constrainAs(txtTitlePosition) {
                        width = Dimension.fillToConstraints
                        top.linkTo(appLogoPosition.bottom, 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    })
                Text(
                    text = stringResource(R.string.txt_language),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(txtChooseLanguagePosition) {
                        width = Dimension.fillToConstraints
                        top.linkTo(txtTitlePosition.bottom, 32.dp)
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(parent.end, 16.dp)

                    })
                Card(
                    Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            persianBorder = 0.dp
                            englishBorder = 3.dp
                            selectedLocale = "en"
                        }
                        .constrainAs(englishPosition) {
                            height = Dimension.value(80.dp)
                            width = Dimension.value(80.dp)
                            top.linkTo(txtChooseLanguagePosition.bottom, 24.dp)
                        }, border = BorderStroke(
                        width = englishBorder,
                        if (englishBorder != 0.dp) MaterialTheme.colors.primary
                        else MaterialTheme.colors.surface
                    ), shape = RoundedCornerShape(12.dp), elevation = 10.dp
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "English",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            englishBorder = 0.dp
                            persianBorder = 3.dp
                            selectedLocale = "fa-ir"
                        }
                        .constrainAs(persianPosition) {
                            height = Dimension.value(80.dp)
                            width = Dimension.value(80.dp)
                            top.linkTo(englishPosition.top)
                            bottom.linkTo(englishPosition.bottom)
                        },
                    elevation = 10.dp, shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        persianBorder,
                        if (persianBorder != 0.dp) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                    )
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "فارسی",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                createHorizontalChain(
                    englishPosition,
                    persianPosition,
                    chainStyle = ChainStyle.Spread
                )

                Text(
                    text = stringResource(R.string.txt_theme), textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .constrainAs(txtChooseThemePosition) {
                            width = Dimension.fillToConstraints
                            top.linkTo(englishPosition.bottom, 50.dp)
                            start.linkTo(txtChooseLanguagePosition.start)
                            end.linkTo(parent.end, 16.dp)

                        })

                Card(
                    Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            lightTheme = 3.dp
                            darkTheme = 0.dp
                            themeIsDark = false
                        }
                        .constrainAs(lightThemePosition) {
                            height = Dimension.value(80.dp)
                            width = Dimension.value(80.dp)
                            top.linkTo(txtChooseThemePosition.bottom, 16.dp)
                            start.linkTo(txtChooseLanguagePosition.start, 8.dp)
                        },
                    elevation = 10.dp,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        lightTheme,
                        if (lightTheme != 0.dp) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.txt_light),
                             style = MaterialTheme.typography.body2
                        )
                    }
                }

                Card(
                    Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            darkTheme = 3.dp
                            lightTheme = 0.dp
                            themeIsDark = true
                        }
                        .constrainAs(darkThemePosition) {
                            height = Dimension.value(80.dp)
                            width = Dimension.value(80.dp)
                            top.linkTo(txtChooseThemePosition.bottom, 16.dp)
                        },
                    elevation = 10.dp,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        darkTheme,
                        if (darkTheme != 0.dp) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.txt_dark),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

                createHorizontalChain(
                    lightThemePosition,
                    darkThemePosition,
                    chainStyle = ChainStyle.Spread
                )

                Button(
                    onClick = { showWelcomeScreen = false },
                    modifier = Modifier
                        .shadow(
                            spotColor = MaterialTheme.colors.primary,
                            elevation = 12.dp,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .constrainAs(confirmButtonPosition) {
                            width = Dimension.fillToConstraints
                            start.linkTo(parent.start, 32.dp)
                            end.linkTo(parent.end, 32.dp)
                            bottom.linkTo(parent.bottom, 24.dp)
                        }) {
                    Text(
                        text = stringResource(R.string.txt_confirm),
                        style = MaterialTheme.typography.h6, fontFamily =  iranYekan, color = Color.White, fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun WelcomePagePreview() {

}