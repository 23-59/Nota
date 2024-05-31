package com.A_23_59.hypernote.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.A_23_59.hypernote.R
import com.A_23_59.hypernote.ui.theme.iranYekan

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

var selectedRepeatTaskOption by mutableStateOf("")

var showRepeatDialog by mutableStateOf(false)

@SuppressLint("SuspiciousIndentation")

@Composable
fun RepeatTaskDialog() {

    val repeatOptions = listOf(
        stringResource(R.string.daily),
        stringResource(R.string.weekly), stringResource(R.string.monthly),
        stringResource(R.string.yearly)
    )

    Dialog(onDismissRequest = { showRepeatDialog = false }) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                Modifier
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val coroutine = rememberCoroutineScope()
                val calendar = Calendar.getInstance()

                repeatOptions.forEach { option ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedRepeatTaskOption = option
                                coroutine.launch {
                                    delay(300)
                                    dayFromTextField = currentSystemDay.toString()
                                    monthFromTextField = currentSystemMonth.toString()
                                    yearFromTextField = currentSystemYear.toString()
                                    hourFromTextField = calendar
                                        .get(Calendar.HOUR_OF_DAY)
                                        .toString()
                                    minuteFromTextField = calendar
                                        .get(Calendar.MINUTE)
                                        .toString()

                                    txtShowDateAndTime =
                                        " $dayFromTextField/$monthFromTextField/$yearFromTextField \n $hourFromTextField:$minuteFromTextField "
                                    dueDateHasBeenSet = true
                                    showRepeatDialog = false
                                }

                            }) {
                        Text(
                            fontFamily = iranYekan,
                            text = option, textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxWidth()
                        )

                    }
                    if (option != repeatOptions.last())
                        Divider(
                            Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface.copy(0.3f)
                        )
                }
            }
        }
    }



}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RepeatDialogPreview() {
    RepeatTaskDialog()
}