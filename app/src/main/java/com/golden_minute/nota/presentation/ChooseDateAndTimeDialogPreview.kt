@file:Suppress("AnimatedContentLabel")

package com.golden_minute.nota.presentation

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
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.golden_minute.nota.R
import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.domain.util.Add_Edit_Events
import com.golden_minute.nota.domain.util.ChooseDateTimeDialogEvents
import com.golden_minute.nota.domain.util.leadingZero
import com.golden_minute.nota.ui.theme.LighterGreen
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date


@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ChooseDateTimeDialog(
    activityViewModel: MainActivityViewModel,
    addScreenViewModel: Add_EditScreenViewModel
) {


    var dayError by remember { mutableStateOf(false) }
    var monthError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }
    val customTagColor by animateColorAsState(
        targetValue = if (!addScreenViewModel.dialogState.value.dateTypeIsPredefined) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onSurface.copy(
            0.8f
        )
    )
    val predefinedTagColor by animateColorAsState(
        targetValue = if (addScreenViewModel.dialogState.value.dateTypeIsPredefined) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onSurface.copy(
            0.8f
        )
    )


    val context = LocalContext.current

    Dialog(onDismissRequest = {
        addScreenViewModel.dialogEvents(ChooseDateTimeDialogEvents.ClearAllValues)
        addScreenViewModel.dialogEvents(
            ChooseDateTimeDialogEvents.ShowDateAndTimeDialog(false)
        )
    }) {
        Card(
            shape = RoundedCornerShape(15.dp)
        ) {
            if (addScreenViewModel.dialogState.value.showTimePickerDialog)
                TimePickerDialog(addScreenViewModel)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
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
                                addScreenViewModel.dialogEvents(
                                    ChooseDateTimeDialogEvents.EnteredDateType(
                                        true
                                    )
                                )
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
                                addScreenViewModel.dialogEvents(
                                    ChooseDateTimeDialogEvents.EnteredDateType(
                                        false
                                    )
                                )
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
                    targetState = !addScreenViewModel.dialogState.value.dateTypeIsPredefined,
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
                            TextField(value = addScreenViewModel.dialogState.value.dayFromTextField,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(text = stringResource(R.string.day)) },
                                modifier = Modifier
                                    .width(60.dp)
                                    .weight(1f), isError = dayError,
                                onValueChange = {
                                    try {
                                        if (it.length < 3)
                                            addScreenViewModel.dialogEvents(
                                                ChooseDateTimeDialogEvents.EnteredDay(it)
                                            )
                                        if (addScreenViewModel.dialogState.value.dayFromTextField.isNotEmpty())
                                            dayError =
                                                addScreenViewModel.dialogState.value.dayFromTextField.toInt() > 31 || addScreenViewModel.dialogState.value.dayFromTextField.toInt() == 0
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.invalid_input),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        addScreenViewModel.dialogEvents(
                                            ChooseDateTimeDialogEvents.EnteredDay(
                                                ""
                                            )
                                        )
                                    }

                                })
                            TextField(value = addScreenViewModel.dialogState.value.monthFromTextField,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(text = stringResource(R.string.month)) },
                                modifier = Modifier
                                    .width(60.dp)
                                    .weight(1f), isError = monthError,
                                onValueChange = {
                                    try {
                                        if (it.length < 3)
                                            addScreenViewModel.dialogEvents(
                                                ChooseDateTimeDialogEvents.EnteredMonth(it)
                                            )
                                        if (addScreenViewModel.dialogState.value.monthFromTextField.isNotEmpty())
                                            monthError =
                                                addScreenViewModel.dialogState.value.monthFromTextField.toInt() > 12 || addScreenViewModel.dialogState.value.monthFromTextField.toInt() == 0
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.invalid_input),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        addScreenViewModel.dialogEvents(
                                            ChooseDateTimeDialogEvents.EnteredMonth(
                                                ""
                                            )
                                        )
                                    }

                                })
                            TextField(value = addScreenViewModel.dialogState.value.yearFromTextField,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(text = stringResource(R.string.year)) },
                                modifier = Modifier
                                    .width(60.dp)
                                    .weight(1f), isError = yearError,
                                onValueChange = {
                                    try {
                                        if (it.length < 5) {
                                            addScreenViewModel.dialogEvents(
                                                ChooseDateTimeDialogEvents.EnteredYear(it)
                                            )
                                            yearError = it.length != 4
                                        } else yearError = true

                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.invalid_input),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        addScreenViewModel.dialogEvents(
                                            ChooseDateTimeDialogEvents.EnteredYear(
                                                ""
                                            )
                                        )
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
                                onDismissRequest = { expanded = false }, modifier = Modifier
                                    .width(288.dp)
                                    .basicMarquee()
                            ) {
                                dropdownItems.forEach { currentItem ->
                                    DropdownMenuItem(onClick = {
                                        when (currentItem) {
                                            context.getString(R.string.today) -> {
                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredDay(
                                                        currentSystemDay.toString()
                                                    )
                                                )
                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredMonth(
                                                        currentSystemMonth.toString()
                                                    )
                                                )
                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredYear(
                                                        currentSystemYear.toString()
                                                    )
                                                )
                                            }

                                            context.getString(R.string.tomorrow) -> {
                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredDay(
                                                        if (currentSystemDay < 30) (1 + currentSystemDay).toString()
                                                        else 1.toString()
                                                    )
                                                )

                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredMonth(
                                                        currentSystemMonth.toString()
                                                    )
                                                )
                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredYear(
                                                        currentSystemYear.toString()
                                                    )
                                                )
                                            }

                                            context.getString(R.string.after_tomorrow) -> {
                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredDay(
                                                        if (currentSystemDay < 30)
                                                            (2 + currentSystemDay).toString()
                                                        else 2.toString()
                                                    )
                                                )

                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredMonth(
                                                        currentSystemMonth.toString()
                                                    )
                                                )
                                                addScreenViewModel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.EnteredYear(
                                                        currentSystemYear.toString()
                                                    )
                                                )
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
                    text = stringResource(id = R.string.time),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )


                OutlinedButton(
                    onClick = {
                        addScreenViewModel.dialogEvents(
                            ChooseDateTimeDialogEvents.TimeDialogVisibility(
                                true
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.choose_time),
                        style = MaterialTheme.typography.body1
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.bell_01),
                        contentDescription = "reminder",
                        tint = MaterialTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = stringResource(R.string.have_reminder))
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = addScreenViewModel.dialogState.value.reminderIsChecked,
                        colors = SwitchDefaults.colors(
                            Color.White,
                            MaterialTheme.colors.primary
                        ),
                        onCheckedChange = {
                            addScreenViewModel.dialogEvents(
                                ChooseDateTimeDialogEvents.ReminderStatusIsChanged(!addScreenViewModel.dialogState.value.reminderIsChecked)
                            )
                        })
                }



                Button(
                    enabled = addScreenViewModel.dialogState.value.dayFromTextField.isNotBlank() && addScreenViewModel.dialogState.value.monthFromTextField.isNotBlank() && addScreenViewModel.dialogState.value.yearFromTextField.isNotBlank() && addScreenViewModel.dialogState.value.hourValue.isNotBlank() && addScreenViewModel.dialogState.value.minuteValue.isNotBlank(),
                    onClick = {
                        val simpleFormat = SimpleDateFormat("yyyy-MM-dd")
                        val currentDate: Date =
                            simpleFormat.parse("$currentSystemYear-$currentSystemMonth-$currentSystemDay") as Date

                        val enteredDate =
                            simpleFormat.parse(
                                "${addScreenViewModel.dialogState.value.yearFromTextField}-${
                                    leadingZero(
                                        addScreenViewModel.dialogState.value.monthFromTextField
                                    )
                                }-${addScreenViewModel.dialogState.value.dayFromTextField}"
                            ) as Date

                        val dateIsOld = enteredDate.before(currentDate)
                        if (dateIsOld) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.the_date_entered_is_before_today_s_date),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            addScreenViewModel.dialogEvents(
                                ChooseDateTimeDialogEvents.ShowDateAndTimeDialog(
                                    false
                                )
                            )
                        }
                        addScreenViewModel.dialogEvents(
                            ChooseDateTimeDialogEvents.ToggleDueDate(
                                true
                            )
                        )
                        addScreenViewModel.onEvent(
                            Add_Edit_Events.SaveDueDate(
                                if (activityViewModel.languageData.value == "en") {
                                    LocalDateTime.parse(
                                        "${addScreenViewModel.dialogState.value.yearFromTextField}-${
                                            leadingZero(
                                                addScreenViewModel.dialogState.value.monthFromTextField
                                            )
                                        }-${leadingZero(addScreenViewModel.dialogState.value.dayFromTextField)}T${
                                            leadingZero(
                                                addScreenViewModel.dialogState.value.hourValue
                                            )
                                        }:${leadingZero(addScreenViewModel.dialogState.value.minuteValue)}:00",
                                        Task.timeFormat
                                    )
                                } else {
                                    val convertedDate = jalali_to_gregorian(
                                        addScreenViewModel.dialogState.value.yearFromTextField.toInt(),
                                        addScreenViewModel.dialogState.value.monthFromTextField.toInt(),
                                        addScreenViewModel.dialogState.value.dayFromTextField.toInt()
                                    )
                                    LocalDateTime.parse(
                                        "${convertedDate[0]}-${
                                            leadingZero(convertedDate[1].toString())
                                        }-${
                                            leadingZero(convertedDate[2].toString())
                                        }T${leadingZero(addScreenViewModel.dialogState.value.hourValue)}:${
                                            leadingZero(addScreenViewModel.dialogState.value.minuteValue)
                                        }:00",
                                        Task.timeFormat
                                    )
                                }

                            )
                        )


                    }, modifier = Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(addScreenViewModel: Add_EditScreenViewModel) {

    val calendar = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    Dialog(onDismissRequest = {
        addScreenViewModel.dialogEvents(
            ChooseDateTimeDialogEvents.TimeDialogVisibility(
                false
            )
        )
    },
        content = {
            Card(shape = RoundedCornerShape(12.dp)) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = stringResource(R.string.choose_time),
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    CompositionLocalProvider(value = LocalLayoutDirection provides LayoutDirection.Ltr) { // this piece of code will force the child composable to be always in LTR
                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                selectorColor = MaterialTheme.colors.primary,
                                timeSelectorSelectedContainerColor = MaterialTheme.colors.primary,
                                timeSelectorSelectedContentColor = Color.White,
                                periodSelectorBorderColor = MaterialTheme.colors.primary

                            )
                        )
                    }
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(onClick = {
                            addScreenViewModel.dialogEvents(
                                ChooseDateTimeDialogEvents.TimeDialogVisibility(false)
                            )
                        }) {
                            Text(
                                text = stringResource(id = R.string.dismiss), color = LighterGreen
                            )
                        }
                        TextButton(onClick = {
                            addScreenViewModel.dialogEvents(
                                ChooseDateTimeDialogEvents.EnteredHour(
                                    timePickerState.hour.toString()
                                )
                            )
                            addScreenViewModel.dialogEvents(
                                ChooseDateTimeDialogEvents.EnteredMinute(
                                    timePickerState.minute.toString()
                                )
                            )
                            addScreenViewModel.dialogEvents(
                                ChooseDateTimeDialogEvents.TimeDialogVisibility(
                                    false
                                )
                            )
                        }) {
                            Text(
                                text = stringResource(id = R.string.OK), color = LighterGreen
                            )
                        }
                    }
                }


            }

        })
}






