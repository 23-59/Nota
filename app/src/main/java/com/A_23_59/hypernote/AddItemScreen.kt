package com.A_23_59.hypernote


import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.DatePicker
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.A_23_59.hypernote.destinations.ChooseDateTimeDialogDestination
import com.A_23_59.hypernote.ui.theme.*
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


var title by mutableStateOf("")
var description by mutableStateOf("")

var blueIsSelected by mutableStateOf(true)
var yellowIsSelected by mutableStateOf(false)
var redIsSelected by mutableStateOf(false)
var taskType by mutableStateOf("")
var txtShowDateAndTime by mutableStateOf("")
var tagNumber1 by mutableStateOf("مطالعه")
var tagNumber2 by mutableStateOf("ورزش")
var tagNumber3 by mutableStateOf("کارهای روزانه")
var tagNumber by mutableStateOf(
    if (tagNumber1.isNotEmpty() && tagNumber2.isNotEmpty() && tagNumber3.isNotEmpty())
        3
    else if (tagNumber1.isNotEmpty() && tagNumber2.isNotEmpty())
        2
    else if (tagNumber1.isNotEmpty())
        1
    else
        0
)


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddNewItem(
    task_or_note: Char = 'T',
    edit_or_add: Char = 'A',
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    txtShowDateAndTime = stringResource(id = R.string.due_date)


    val confirmButtonText =
        if (task_or_note == 'T' && edit_or_add == 'A') stringResource(R.string.add_to_the_tasks)
        else if (task_or_note == 'N' && edit_or_add == 'A') stringResource(R.string.add_to_the_notes)
        else if (task_or_note == 'T' && edit_or_add == 'E') stringResource(R.string.edit_the_task)
        else stringResource(R.string.edit_the_note)

    val topAppBarText =
        if (task_or_note == 'T' && edit_or_add == 'A') stringResource(R.string.adding_task)
        else if (task_or_note == 'N' && edit_or_add == 'A') stringResource(R.string.adding_note)
        else if (task_or_note == 'T' && edit_or_add == 'E') stringResource(R.string.editing_task)
        else stringResource(R.string.editing_note)

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .verticalScroll(rememberScrollState())
    ) {
        val (titlePosition, priorityPosition, lowerColumnPosition, descriptionPosition, persistentItemsPosition, priorityTextPosition, taskTypeTextPosition, taskTypePosition, topBarPosition, tagsTextPosition) = createRefs()



        TopAppBar(title = {
            Text(
                text = topAppBarText,
                style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primaryVariant
            )
        }, backgroundColor = MaterialTheme.colors.surface, elevation = 0.dp, navigationIcon = {
            IconButton(onClick = { navigator.navigateUp() }) {
                Icon(
                    imageVector = if (selectedLocale == "en") Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                    tint = MaterialTheme.colors.primaryVariant,
                    contentDescription = "back"
                )
            }
        }, modifier = Modifier.constrainAs(topBarPosition) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        })

        OutlinedTextField(
            value = title,
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                backgroundColor = if (themeIsDark) elevatedSurface else Color.Black.copy(0.12f)
            ),
            onValueChange = { title = it },
            label = {
                Text(
                    text = stringResource(
                        R.string.text_field_title
                    )
                )
            },
            modifier = Modifier.constrainAs(titlePosition) {
                top.linkTo(topBarPosition.bottom, 16.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
            })

        OutlinedTextField(
            value = description,
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                backgroundColor = if (themeIsDark) elevatedSurface else Color.Black.copy(0.12f)
            ),
            onValueChange = { description = it },
            label = {
                Text(
                    text = if (task_or_note == 'T') stringResource(R.string.textfield_description_optional) else stringResource(
                        id = R.string.textfield_description
                    )
                )
            },
            modifier = Modifier.constrainAs(descriptionPosition) {
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
                if (task_or_note == 'T')
                    top.linkTo(titlePosition.bottom, 32.dp)
                 else {
                     top.linkTo(titlePosition.bottom,16.dp)
                    bottom.linkTo(tagsTextPosition.top)
                    height = Dimension.fillToConstraints
                }

            })


        if (task_or_note == 'T') {
            Text(
                text = stringResource(R.string.level_of_importance),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.constrainAs(priorityTextPosition) {
                    top.linkTo(descriptionPosition.bottom, 24.dp)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                })

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.constrainAs(priorityPosition) {
                    width = Dimension.fillToConstraints
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(priorityTextPosition.bottom, 24.dp)

                }) {

                val redWeight by animateFloatAsState(targetValue = if (redIsSelected) 2f else 1f)
                val yellowWeight by animateFloatAsState(targetValue = if (yellowIsSelected) 2f else 1f)
                val blueWeight by animateFloatAsState(targetValue = if (blueIsSelected) 2f else 1f)
                val redBorder by animateDpAsState(targetValue = if (redIsSelected) 4.dp else 1.dp)
                val yellowBorder by animateDpAsState(targetValue = if (yellowIsSelected) 4.dp else 0.dp)
                val blueBorder by animateDpAsState(targetValue = if (blueIsSelected) 4.dp else 0.dp)


                val blueSize by animateDpAsState(targetValue = if (blueIsSelected) 75.dp else 45.dp)
                Box(
                    modifier = Modifier
                        .shadow(
                            20.dp,
                            RoundedCornerShape(15.dp),
                            spotColor = if (themeIsDark && blueIsSelected) Color.White else Color.Black
                        )
                        .border(
                            width = blueBorder,
                            color = if (blueIsSelected) MaterialTheme.colors.onSurface else Color.Transparent,
                            RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .height(blueSize)
                        .background(
                            Brush.verticalGradient(listOf(lighterBlue, darkerBlue), startY = -10f)
                        )
                        .weight(blueWeight)
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ) {
                            blueIsSelected = true
                            yellowIsSelected = false
                            redIsSelected = false
                        },
                ) {
                    AnimatedContent(
                        targetState = blueIsSelected,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        if (it)
                            Text(
                                text = stringResource(id = R.string.low),
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        else
                            Text(
                                text = stringResource(id = R.string.low),
                                style = MaterialTheme.typography.body1,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                    }
                }


                val yellowSize by animateDpAsState(targetValue = if (yellowIsSelected) 75.dp else 45.dp)
                Box(
                    modifier = Modifier
                        .shadow(
                            20.dp, RoundedCornerShape(15.dp),
                            spotColor = if (themeIsDark && yellowIsSelected) Color.White else Color.Black
                        )
                        .border(
                            yellowBorder,
                            if (yellowIsSelected) MaterialTheme.colors.onSurface else Color.Transparent,
                            RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(lighterYellow, darkerYellow),
                                startY = -10f
                            )
                        )
                        .weight(yellowWeight)
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ) {
                            yellowIsSelected = true
                            redIsSelected = false
                            blueIsSelected = false
                        }
                        .size(yellowSize),
                ) {
                    AnimatedContent(
                        targetState = yellowIsSelected,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        if (it)
                            Text(
                                text = stringResource(id = R.string.medium),
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        else
                            Text(
                                text = stringResource(id = R.string.medium),
                                style = MaterialTheme.typography.body1,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                    }

                }


                val redSize by animateDpAsState(targetValue = if (redIsSelected) 75.dp else 45.dp)

                Box(
                    modifier = Modifier
                        .shadow(
                            20.dp, RoundedCornerShape(15.dp),
                            spotColor = if (themeIsDark && redIsSelected) Color.White else Color.Black
                        )
                        .border(
                            redBorder,
                            if (redIsSelected) MaterialTheme.colors.onSurface else Color.Transparent,
                            RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .weight(redWeight)
                        .background(
                            Brush.verticalGradient(
                                listOf(lighterRed, darkerRed),
                                startY = -15f
                            )
                        )
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ) {
                            redIsSelected = true
                            yellowIsSelected = false
                            blueIsSelected = false
                        }
                        .height(redSize),
                ) {
                    AnimatedContent(
                        targetState = redIsSelected,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        if (it)
                            Text(
                                text = stringResource(id = R.string.high),
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        else
                            Text(
                                text = stringResource(id = R.string.high),
                                style = MaterialTheme.typography.body1,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )

                    }
                }
            }



            Text(
                text = stringResource(R.string.task_type),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.constrainAs(taskTypeTextPosition) {
                    top.linkTo(priorityTextPosition.bottom, 130.dp)
                    start.linkTo(parent.start, 16.dp)
                })
            val options =
                listOf(stringResource(R.string.temporary), stringResource(R.string.persistent))


            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.constrainAs(taskTypePosition) {
                    width = Dimension.fillToConstraints
                    start.linkTo(taskTypeTextPosition.start)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(taskTypeTextPosition.bottom, 24.dp)
                }) {
                options.forEach { option ->

                    val backgroundColor: Color by animateColorAsState(targetValue = if (taskType == option) MaterialTheme.colors.primary else Color.Gray)
                    val contentColor: Color by animateColorAsState(
                        targetValue = if (taskType == option) Color.White else Color(
                            0xFFE6E6E6
                        )
                    )
                    var selectedDay by remember{mutableStateOf("")}


                    Button(
                        onClick = {
                            taskType = option
                            if (option == context.getString(R.string.persistent)) {
                                resetTemporary(context)
                            }
                        }, modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = backgroundColor,
                            contentColor = MaterialTheme.colors.onSurface
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                fontSize = 17.sp,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                painter = painterResource(id = if (option == stringResource(id = R.string.persistent)) R.drawable.round_autorenew_24 else R.drawable.round_timelapse_30),
                                contentDescription = "task type",
                                tint = contentColor
                            )

                        }
                    }
                }
            }


            Column(
                Modifier
                    .constrainAs(lowerColumnPosition) {
                        start.linkTo(taskTypePosition.start)
                        end.linkTo(taskTypePosition.end)
                        top.linkTo(taskTypePosition.bottom, 16.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints


                    }
                    .fillMaxWidth(), horizontalAlignment = Alignment.Start)
            {

                    Column(Modifier.padding(vertical = 16.dp)) {

                        Text(
                            text = txtShowDateAndTime,
                            style = MaterialTheme.typography.h6,
                            fontFamily = if (selectedLocale == "fa-ir") iranYekanFarsiNamerals else iranYekan,
                            color = MaterialTheme.colors.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { navigator.navigate(ChooseDateTimeDialogDestination) },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = stringResource(R.string.choose_date_and_time),
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.primaryVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }


                    }

                if (taskType == context.getString(R.string.temporary)) {
                    Text(
                        text = stringResource(R.string.tags),
                        color = MaterialTheme.colors.onSurface,
                        style = MaterialTheme.typography.h6
                    )
                } else {
                    Text(
                        text = stringResource(R.string.tags),
                        color = MaterialTheme.colors.onSurface,
                        style = MaterialTheme.typography.h6
                    )

                }
                Text(
                    text = stringResource(R.string.up_to_three_tags),
                    color = Gold200,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
                )

                var tagValue by remember { mutableStateOf("") }

                var tagError by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = tagValue, enabled = tagNumber != 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onValueChange = {

                        if (!it.startsWith(" ")) {
                            tagValue = it
                            tagError = false
                        }


//                        if (it.length < 30) { this code snippet make TextField's color blinking when length is more than 30
//                            tagError = false
//                            tagValue = it
//                        } else tagError = true
                    }, isError = tagError,
                    trailingIcon = {
                        IconButton(enabled = tagNumber != 3 && tagValue.isNotBlank(),
                            onClick = {


                                if (tagValue.isNotBlank())
                                    tagNumber++
                                else tagError = true

                                when (tagNumber) {
                                    1 -> if (tagValue.isNotEmpty()) tagNumber1 = tagValue
                                    2 -> if (tagValue.isNotEmpty()) tagNumber2 = tagValue
                                    3 -> if (tagValue.isNotEmpty()) tagNumber3 = tagValue
                                }
                                tagValue = ""

                            }) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    },
                    placeholder = { Text(text = stringResource(R.string.tag_name)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = if (themeIsDark) elevatedSurface else Color.Black.copy(
                            0.12f
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (tagNumber1.isEmpty()) 24.dp else 0.dp)
                )
                AnimatedVisibility(visible = tagNumber1.isNotEmpty() || tagNumber2.isNotEmpty() || tagNumber3.isNotEmpty()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            AnimatedVisibility(
                                visible = tagNumber1.isNotEmpty() && tagNumber1.length < 25,
                                exit = fadeOut()
                            ) {
                                MyTag(
                                    tagNumber1,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp, deletable = true
                                ) {
                                    tagNumber--
                                    tagNumber1 = ""
                                    return@MyTag Unit
                                }
                            }
                            AnimatedVisibility(
                                visible = tagNumber2.isNotEmpty() && tagNumber2.length < 16,
                                exit = fadeOut()
                            ) {
                                MyTag(
                                    tagNumber2,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp, deletable = true
                                ) {
                                    tagNumber--
                                    tagNumber2 = ""
                                    return@MyTag Unit
                                }
                            }

                            AnimatedVisibility(
                                visible = tagNumber3.isNotEmpty() && tagNumber3.length < 13,
                                exit = fadeOut()
                            ) {
                                MyTag(
                                    tagNumber3,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp, deletable = true
                                ) {
                                    tagNumber--
                                    tagNumber3 = ""
                                    return@MyTag Unit
                                }
                            }

                        }
                        AnimatedVisibility(
                            visible = tagNumber1.isNotEmpty() && tagNumber1.length >= 25,
                            exit = fadeOut()
                        ) {
                            MyTag(
                                tagNumber1,
                                MaterialTheme.colors.onSurface,
                                borderStroke = 1.dp,
                                deletable = true
                            ) {
                                tagNumber--
                                tagNumber1 = ""
                                return@MyTag Unit
                            }

                        }

                        AnimatedVisibility(visible = tagNumber2.length >= 16, exit = fadeOut()) {
                            MyTag(
                                tagNumber2,
                                MaterialTheme.colors.onSurface,
                                borderStroke = 1.dp,
                                deletable = true
                            ) {
                                tagNumber--
                                tagNumber2 = ""
                                return@MyTag Unit
                            }

                        }

                        AnimatedVisibility(visible = tagNumber3.length >= 13, exit = fadeOut()) {
                            MyTag(
                                tagNumber3,
                                MaterialTheme.colors.onSurface,
                                borderStroke = 1.dp,
                                deletable = true
                            ) {
                                tagNumber--
                                tagNumber3 = ""
                                return@MyTag Unit
                            }
                        }


                    }
                }


                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Button(
                    onClick = { /*TODO*/ }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = confirmButtonText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        } else {

            Text(
                text = stringResource(R.string.tags),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.constrainAs(tagsTextPosition) {
                    top.linkTo(descriptionPosition.bottom,16.dp)
                    start.linkTo(descriptionPosition.start)


                })
            Column(
                Modifier
                    .constrainAs(lowerColumnPosition) {
                        start.linkTo(tagsTextPosition.start)
                        end.linkTo(descriptionPosition.end)
                        top.linkTo(tagsTextPosition.bottom, 12.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints


                    }
                    .fillMaxWidth(), horizontalAlignment = Alignment.Start)
            {


                Text(
                    text = stringResource(R.string.up_to_three_tags),
                    color = Gold200,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding( bottom = 16.dp)
                )

                var tagValue by remember { mutableStateOf("") }

                var tagError by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = tagValue, enabled = tagNumber != 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onValueChange = {

                        if (!it.startsWith(" ")) {
                            tagValue = it
                            tagError = false
                        }


//                        if (it.length < 30) { this code snippet make TextField's color blinking when length is more than 30
//                            tagError = false
//                            tagValue = it
//                        } else tagError = true
                    }, isError = tagError,
                    trailingIcon = {
                        IconButton(enabled = tagNumber != 3 && tagValue.isNotBlank(),
                            onClick = {


                                if (tagValue.isNotBlank())
                                    tagNumber++
                                else tagError = true

                                when (tagNumber) {
                                    1 -> if (tagValue.isNotEmpty()) tagNumber1 = tagValue
                                    2 -> if (tagValue.isNotEmpty()) tagNumber2 = tagValue
                                    3 -> if (tagValue.isNotEmpty()) tagNumber3 = tagValue
                                }
                                tagValue = ""

                            }) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    },
                    placeholder = { Text(text = stringResource(R.string.tag_name)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = if (themeIsDark) elevatedSurface else Color.Black.copy(
                            0.12f
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (tagNumber1.isEmpty()) 24.dp else 0.dp)
                )
                AnimatedVisibility(visible = tagNumber1.isNotEmpty() || tagNumber2.isNotEmpty() || tagNumber3.isNotEmpty()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            AnimatedVisibility(
                                visible = tagNumber1.isNotEmpty() && tagNumber1.length < 25,
                                exit = fadeOut()
                            ) {
                                MyTag(
                                    tagNumber1,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp, deletable = true
                                ) {
                                    tagNumber--
                                    tagNumber1 = ""
                                    return@MyTag Unit
                                }
                            }
                            AnimatedVisibility(
                                visible = tagNumber2.isNotEmpty() && tagNumber2.length < 16,
                                exit = fadeOut()
                            ) {
                                MyTag(
                                    tagNumber2,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp, deletable = true
                                ) {
                                    tagNumber--
                                    tagNumber2 = ""
                                    return@MyTag Unit
                                }
                            }

                            AnimatedVisibility(
                                visible = tagNumber3.isNotEmpty() && tagNumber3.length < 13,
                                exit = fadeOut()
                            ) {
                                MyTag(
                                    tagNumber3,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp, deletable = true
                                ) {
                                    tagNumber--
                                    tagNumber3 = ""
                                    return@MyTag Unit
                                }
                            }

                        }
                        AnimatedVisibility(
                            visible = tagNumber1.isNotEmpty() && tagNumber1.length >= 25,
                            exit = fadeOut()
                        ) {
                            MyTag(
                                tagNumber1,
                                MaterialTheme.colors.onSurface,
                                borderStroke = 1.dp,
                                deletable = true
                            ) {
                                tagNumber--
                                tagNumber1 = ""
                                return@MyTag Unit
                            }

                        }

                        AnimatedVisibility(visible = tagNumber2.length >= 16, exit = fadeOut()) {
                            MyTag(
                                tagNumber2,
                                MaterialTheme.colors.onSurface,
                                borderStroke = 1.dp,
                                deletable = true
                            ) {
                                tagNumber--
                                tagNumber2 = ""
                                return@MyTag Unit
                            }

                        }

                        AnimatedVisibility(visible = tagNumber3.length >= 13, exit = fadeOut()) {
                            MyTag(
                                tagNumber3,
                                MaterialTheme.colors.onSurface,
                                borderStroke = 1.dp,
                                deletable = true
                            ) {
                                tagNumber--
                                tagNumber3 = ""
                                return@MyTag Unit
                            }
                        }


                    }
                }


                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Button(
                    onClick = { /*TODO*/ }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = confirmButtonText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

        }
    }
}
/**
 * this function will reset all the values in the Temporary section
 * to their default values when Persistent Button is clicked
 * @param context is for fetching Due Date string from strings.xml
 */
fun resetTemporary(context: Context) {
    txtShowDateAndTime = context.getString(
        R.string.due_date
    )
    dayFromTextField = ""
    monthFromTextField = ""
    yearFromTextField = ""
    minuteFromTextField = ""
    hourFromTextField = ""
    todayIsSelected = false
    tomorrowIsSelected = false
    afterTomorrowIsSelected = false
}

