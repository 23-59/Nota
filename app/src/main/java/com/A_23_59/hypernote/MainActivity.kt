package com.A_23_59.hypernote

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.A_23_59.hypernote.ui.theme.Gold200
import com.A_23_59.hypernote.ui.theme.HyperNoteTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import java.text.SimpleDateFormat
import java.util.Date

var selectedLocale by mutableStateOf("fa-ir")
var currentSystemYear = 0
var currentSystemMonth = 0
var currentSystemDay = 0

fun gregorian_to_jalali(gy: Int =0, gm: Int =0, gd: Int =0): IntArray {
    val g_d_m: IntArray = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    val gy2: Int = if (gm > 2) (gy + 1) else gy
    var days: Int = 355666 + (365 * gy) + ((gy2 + 3) / 4).toInt() - ((gy2 + 99) / 100).toInt() + ((gy2 + 399) / 400).toInt() + gd + g_d_m[gm - 1]
    var jy: Int = -1595 + (33 * (days / 12053).toInt())
    days %= 12053
    jy += 4 * (days / 1461).toInt()
    days %= 1461
    if (days > 365) {
        jy += ((days - 1) / 365).toInt()
        days = (days - 1) % 365
    }
    val jm: Int; val jd: Int;
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
    var days: Int = -355668 + (365 * jy1) + ((jy1 / 33).toInt() * 8) + (((jy1 % 33) + 3) / 4).toInt() + jd + (if (jm < 7) ((jm - 1) * 31) else (((jm - 7) * 30) + 186))
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
    val sal_a: IntArray = intArrayOf(0, 31, if((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
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




        taskList.add(Item("پرداخت شهریه دانشگاه", "این ماه رو باید از یکی قرض کنم", Color.Red,"کارهای دانشگاه","مطالعه","قرض ها"))
        taskList.add(Item("تحویل دادن کتاب ها به کتابخونه", "", Gold200,"مطالعه","کتابخونه"))
        taskList.add(Item("ورزش صبحگاهی", "", Color.Blue,"ورزش","کارهای روزانه"))
        taskList.add(Item("خواندن ده صفحه کتاب روزانه", "حداقل صفحاتی که هرروز برای خوندن لازمه و میتونه تورو به کتاب خوندن عادت بده و یه روتین کتابخونی برات درست بشه ده صفحه ست ", Color.Red,"کارهای روزانه","مطالعه","روانشناسی"))
        taskList.add(Item("تمرین زبان انگلیسی", "امروز فقط روی گرامر تمرکز میکنی", Gold200,"کارهای روزانه","زبان","مهاجرت"))
        taskList.add(Item("some coding !", "code practicing will increase your coding skills", Color.Blue,"coding"))
        taskList.add(Item("drink one glass of  water after each 1 hour", "you better do this to stay hydrate!", Color.Blue,"daily routine","self care","hydration"))
        taskList.add(Item("task eight", "", Gold200))
        taskList.add(Item("task nine", "", Color.Blue))
        taskList.add(Item("task ten", "", Color.Red))
        taskList.add(Item("task eleven", "", Color.Red))
        taskList.add(Item("task twelve", "", Color.Blue))
        installSplashScreen()
        taskType = this.getString(R.string.persistent)
        setContent {

            val currentLocale = LocaleListCompat.forLanguageTags(selectedLocale)
            AppCompatDelegate.setApplicationLocales(currentLocale)

            HyperNoteTheme(themeIsDark) {
                DestinationsNavHost(navGraph = NavGraphs.root)

                AnimatedVisibility(
                    visible = showWelcomeScreen,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Log.i(TAG, "SHOW WELCOME SCREEN VALUE IS $showWelcomeScreen ")
                    WelcomeScreen()
                }
            }

        }
    }



}

