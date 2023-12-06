package com.A_23_59.hypernote


import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.AbsoluteAlignment
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.A_23_59.hypernote.ui.theme.*


var title by mutableStateOf("")
var description by mutableStateOf("")

var blueIsSelected by mutableStateOf(true)
var yellowIsSelected by mutableStateOf(false)
var redIsSelected by mutableStateOf(false)
var txtShowDateAndTime by mutableStateOf("")
var tagNumber1 by mutableStateOf("مطالعه")
var tagNumber2 by mutableStateOf("ورزش")
var tagNumber3 by mutableStateOf("کارهای روزانه")
var tagNumber by mutableIntStateOf(
    if (tagNumber1.isNotEmpty() && tagNumber2.isNotEmpty() && tagNumber3.isNotEmpty())
        3
    else if (tagNumber1.isNotEmpty() && tagNumber2.isNotEmpty())
        2
    else if (tagNumber1.isNotEmpty())
        1
    else
        0
)


@SuppressLint("SuspiciousIndentation")
@Composable
fun AddNewItem(
    edit_or_add: Char = 'A',
    navController: NavController,
    currentPage: Int
) {

    val context = LocalContext.current
    BackHandler {
        txtShowDateAndTime = context.getString(R.string.due_date)
        yearFromTextField = 0.toString()
        monthFromTextField = 0.toString()
        dayFromTextField = 0.toString()
        hourFromTextField = 0.toString()
        minuteFromTextField = 0.toString()
        tagNumber = 0
        tagNumber1 = ""
        tagNumber2 = ""
        tagNumber3 = ""
        navController.popBackStack()
    }
    txtShowDateAndTime = stringResource(id = R.string.due_date)


    val confirmButtonText =
        if (currentPage == 0) stringResource(id = R.string.add_to_the_tasks) else stringResource(id = R.string.add_to_the_notes)

    val topAppBarText =
        if (currentPage == 0) stringResource(R.string.adding_task) else stringResource(R.string.adding_note)


    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .verticalScroll(rememberScrollState())
    ) {
        val (titlePosition, priorityPosition, lowerColumnPosition, descriptionPosition, priorityTextPosition, topBarPosition) = createRefs()



        TopAppBar(title = {
            Text(
                text = topAppBarText,
                style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primaryVariant
            )
        }, backgroundColor = MaterialTheme.colors.surface, elevation = 0.dp, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
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
                Text(text = stringResource(R.string.textfield_description_optional))
            },
            modifier = Modifier.constrainAs(descriptionPosition) {
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
                top.linkTo(titlePosition.bottom, 32.dp)


            })


        if (currentPage == 0) {
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
                val redBorder by animateDpAsState(targetValue = if (redIsSelected) 3.dp else 0.dp)
                val yellowBorder by animateDpAsState(targetValue = if (yellowIsSelected) 3.dp else 0.dp)
                val blueBorder by animateDpAsState(targetValue = if (blueIsSelected) 3.dp else 0.dp)
                val interactionSource = remember { MutableInteractionSource() }
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
                            interactionSource = interactionSource
                        ) {
                            blueIsSelected = true
                            yellowIsSelected = false
                            redIsSelected = false
                        },
                ) {
                    AnimatedContent(
                        targetState = blueIsSelected,
                        modifier = Modifier.align(Alignment.Center), label = ""
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
                            interactionSource = interactionSource
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
                            interactionSource = interactionSource
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

            Column(
                Modifier
                    .constrainAs(lowerColumnPosition) {
                        start.linkTo(priorityPosition.start)
                        end.linkTo(priorityPosition.end)
                        top.linkTo(priorityTextPosition.bottom, 135.dp)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .fillMaxWidth(), horizontalAlignment = Alignment.Start)
            {
                var dueDateButtonIsSelected by remember { mutableStateOf(false) }
                val taskOptions =
                    listOf(
                        context.getString(R.string.repeat),
                        context.getString(R.string.due_date_add_screen)
                    )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    var repeatButtonSelected by remember { mutableStateOf(false) }

                    val repeatIsSelectedColor by animateColorAsState(  //TODO   WE WANT TO TURN THIS BUTTON ON ONLY WHEN REPEAT DATE HAS BEEN SET , AND THEN TURN IT OFF
                        targetValue = if (selectedRepeatTaskOption.isNotEmpty()) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(
                            0.6f
                        ),
                        label = "repeatColorIsSelected"
                    )
                    val dueDateIsSelectedColor by animateColorAsState(
                        targetValue = if (dueDateHasBeenSet || selectedRepeatTaskOption.isNotEmpty()) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(
                            0.6f
                        ),
                        label = "dueDateColorIsSelected"
                    )
                    taskOptions.forEach {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.5.dp,
                                    if (it == context.getString(R.string.repeat)) repeatIsSelectedColor else dueDateIsSelectedColor,
                                    RoundedCornerShape(12.dp)
                                )
                                .clip(
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = MutableInteractionSource()
                                ) {
                                    if (it == context.getString(R.string.repeat)) {
                                        if (selectedRepeatTaskOption.isNotEmpty()) {
                                            selectedRepeatTaskOption = ""
                                            repeatButtonSelected = false
                                        } else {
                                            showRepeatDialog = true
                                            repeatButtonSelected = true


                                        }

                                    } else {

                                        if (dueDateHasBeenSet && !repeatButtonSelected) {
                                            dueDateHasBeenSet = false
                                            txtShowDateAndTime = ""
                                        } else {
                                            showDateAndTimeDialog = true
                                            dueDateButtonIsSelected = true
                                        }

                                    }

                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (it == context.getString(R.string.repeat))
                                        R.drawable.clock_refresh
                                    else
                                        R.drawable.calendar
                                ),
                                contentDescription = "repeat",
                                tint = if (it == context.getString(R.string.repeat)) repeatIsSelectedColor else dueDateIsSelectedColor
                            )
                            if (it == context.getString(R.string.due_date_add_screen)) {
                                AnimatedContent(
                                    targetState = dueDateHasBeenSet,
                                    label = "dueDateAnimation"
                                ) { dueDateHasBeenSet ->
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = if (dueDateHasBeenSet) {
                                            if (reminderIsChecked)
                                                "$txtShowDateAndTime \n ${stringResource(R.string.has_reminder)}"
                                            else
                                                txtShowDateAndTime
                                        } else
                                            context.getString(
                                                R.string.due_date_add_screen
                                            ),
                                        color = if (it == context.getString(R.string.repeat)) repeatIsSelectedColor else dueDateIsSelectedColor
                                    )
                                }
                            } else
                                AnimatedContent(targetState = selectedRepeatTaskOption.isNotEmpty(),
                                    label = "repeatAnimation"
                                ) { dueDateIsSet ->
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = if (dueDateIsSet) selectedRepeatTaskOption else context.getString(R.string.repeat),
                                        color = if (it == context.getString(R.string.repeat)) repeatIsSelectedColor else dueDateIsSelectedColor
                                    )
                                }


                        }
                    }
                }



                Text(
                    text = stringResource(R.string.tags),
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6
                )

                Text(
                    text = stringResource(R.string.up_to_three_tags),
                    color = Gold200,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 12.dp)
                )

                var tagValue by remember { mutableStateOf("") }

                var tagError by remember { mutableStateOf(false) }

                var characterLimit by remember { mutableStateOf<Byte>(0) }

                Column(Modifier.fillMaxWidth()) {
                    AnimatedContent(
                        targetState = characterLimit,
                        label = "characterLimit",
                        modifier = Modifier
                            .align(AbsoluteAlignment.Left)
                            .padding(end = 4.dp, bottom = 4.dp)
                    ) {
                        Text(text = "$it / 25", color = MaterialTheme.colors.onSurface.copy(0.5f))
                    }
                    OutlinedTextField(
                        value = tagValue, enabled = tagNumber != 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        onValueChange = {

                            if (!it.startsWith(" ") && it.length <= 25) {
                                characterLimit = it.length.toByte()
                                tagValue = it
                                tagError = false
                            } else tagError = true


                        }, isError = tagError,
                        trailingIcon = {
                            IconButton(enabled = tagNumber != 3 && tagValue.isNotBlank() && !tagError,
                                onClick = {

                                    tagNumber++

                                    when (tagNumber) {
                                        1 -> if (tagValue.isNotEmpty()) tagNumber1 = tagValue
                                        2 -> if (tagValue.isNotEmpty()) tagNumber2 = tagValue
                                        3 -> if (tagValue.isNotEmpty()) tagNumber3 = tagValue
                                    }
                                    tagValue = ""
                                    characterLimit = 0

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


                }


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
            Column(Modifier.constrainAs(lowerColumnPosition) {
                start.linkTo(descriptionPosition.start)
                end.linkTo(descriptionPosition.end)
                top.linkTo(descriptionPosition.bottom, 16.dp)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }) {
                Text(
                    text = stringResource(R.string.tags),
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6, modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = stringResource(R.string.up_to_three_tags),
                    color = Gold200,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 12.dp)
                )

                var tagValue by remember { mutableStateOf("") }

                var tagError by remember { mutableStateOf(false) }

                var characterLimit by remember { mutableStateOf<Byte>(0) }

                Column(Modifier.fillMaxWidth()) {
                    AnimatedContent(
                        targetState = characterLimit,
                        label = "characterLimit",
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 4.dp, bottom = 4.dp)
                    ) {
                        Text(text = "$it / 25", color = MaterialTheme.colors.onSurface.copy(0.5f))
                    }
                    OutlinedTextField(
                        value = tagValue, enabled = tagNumber != 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        onValueChange = {

                            if (!it.startsWith(" ") && it.length <= 25) {
                                characterLimit = it.length.toByte()
                                tagValue = it
                                tagError = false
                            } else tagError = true


                        }, isError = tagError,
                        trailingIcon = {
                            IconButton(enabled = tagNumber != 3 && tagValue.isNotBlank() && !tagError,
                                onClick = {

                                    tagNumber++

                                    when (tagNumber) {
                                        1 -> if (tagValue.isNotEmpty()) tagNumber1 = tagValue
                                        2 -> if (tagValue.isNotEmpty()) tagNumber2 = tagValue
                                        3 -> if (tagValue.isNotEmpty()) tagNumber3 = tagValue
                                    }
                                    tagValue = ""
                                    characterLimit = 0

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


                }


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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddItemScreenPreview() {
AddNewItem(navController = rememberNavController(), currentPage = 0)
}
