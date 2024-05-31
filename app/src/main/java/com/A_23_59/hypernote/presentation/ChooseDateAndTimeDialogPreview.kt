@file:Suppress("AnimatedContentLabel")

package com.A_23_59.hypernote.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.A_23_59.hypernote.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date

var todayIsSelected by mutableStateOf(false)
var tomorrowIsSelected by mutableStateOf(false)
var afterTomorrowIsSelected by mutableStateOf(false)
var yearFromTextField by mutableStateOf("")
var monthFromTextField by mutableStateOf("")
var dayFromTextField by mutableStateOf("")
var hourFromTextField by mutableStateOf("")
var minuteFromTextField by mutableStateOf("")
var predefinedIsSelected by mutableStateOf(false)
var customIsSelected by mutableStateOf(true)
var dueDateHasBeenSet by mutableStateOf(false)
var reminderIsChecked by mutableStateOf(false)
var showDateAndTimeDialog by mutableStateOf(false)

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ChooseDateTimeDialog() {

    var minuteError by remember { mutableStateOf(false) }
    var hourError by remember { mutableStateOf(false) }
    var dayError by remember { mutableStateOf(false) }
    var monthError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }
    val customTagColor by animateColorAsState(
        targetValue = if (customIsSelected) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onSurface.copy(
            0.8f
        )
    )
    val predefinedTagColor by animateColorAsState(
        targetValue = if (predefinedIsSelected) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onSurface.copy(
            0.8f
        )
    )


    val context = LocalContext.current
    var setDateAndTimeIsEnabled by remember { mutableStateOf(false) }
    setDateAndTimeIsEnabled =
        !hourError && !minuteError && !dayError && !monthError && !yearError && minuteFromTextField.isNotEmpty() && hourFromTextField.isNotEmpty() && yearFromTextField.isNotEmpty() && monthFromTextField.isNotEmpty() && dayFromTextField.isNotEmpty()

