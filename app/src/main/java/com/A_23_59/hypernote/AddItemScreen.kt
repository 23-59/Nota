package com.A_23_59.hypernote


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
var date by mutableStateOf(IntArray(3))
val time by mutableStateOf(IntArray(2))
var dateAndTime by mutableStateOf("")

@Composable
fun AddNewItem(
    task_or_note: Char = 'T',
    edit_or_add: Char = 'A',
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    dateAndTime = stringResource(id = R.string.due_date)

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
        val (confirmButtonPosition, titlePosition, priorityPosition, descriptionPosition, priorityTextPosition, dateTimePosition, taskTypeTextPosition, taskTypePosition, topBarPosition, tagsTextPosition, tagsPosition) = createRefs()

        Button(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(confirmButtonPosition) {
            width = Dimension.fillToConstraints
            bottom.linkTo(parent.bottom, 24.dp)
            start.linkTo(parent.start, 16.dp)
            end.linkTo(parent.end, 16.dp)
        }) {
            Text(
                text = confirmButtonText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        TopAppBar(title = {
            Text(
                text = topAppBarText,
                style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primaryVariant
            )
        }, backgroundColor = MaterialTheme.colors.surface, elevation = 0.dp, navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
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
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface),
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
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface),
            onValueChange = { description = it },
            label = {
                Text(
                    text = if (task_or_note == 'T') stringResource(R.string.textfield_description_optional) else stringResource(
                        id = R.string.textfield_description
                    )
                )
            },
            modifier = Modifier.constrainAs(descriptionPosition) {
                top.linkTo(titlePosition.bottom, 32.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.constrainAs(priorityPosition) {
                    width = Dimension.fillToConstraints
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(priorityTextPosition.bottom, 24.dp)

                }) {
                Button(
                    onClick = {
                        redIsSelected = false
                        yellowIsSelected = false
                        blueIsSelected = true
                    },
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        sBlue
                    )
                ) {
                    val alpha: Float by animateFloatAsState(targetValue = if (blueIsSelected) 1f else 0f)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.low),
                            style = MaterialTheme.typography.body1,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .width(30.dp)
                                .height(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .alpha(alpha)
                            )
                        }

                    }
                }

                Button(
                    onClick = {
                        redIsSelected = false
                        yellowIsSelected = true
                        blueIsSelected = false
                    },
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        Gold200
                    )
                ) {
                    val alpha: Float by animateFloatAsState(targetValue = if (yellowIsSelected) 1f else 0f)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(if (selectedLocale == "fa-ir") 16.dp else 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.medium),
                            style = MaterialTheme.typography.body1,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .height(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .alpha(alpha)
                            )
                        }

                    }

                }

                Button(
                    onClick = {
                        redIsSelected = true
                        yellowIsSelected = false
                        blueIsSelected = false
                    },
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        darkerRed
                    )
                ) {
                    val alpha: Float by animateFloatAsState(targetValue = if (redIsSelected) 1f else 0f)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.high),
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(end = 16.dp),
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .alpha(alpha)
                            )
                        }

                    }
                }
            }


            Text(
                text = stringResource(R.string.task_type),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.constrainAs(taskTypeTextPosition) {
                    top.linkTo(priorityTextPosition.bottom, 100.dp)
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

                    Button(
                        onClick = { taskType = option
                            if(option==context.getString(R.string.persistent)){
                                dateAndTime = context.getString(
                                    R.string.due_date
                                )
                                day=""
                                month =""
                                year = ""
                                minute=""
                                hour = ""
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
            val dateTimeHeight by animateDpAsState(
                targetValue = if (taskType == context.getString(
                        R.string.temporary
                    )
                ) 80.dp else 0.dp
            )


            Column(modifier = Modifier
                .constrainAs(dateTimePosition) {
                    width = Dimension.fillToConstraints
                    top.linkTo(taskTypePosition.bottom, 32.dp)
                    start.linkTo(taskTypePosition.start)
                    end.linkTo(taskTypePosition.end)
                }
                .height(dateTimeHeight)) {
                Text(
                    text =dateAndTime,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = { navigator.navigate(ChooseDateTimeDialogDestination) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(R.string.choose_date_and_time),
                        style = MaterialTheme.typography.body1
                    )
                }

            }


        } else {
            Text(
                text = stringResource(R.string.tags),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.constrainAs(tagsTextPosition) {
                    top.linkTo(descriptionPosition.bottom, 24.dp)
                    start.linkTo(descriptionPosition.start)

                })

        }

    }


}

