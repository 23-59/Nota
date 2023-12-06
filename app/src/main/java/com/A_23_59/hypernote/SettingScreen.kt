package com.A_23_59.hypernote

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.A_23_59.hypernote.ui.theme.NotaTheme


@Composable
fun SettingsScreen(navController: NavController) {

    val darkIsSelected by animateColorAsState(targetValue = if (themeIsDark) MaterialTheme.colors.primary else Color.Gray)
    val lightIsSelected by animateColorAsState(targetValue = if (!themeIsDark) MaterialTheme.colors.primary else Color.Gray)

    NotaTheme(darkTheme = themeIsDark) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.settings),
                            style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    },
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = if (themeIsDark) 0.dp else 10.dp,
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = if (selectedLocale == "en") Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                                tint = MaterialTheme.colors.primaryVariant,
                                contentDescription = "back"
                            )
                        }
                    })
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Text(
                        text = stringResource(R.string.txt_theme),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = lightIsSelected),
                        onClick = { themeIsDark = false },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                            .width(100.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_light),
                            color = Color.White,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = darkIsSelected),
                        onClick = { themeIsDark = true },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                            .width(100.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_dark),
                           color =  Color.White,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }


                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp), color = MaterialTheme.colors.onSurface.copy(0.3f)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Text(
                        text = stringResource(R.string.txt_language),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        onClick = {
                            selectedLocale = if (selectedLocale == "fa-ir") "en" else "fa-ir"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (selectedLocale == "fa-ir") "فارسی" else "English",
                            color = Color.White,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }


                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp), color = MaterialTheme.colors.onSurface.copy(0.3f)
                )
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}

