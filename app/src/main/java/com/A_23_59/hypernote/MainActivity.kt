package com.A_23_59.hypernote

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.A_23_59.hypernote.ui.theme.NotaTheme
import com.A_23_59.hypernote.ui.theme.TagsSelectionDialog
import com.A_23_59.hypernote.ui.theme.showTagsDialog
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

class MainActivity : AppCompatActivity() {


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


        noteList.add(
            Item(
                "یادداشت شماره یک",
                "توضیحات یادداشت شماره یک",
                Priority.LOW,
                "یادداشت",
                "یادداشت های تستی",
                "اهمیت کم"
            )
        )
        noteList.add(
            Item(
                "یادداشت شماره دو",
                "توضیحات یادداشت شماره دو",
                Priority.LOW,
                "اینو مینویسم که عوض شه",
                "مهم نیست که چی باشه",
                "فقط نباید تکراری باشه"
            )
        )
        noteList.add(
            Item(
                "یادداشت شماره سه",
                "توضیحات یادداشت شماره سه",
                Priority.LOW,
                "یادداشت",
                "یادداشت های تستی",
                "اهمیت کم"
            )
        )
        noteList.add(
            Item(
                "یادداشت شماره چهار",
                "توضیحات یادداشت شماره چهار",
                Priority.LOW,
                "یادداشت",
                "یادداشت های تستی",
                "اهمیت کم"
            )
        )


        taskList.add(
            Item(
                "پرداخت شهریه دانشگاه",
                "این ماه رو باید از یکی قرض کنم",
                Priority.HIGH,
                "دانشگاه",
                "مطالعه",
                "قرض ها",
                null,
                "",
                true, hasReminder = true
            )
        )
        taskList.add(
            Item(
                "برگرداندن کتاب ها به کتابخونه دانشگاه",
                "",
                Priority.MEDIUM,
                "مطالعه",
                "کتابخونه",
                "",
                arrayOf("2", "06", "1403", "15", "26"),
                "",
                false
            )
        )
        taskList.add(
            Item(
                "تمرین صبحگاهی",
                "",
                Priority.LOW,
                "تمرین",
                "کارهای روزانه",
                "",
                arrayOf("2", "06", "1403", "15", "26"),
                this.getString(R.string.monthly),
                true
            )
        )
        taskList.add(
            Item(
                "خواندن ده صفحه کتاب روزانه",
                "حداقل تعداد صفحاتی که برای ساختن یه عادت کتابخونی لازم هست ده صفحه ست",
                Priority.HIGH,
                "کارهای روزانه",
                "مطالعه",
                "روانشناسی",
                arrayOf("2", "06", "1403", "15", "26"),
                this.getString(R.string.yearly), hasReminder = true
            )
        )
        taskList.add(
            Item(
                "تمرین انگلیسی",
                "امروز فقط روی گرامر تمرکز میکنی",
                Priority.MEDIUM,
                "کارهای روزانه",
                "انگلیسی",
                "مهاجرت",
                arrayOf("2", "06", "1403", "19", "41"),
                ""
            )
        )
        taskList.add(
            Item(
                "some coding !",
                "code practicing will increase your coding skills",
                Priority.HIGH,
                "coding",
                "",
                "",
                arrayOf("22", "11", "1420", "13", "52"),
                this.getString(R.string.daily)
            )
        )
        taskList.add(
            Item(
                "drink one glass of  water after each 1 hour",
                "you better do this to stay hydrate!",
                Priority.LOW,
                "daily routine",
                "self care",
                "hydration",
                arrayOf("2", "06", "1403", "19", "07"),
                this.getString(R.string.daily)
            )
        )
        taskList.add(
            Item(
                "task eight",
                "",
                Priority.MEDIUM,
                "this is tag 8",
                "and i also have added this one",
                "and let's test this one",
                arrayOf("2", "06", "1403", "15", "26"),
                this.getString(R.string.daily)
            )
        )
        taskList.add(
            Item(
                "task nine", "", Priority.LOW, "", "", "", arrayOf("2", "06", "1403", "15", "26"),
                this.getString(R.string.daily)
            )
        )
        taskList.add(
            Item(
                "task ten", "", Priority.HIGH, "", "", "", arrayOf("2", "06", "1403", "15", "26"),
                this.getString(R.string.daily)
            )
        )
        taskList.add(
            Item(
                "task eleven", "", Priority.LOW, "", "", "", arrayOf("2", "06", "1403", "15", "26"),
                this.getString(R.string.daily)
            )
        )
        taskList.add(
            Item(
                "task twelve",
                "",
                Priority.LOW,
                "let's see what will happen to the item when a tag length is too much",
                "",
                "",
                arrayOf("2", "06", "1403", "15", "26"),
                this.getString(R.string.daily)
            )
        )
        installSplashScreen()
        setContent {
            val currentLocale = LocaleListCompat.forLanguageTags(selectedLocale)
            AppCompatDelegate.setApplicationLocales(currentLocale)
            val navController = rememberNavController()
            NotaTheme(themeIsDark) {
                NavHost(navController = navController, startDestination = "welcome_screen") {
                    composable("main_screen") { HomePage(navController) }
                    composable(
                        "add_note_screen",
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Up,
                                tween(450)
                            )
                        }, exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Down,
                                tween(450)
                            )
                        }
                    ) {
                        AddNewItem(
                            navController = navController,
                            currentPage = currentPage
                        )
                    }
                    composable("add_task_screen", enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            tween(450)
                        )
                    }, exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            tween(450)
                        )
                    }) {
                        AddNewItem(
                            navController = navController,
                            currentPage = currentPage
                        )
                    }
                    composable("edit_note_screen") {
                        AddNewItem(
                            navController = navController,
                            currentPage = currentPage,
                            edit_or_add = 'E'
                        )
                    }
                    composable("edit_task_screen") {
                        AddNewItem(
                            navController = navController,
                            currentPage = currentPage,
                            edit_or_add = 'E'
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
                    composable("welcome_screen", exitTransition = {slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start)}) { WelcomeScreen(navController) }

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

