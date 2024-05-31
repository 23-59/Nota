package com.A_23_59.hypernote.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.A_23_59.hypernote.ui.theme.NotaTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date

var selectedLocale by mutableStateOf("fa-ir")
var currentSystemYear = 0
var currentSystemMonth = 0
var currentSystemDay = 0

fun gregorian_to_jalali(gy: Int = 0, gm: Int = 0, gd: Int = 0): IntArray {
    val g_d_m: IntArray = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    val gy2: Int = if (gm > 2) (gy + 1) else gy
    var days: Int =
        355666 + (365 * gy) + ((gy2 + 3) / 4).toInt() - ((gy2 + 99) / 100).toInt() + ((gy2 + 399) / 400).toInt() + gd + g_d_m[gm - 1]
    var jy: Int = -1595 + (33 * (days / 12053).toInt())
    days %= 12053
    jy += 4 * (days / 1461).toInt()
    days %= 1461
    if (days > 365) {
        jy += ((days - 1) / 365).toInt()
        days = (days - 1) % 365
    }
    val jm: Int;
    val jd: Int;
    if (days < 186) {
        jm = 1 + (days / 31).toInt()
        jd = 1 + (days % 31)
    } else {
        jm = 7 + ((days - 186) / 30).toInt()
        jd = 1 + ((days - 186) % 30)
    }
    return intArrayOf(jy, jm, jd)
}

fun jalali_to_gregorian(jy: Int, jm: Int, jd: Int): IntArray {
    val jy1: Int = jy + 1595
    var days: Int =
        -355668 + (365 * jy1) + ((jy1 / 33).toInt() * 8) + (((jy1 % 33) + 3) / 4).toInt() + jd + (if (jm < 7) ((jm - 1) * 31) else (((jm - 7) * 30) + 186))
    var gy: Int = 400 * (days / 146097).toInt()
    days %= 146097
    if (days > 36524) {
        gy += 100 * (--days / 36524).toInt()
        days %= 36524
        if (days >= 365) days++
    }
    gy += 4 * (days / 1461).toInt()
    days %= 1461
    if (days > 365) {
        gy += ((days - 1) / 365).toInt()
        days = (days - 1) % 365
    }
    var gd: Int = days + 1
    val sal_a: IntArray = intArrayOf(
        0,
        31,
        if ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) 29 else 28,
        31,
        30,
        31,
        30,
        31,
        31,
        30,
        31,
        30,
        31
    )
    var gm: Int = 0
    while (gm < 13 && gd > sal_a[gm]) gd -= sal_a[gm++]
    return intArrayOf(gy, gm, gd)
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (selectedLocale == "fa-ir") {

            val date = gregorian_to_jalali(
                SimpleDateFormat("yyyy").format(Date()).toInt(),
                SimpleDateFormat("MM").format(Date()).toInt(),
                SimpleDateFormat("dd").format(Date()).toInt()
            )
            currentSystemYear = date[0]
            currentSystemMonth = date[1]
            currentSystemDay = date[2]
        } else {
            currentSystemYear = SimpleDateFormat("yyyy").format(Date()).toInt()
            currentSystemMonth = SimpleDateFormat("MM").format(Date()).toInt()
            currentSystemDay = SimpleDateFormat("dd").format(Date()).toInt()
        }

        installSplashScreen()

        setContent {

            val currentLocale = LocaleListCompat.forLanguageTags(selectedLocale)
            AppCompatDelegate.setApplicationLocales(currentLocale)
            val navController = rememberNavController()
            Box(Modifier.safeDrawingPadding()) {
                NotaTheme(themeIsDark) {

                    ChangeSystemBarsTheme(lightTheme = !themeIsDark)

                    NavHost(navController = navController, startDestination = "welcome_screen") {
                        composable("main_screen") {
                            HomePage(navController)
                        }
                        composable(
                            "add_note_screen?id={id}",
                            arguments = listOf(navArgument("id") {
                                type = NavType.IntType
                                defaultValue = -1
                            }),
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    tween(450)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Down,
                                    tween(450)
                                )
                            }
                        ) {
                            Add_Edit_Item(
                                navController = navController,
                                currentPage = currentPage
                            )
                        }
                        composable(
                            "add_task_screen?id={id}",
                            arguments = listOf(navArgument("id") {
                                type = NavType.IntType
                                defaultValue = -1
                            }),
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    tween(450)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Down,
                                    tween(450)
                                )
                            }) {
                            Add_Edit_Item(
                                navController = navController,
                                currentPage = currentPage
                            )
                        }
                        composable("settings_screen", enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start, tween(450)
                            )
                        }, exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                tween(450)
                            )
                        }) { SettingsScreen(navController = navController) }
                        composable("welcome_screen", exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start
                            )
                        }) { WelcomeScreen(navController) }


                    }

                    if (showTagsDialog)
                        TagsSelectionDialog()
                    if (showRepeatDialog)
                        RepeatTaskDialog()
                    if (showDateAndTimeDialog)
                        ChooseDateTimeDialog()


                }
            }
        }
    }
}

@Composable
fun MainActivity.ChangeSystemBarsTheme(lightTheme: Boolean) {
    val surfaceColor = MaterialTheme.colors.surface.toArgb()
    LaunchedEffect(lightTheme) {
        if (lightTheme) {

            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(
                    surfaceColor, surfaceColor,
                ),
                navigationBarStyle = SystemBarStyle.light(
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q)
                        surfaceColor else Color.Black.toArgb(),
                    surfaceColor,
                ),
            )

        } else {

            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(
                    surfaceColor,
                ),
                navigationBarStyle = SystemBarStyle.dark(
                    surfaceColor,
                ),
            )
        }
    }
}