Dialog(onDismissRequest = { showDateAndTimeDialog =false }) {
    Card(modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(R.string.date),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(top = 14.dp, end = 6.dp)
                )
                myTag(
                    color = predefinedTagColor,
                    tagTitle = stringResource(R.string.predefined),
                    roundedCornerValue = 10.dp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 15.dp)
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            predefinedIsSelected = true
                            customIsSelected = false
                        }, textModifier = Modifier.padding(all = 6.dp)
                )
                myTag(
                    color = customTagColor,
                    tagTitle = stringResource(R.string.custom),
                    roundedCornerValue = 10.dp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 15.dp)
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            customIsSelected = true
                            predefinedIsSelected = false
                        }, textModifier = Modifier.padding(all = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            var expanded by remember { mutableStateOf(false) }
            val dropdownItems = listOf(
                context.getString(R.string.today),
                context.getString(R.string.tomorrow),
                context.getString(R.string.after_tomorrow)
            )
            var selectedItemDropDown by remember { mutableStateOf("") }

            AnimatedContent(
                targetState = customIsSelected,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = slideInVertically() + fadeIn(),
                        initialContentExit = fadeOut()
                    )
                }) { selected ->
                if (selected) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 12.dp)

                    ) {
                        TextField(value = dayFromTextField,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text(text = stringResource(R.string.day)) },
                            modifier = Modifier
                                .width(60.dp)
                                .weight(1f), isError = dayError,
                            onValueChange = {
                                try {
                                    if (it.length < 3) dayFromTextField = it
                                    if (dayFromTextField.isNotEmpty())
                                        dayError =
                                            dayFromTextField.toInt() > 31 || dayFromTextField.toInt() == 0
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.invalid_input),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dayFromTextField = ""
                                }

                            })
                        TextField(value = monthFromTextField,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text(text = stringResource(R.string.month)) },
                            modifier = Modifier
                                .width(60.dp)
                                .weight(1f), isError = monthError,
                            onValueChange = {
                                try {
                                    if (it.length < 3) monthFromTextField = it
                                    if (monthFromTextField.isNotEmpty())
                                        monthError =
                                            monthFromTextField.toInt() > 12 || monthFromTextField.toInt() == 0
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.invalid_input),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    monthFromTextField = ""
                                }

                            })
                        TextField(value = yearFromTextField,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text(text = stringResource(R.string.year)) },
                            modifier = Modifier
                                .width(60.dp)
                                .weight(1f), isError = yearError,
                            onValueChange = {
                                try {
                                    if (it.length < 5) {
                                        yearFromTextField = it
                                        yearError = it.length != 4
                                    } else yearError = true

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.invalid_input),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    yearFromTextField = ""
                                }

                            })
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }) {

                        TextField(
                            value = selectedItemDropDown,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            placeholder = { Text(text = stringResource(R.string.choose)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)


                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth().basicMarquee()) {
                            dropdownItems.forEach { currentItem ->
                                DropdownMenuItem(onClick = {
                                    when (currentItem) {
                                        context.getString(R.string.today) -> {
                                            dayFromTextField = currentSystemDay.toString()
                                            monthFromTextField = currentSystemMonth.toString()
                                            yearFromTextField = currentSystemYear.toString()
                                        }

                                        context.getString(R.string.tomorrow) -> {
                                            dayFromTextField =
                                                if (currentSystemDay < 30) (1 + currentSystemDay).toString()
                                                else 1.toString()
                                            monthFromTextField = currentSystemMonth.toString()
                                            yearFromTextField = currentSystemYear.toString()
                                        }

                                        context.getString(R.string.after_tomorrow) -> {
                                            if (currentSystemDay < 30)
                                                dayFromTextField = (2 + currentSystemDay).toString()
                                            else 2.toString()
                                            monthFromTextField = currentSystemMonth.toString()
                                            yearFromTextField = currentSystemYear.toString()
                                        }
                                    }
                                    selectedItemDropDown = currentItem
                                    expanded = false

                                }) {
                                    Text(text = currentItem)
                                }
                            }
                        }


                    }

                }


            }



            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.time),
                style = MaterialTheme.typography.h6
            )
            Row(
                horizontalArrangement = Arrangement.Absolute.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                TextField(
                    value = hourFromTextField,
                    placeholder = { Text(text = stringResource(R.string.hour)) },
                    onValueChange = {
                        try {
                            if (it.length < 3) hourFromTextField = it
                            if (hourFromTextField.isNotEmpty())
                                hourError =
                                    hourFromTextField.toInt() > 23 || hourFromTextField.toInt() == 0
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.invalid_input),
                                Toast.LENGTH_SHORT
                            ).show()
                            hourFromTextField = ""
                        }

                    },
                    modifier = Modifier
                        .width(60.dp)
                        .weight(1F), isError = hourError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text(text = ":", style = MaterialTheme.typography.h4)
                TextField(
                    value = minuteFromTextField,
                    placeholder = { Text(text = stringResource(R.string.minute)) },
                    onValueChange = {
                        try {
                            if (it.length < 3) minuteFromTextField = it
                            if (minuteFromTextField.isNotEmpty())
                                minuteError =
                                    minuteFromTextField.toInt() > 59 || minuteFromTextField.toInt() == 0
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.invalid_input),
                                Toast.LENGTH_SHORT
                            ).show()
                            minuteFromTextField = ""
                        }

                    }, isError = minuteError,
                    modifier = Modifier
                        .width(60.dp)
                        .weight(1F),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }



            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.bell_01), contentDescription ="reminder", tint = MaterialTheme.colors.onSurface)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(R.string.have_reminder))
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = reminderIsChecked,
                    colors = SwitchDefaults.colors(
                        Color.White,
                        MaterialTheme.colors.primary
                    ),
                    onCheckedChange = { reminderIsChecked = !reminderIsChecked })
            }

            Button(
                onClick = {
                    val simpleFormat = SimpleDateFormat("yyyy-MM-dd")
                    val currentDate: Date =
                        simpleFormat.parse("$currentSystemYear-$currentSystemMonth-$currentSystemDay") as Date
                    val enteredDate: Date =
                        simpleFormat.parse("${yearFromTextField.toInt()}-${monthFromTextField.toInt()}-${dayFromTextField.toInt()}") as Date
                    val dateIsOld = enteredDate.before(currentDate)
                    if (dateIsOld) {
                        setDateAndTimeIsEnabled = false
                        Toast.makeText(
                            context,
                            context.getString(R.string.the_date_entered_is_before_today_s_date),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        txtShowDateAndTime =
                            " ${dayFromTextField.toInt()}/${monthFromTextField.toInt()}/${yearFromTextField.toInt()} \n  ${if (selectedLocale == "fa-ir") "${minuteFromTextField.toInt()} : ${hourFromTextField.toInt()}" else "${hourFromTextField.toInt()} : ${minuteFromTextField.toInt()}"}"
                        showDateAndTimeDialog = false
                    }
                    dueDateHasBeenSet = true

                }, enabled = setDateAndTimeIsEnabled, modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)

            ) {
                Text(
                    text = stringResource(R.string.set_time_and_date),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChooseDateAndTimeDialogPreview() {
    ChooseDateTimeDialog()
}