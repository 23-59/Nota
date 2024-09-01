package com.golden_minute.nota.presentation


//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.golden_minute.nota.R
import com.golden_minute.nota.domain.model.Priority
import com.golden_minute.nota.domain.util.Add_Edit_Events
import com.golden_minute.nota.domain.util.ChooseDateTimeDialogEvents
import com.golden_minute.nota.domain.util.leadingZero
import com.golden_minute.nota.ui.theme.Gold200
import com.golden_minute.nota.ui.theme.darkerBlue
import com.golden_minute.nota.ui.theme.darkerRed
import com.golden_minute.nota.ui.theme.darkerYellow
import com.golden_minute.nota.ui.theme.elevatedSurface
import com.golden_minute.nota.ui.theme.iranYekan
import com.golden_minute.nota.ui.theme.iranYekanFarsiNamerals
import com.golden_minute.nota.ui.theme.lighterBlue
import com.golden_minute.nota.ui.theme.lighterRed
import com.golden_minute.nota.ui.theme.lighterYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


var title by mutableStateOf("")
var description by mutableStateOf("")


@SuppressLint("SuspiciousIndentation", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Add_Edit_Item(
    navController: NavController,
    currentPage: Int,
    addEditViewmodel: Add_EditScreenViewModel = hiltViewModel(),
    mainActivityViewModel: MainActivityViewModel,

    ) {


    val scrollState = rememberScrollState()

    var txtShowDateAndTime by rememberSaveable {
        mutableStateOf(addEditViewmodel.state.value.dueDate)
    }

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        addEditViewmodel.eventFlow.collectLatest { event ->
            when (event) {
                is Add_EditScreenViewModel.UiEvent.Save -> {
                    navController.popBackStack()
                }

                is Add_EditScreenViewModel.UiEvent.ShowSnackBar -> {
                    snackBarHostState.snackbarHostState.showSnackbar(event.snackBarTitle)
                }
            }
        }
    }

    val context = LocalContext.current


    val confirmButtonText =
        if (addEditViewmodel.currentId == null) {
            if (currentPage == 0) stringResource(id = R.string.add_to_the_tasks) else stringResource(
                id = R.string.add_to_the_notes
            )
        } else {
            if (currentPage == 0) stringResource(id = R.string.edit_the_task) else stringResource(id = R.string.edit_the_note)
        }

    val topAppBarText =
        if (addEditViewmodel.currentId == null) {
            if (currentPage == 0) stringResource(R.string.adding_task) else stringResource(R.string.adding_note)
        } else {
            if (currentPage == 0) stringResource(id = R.string.edit_the_task) else stringResource(id = R.string.edit_the_note)
        }


    Scaffold(scaffoldState = snackBarHostState, topBar = {
        TopAppBar(title = {
            Text(
                text = topAppBarText,
                style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primaryVariant
            )
        }, backgroundColor = MaterialTheme.colors.surface, elevation = 0.dp, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colors.primaryVariant,
                    contentDescription = "back"
                )
            }
        })
    }) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
                .verticalScroll(scrollState)
        )
        {
            val (titlePosition, priorityPosition, lowerColumnPosition, descriptionPosition, priorityTextPosition) = createRefs()



            if (addEditViewmodel.dialogState.value.showDateAndTimeDialog)
                ChooseDateTimeDialog(mainActivityViewModel, addEditViewmodel)

            if (addEditViewmodel.showRepeatDialog)
                RepeatTaskDialog()



            OutlinedTextField(
                isError = addEditViewmodel.titleError,
                value = addEditViewmodel.state.value.title,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onSurface,
                    backgroundColor = if (mainActivityViewModel.themeData.value) elevatedSurface else Color.Black.copy(
                        0.12f
                    )
                ),
                onValueChange = {
                    addEditViewmodel.titleError = false
                    addEditViewmodel.onEvent(Add_Edit_Events.EnteredTitle(it))
                },
                label = {
                    Text(
                        text = stringResource(
                            R.string.text_field_title
                        )
                    )
                },
                modifier = Modifier.constrainAs(titlePosition) {
                    top.linkTo(parent.top, 16.dp)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                })

            OutlinedTextField(
                isError = addEditViewmodel.descriptionError,
                value = addEditViewmodel.state.value.description,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onSurface,
                    backgroundColor = if (mainActivityViewModel.themeData.value) elevatedSurface else Color.Black.copy(
                        0.12f
                    )
                ),
                onValueChange = {
                    addEditViewmodel.descriptionError = false
                    addEditViewmodel.onEvent(Add_Edit_Events.EnteredDescription(it))
                },
                label = {
                    Text(
                        text = if (currentPage == 0) stringResource(R.string.textfield_description_optional) else stringResource(
                            id = R.string.textfield_description
                        )
                    )
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
                        top.linkTo(descriptionPosition.bottom, 32.dp)
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

                    var blueIsSelected by rememberSaveable {
                        mutableStateOf(addEditViewmodel.state.value.taskPriority == Priority.LOW)
                    }
                    var yellowIsSelected by rememberSaveable {
                        mutableStateOf(addEditViewmodel.state.value.taskPriority == Priority.MEDIUM)
                    }
                    var redIsSelected by rememberSaveable {
                        mutableStateOf(addEditViewmodel.state.value.taskPriority == Priority.HIGH)
                    }


                    txtShowDateAndTime =
                        addEditViewmodel.state.value.dueDate.ifBlank { stringResource(id = R.string.due_date) }

                    val redWeight by animateFloatAsState(targetValue = if (redIsSelected) 2f else 1f)
                    val yellowWeight by animateFloatAsState(targetValue = if (yellowIsSelected) 2f else 1f)
                    val blueWeight by animateFloatAsState(targetValue = if (blueIsSelected) 2f else 1f)
                    val redBorder by animateDpAsState(targetValue = if (redIsSelected) 3.dp else 0.dp)
                    val yellowBorder by animateDpAsState(targetValue = if (yellowIsSelected) 3.dp else 0.dp)
                    val blueBorder by animateDpAsState(targetValue = if (blueIsSelected) 3.dp else 0.dp)
                    val interactionSource = remember { MutableInteractionSource() }
                    val blueSize by animateDpAsState(targetValue = if (blueIsSelected) 75.dp else 45.dp)
                    val redSize by animateDpAsState(targetValue = if (redIsSelected) 75.dp else 45.dp)
                    val yellowSize by animateDpAsState(targetValue = if (yellowIsSelected) 75.dp else 45.dp)

                    when (addEditViewmodel.state.value.taskPriority) {
                        Priority.LOW -> {
                            blueIsSelected = true
                            yellowIsSelected = false
                            redIsSelected = false
                        }

                        Priority.MEDIUM -> {
                            yellowIsSelected = true
                            blueIsSelected = false
                            redIsSelected = false
                        }

                        Priority.HIGH -> {
                            redIsSelected = true
                            yellowIsSelected = false
                            blueIsSelected = false
                        }
                    }

                    Box(
                        modifier = Modifier
                            .shadow(
                                20.dp,
                                RoundedCornerShape(15.dp),
                                spotColor = if (mainActivityViewModel.themeData.value && blueIsSelected) Color.White else Color.Black
                            )
                            .border(
                                width = blueBorder,
                                color = if (blueIsSelected) MaterialTheme.colors.onSurface else Color.Transparent,
                                RoundedCornerShape(15.dp)
                            )
                            .clip(RoundedCornerShape(15.dp))
                            .height(blueSize)
                            .background(
                                Brush.verticalGradient(
                                    listOf(lighterBlue, darkerBlue),
                                    startY = -10f
                                )
                            )
                            .weight(blueWeight)
                            .clickable(
                                indication = null,
                                interactionSource = interactionSource
                            ) {
                                blueIsSelected = true
                                yellowIsSelected = false
                                redIsSelected = false
                                addEditViewmodel.onEvent(Add_Edit_Events.ChangePriority(Priority.LOW))
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

                    Box(
                        modifier = Modifier
                            .shadow(
                                20.dp, RoundedCornerShape(15.dp),
                                spotColor = if (mainActivityViewModel.themeData.value && yellowIsSelected) Color.White else Color.Black
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
                                addEditViewmodel.onEvent(Add_Edit_Events.ChangePriority(Priority.MEDIUM))
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




                    Box(
                        modifier = Modifier
                            .shadow(
                                20.dp, RoundedCornerShape(15.dp),
                                spotColor = if (mainActivityViewModel.themeData.value && redIsSelected) Color.White else Color.Black
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
                                addEditViewmodel.onEvent(Add_Edit_Events.ChangePriority(Priority.HIGH))
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
                            top.linkTo(priorityPosition.bottom, 40.dp)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxWidth(), horizontalAlignment = Alignment.Start)
                {
                    var dueDateButtonIsSelected by remember { mutableStateOf(addEditViewmodel.state.value.dueDate.isNotBlank()) }
                    dueDateButtonIsSelected = addEditViewmodel.state.value.dueDate.isNotBlank()
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
                        var repeatButtonSelected by remember { mutableStateOf(addEditViewmodel.state.value.repeatTime.isNotEmpty()) }

                        val repeatIsSelectedColor by animateColorAsState(  //TODO   WE WANT TO TURN THIS BUTTON ON ONLY WHEN REPEAT DATE HAS BEEN SET , AND THEN TURN IT OFF
                            targetValue = if (addEditViewmodel.selectedRepeatTaskOption.isNotEmpty() || addEditViewmodel.state.value.repeatTime.isNotBlank()) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(
                                0.6f
                            ),
                            label = "repeatColorIsSelected"
                        )
                        val dueDateIsSelectedColor by animateColorAsState(
                            targetValue = if (addEditViewmodel.dialogState.value.dueDateHasBeenSet || addEditViewmodel.state.value.dueDate.isNotBlank()) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(
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
                                            if (addEditViewmodel.selectedRepeatTaskOption.isNotEmpty() || addEditViewmodel.state.value.repeatTime.isNotBlank() && addEditViewmodel.state.value.dueDate.isNotBlank()) {
                                                addEditViewmodel.selectedRepeatTaskOption = ""
                                                addEditViewmodel.onEvent(
                                                    Add_Edit_Events.EnteredRepeatDialog(
                                                        ""
                                                    )
                                                )
                                                repeatButtonSelected = false

                                            } else {
                                                addEditViewmodel.showRepeatDialog = true
                                                repeatButtonSelected = true


                                            }

                                        } else {

                                            if (addEditViewmodel.dialogState.value.dueDateHasBeenSet || addEditViewmodel.state.value.dueDate.isNotBlank()) {

                                                addEditViewmodel.onEvent(Add_Edit_Events.EnteredRepeatDialog(""))

                                                addEditViewmodel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.ToggleDueDate(false)
                                                )
                                                addEditViewmodel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.ClearAllValues
                                                )
                                            } else {
                                                addEditViewmodel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.ShowDateAndTimeDialog(
                                                        true
                                                    )
                                                )
                                                addEditViewmodel.dialogEvents(
                                                    ChooseDateTimeDialogEvents.ToggleDueDate(true)
                                                )
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
                                        targetState = addEditViewmodel.dialogState.value.dueDateHasBeenSet || addEditViewmodel.state.value.dueDate.isNotBlank(),
                                        label = "dueDateAnimation"
                                    ) { dueDateHasBeenSet ->

                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            fontFamily = if (mainActivityViewModel.languageData.value == "fa-ir") iranYekanFarsiNamerals else iranYekan,
                                            text = if (dueDateHasBeenSet) {
                                                if (addEditViewmodel.dialogState.value.reminderIsChecked && addEditViewmodel.state.value.dueDate.isNotBlank()) {
                                                    val localDate = LocalDateTime.parse(
                                                        addEditViewmodel.state.value.dueDate,
                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                                    )
                                                    val calendarType =
                                                        if (mainActivityViewModel.languageData.value == "fa-ir") gregorian_to_jalali(
                                                            localDate.year,
                                                            localDate.monthValue,
                                                            localDate.dayOfMonth
                                                        ) else intArrayOf(
                                                            localDate.year,
                                                            localDate.monthValue,
                                                            localDate.dayOfMonth
                                                        )
                                                    "${calendarType[0]}/${leadingZero(calendarType[1].toString())}/${
                                                        leadingZero(
                                                            calendarType[2].toString()
                                                        )
                                                    } \n ${leadingZero(localDate.hour.toString())}:${
                                                        leadingZero(
                                                            localDate.minute.toString()
                                                        )
                                                    } \n ${
                                                        stringResource(
                                                            R.string.has_reminder
                                                        )
                                                    }"
                                                } else if (addEditViewmodel.state.value.dueDate.isNotBlank()) {
                                                    val localDate = LocalDateTime.parse(
                                                        addEditViewmodel.state.value.dueDate,
                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                                    )
                                                    "${localDate.year}/${leadingZero(localDate.monthValue.toString())}/${
                                                        leadingZero(
                                                            localDate.dayOfMonth.toString()
                                                        )
                                                    } \n ${leadingZero(localDate.hour.toString())}:${
                                                        leadingZero(
                                                            localDate.minute.toString()
                                                        )
                                                    }"
                                                } else ""
                                            } else
                                                context.getString(
                                                    R.string.due_date_add_screen
                                                ),
                                            color = if (it == context.getString(R.string.repeat)) repeatIsSelectedColor else dueDateIsSelectedColor
                                        )
                                    }
                                } else
                                    AnimatedContent(
                                        targetState = addEditViewmodel.selectedRepeatTaskOption.isNotEmpty() || addEditViewmodel.state.value.repeatTime.isNotBlank(),
                                        label = "repeatAnimation"
                                    ) { dueDateIsSet ->
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            text = if (dueDateIsSet) addEditViewmodel.state.value.repeatTime else context.getString(
                                                R.string.repeat
                                            ),
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

                    var tagValue by rememberSaveable { mutableStateOf("") }

                    var tagError by rememberSaveable { mutableStateOf(false) }

                    var characterLimit by rememberSaveable { mutableStateOf<Byte>(0) }

                    Column(Modifier.fillMaxWidth()) {
                        AnimatedContent(
                            targetState = characterLimit,
                            label = "characterLimit",
                            modifier = Modifier
                                .align(AbsoluteAlignment.Right)
                                .padding(end = 4.dp, bottom = 4.dp, top = 4.dp)
                        ) {
                            Text(
                                text = "$it / 25",
                                color = MaterialTheme.colors.onSurface.copy(0.5f)
                            )
                        }
                        OutlinedTextField(
                            value = tagValue,
                            enabled = addEditViewmodel.state.value.tagNumber1.isBlank() || addEditViewmodel.state.value.tagNumber2.isBlank() || addEditViewmodel.state.value.tagNumber3.isBlank(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            onValueChange = {

                                if (!it.startsWith(" ") && it.length <= 25) {
                                    characterLimit = it.length.toByte()
                                    tagValue = it
                                    tagError = false
                                } else tagError = true


                            },
                            isError = tagError,
                            trailingIcon = {
                                IconButton(enabled = addEditViewmodel.state.value.tagNumber1.isBlank() || addEditViewmodel.state.value.tagNumber2.isBlank() || addEditViewmodel.state.value.tagNumber3.isBlank(),
                                    onClick = {

                                        addEditViewmodel.onEvent(
                                            Add_Edit_Events.PressedAddTagButton(
                                                tagValue
                                            )
                                        )
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
                                backgroundColor = if (mainActivityViewModel.themeData.value) elevatedSurface else Color.Black.copy(
                                    0.12f
                                )
                            ),
                            modifier = Modifier
                                .onFocusEvent {
                                    if (it.isFocused)
                                        coroutineScope.launch {
                                            delay(500)
                                            scrollState.animateScrollTo(scrollState.maxValue)
                                        }

                                }
                                .fillMaxWidth()
                                .padding(bottom = if (addEditViewmodel.state.value.tagNumber1.isEmpty()) 24.dp else 0.dp)
                        )


                    }

                    AnimatedVisibility(visible = addEditViewmodel.state.value.tagNumber1.isNotEmpty() || addEditViewmodel.state.value.tagNumber2.isNotEmpty() || addEditViewmodel.state.value.tagNumber3.isNotEmpty()) {
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
                                    visible = addEditViewmodel.state.value.tagNumber1.isNotEmpty() && addEditViewmodel.state.value.tagNumber1.length < 25,
                                    exit = fadeOut()
                                ) {
                                    myTag(
                                        addEditViewmodel.state.value.tagNumber1,
                                        MaterialTheme.colors.onSurface,
                                        borderStroke = 1.dp, deletable = true
                                    ) {
                                        addEditViewmodel.onEvent(
                                            Add_Edit_Events.PressedDeleteTagButton(
                                                "tagNumber1"
                                            )
                                        )
                                        return@myTag Unit
                                    }
                                }
                                AnimatedVisibility(
                                    visible = addEditViewmodel.state.value.tagNumber2.isNotEmpty() && addEditViewmodel.state.value.tagNumber2.length < 16,
                                    exit = fadeOut()
                                ) {
                                    myTag(
                                        addEditViewmodel.state.value.tagNumber2,
                                        MaterialTheme.colors.onSurface,
                                        borderStroke = 1.dp, deletable = true
                                    ) {
                                        addEditViewmodel.onEvent(
                                            Add_Edit_Events.PressedDeleteTagButton(
                                                "tagNumber2"
                                            )
                                        )
                                        return@myTag Unit
                                    }
                                }

                                AnimatedVisibility(
                                    visible = addEditViewmodel.state.value.tagNumber3.isNotEmpty() && addEditViewmodel.state.value.tagNumber2.length < 13,
                                    exit = fadeOut()
                                ) {
                                    addEditViewmodel.state.value.tagNumber3.let {
                                        myTag(
                                            it,
                                            MaterialTheme.colors.onSurface,
                                            borderStroke = 1.dp, deletable = true
                                        ) {

                                            addEditViewmodel.onEvent(
                                                Add_Edit_Events.PressedDeleteTagButton(
                                                    "tagNumber3"
                                                )
                                            )
                                            return@myTag Unit
                                        }
                                    }
                                }

                            }
                            AnimatedVisibility(
                                visible = addEditViewmodel.state.value.tagNumber1.isNotEmpty() && addEditViewmodel.state.value.tagNumber1.length >= 25,
                                exit = fadeOut()
                            ) {
                                myTag(
                                    addEditViewmodel.state.value.tagNumber1,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp,
                                    deletable = true
                                ) {
                                    addEditViewmodel.onEvent(
                                        Add_Edit_Events.PressedDeleteTagButton(
                                            "tagNumber1"
                                        )
                                    )
                                    return@myTag Unit
                                }

                            }

                            AnimatedVisibility(
                                visible = addEditViewmodel.state.value.tagNumber2.length >= 16,
                                exit = fadeOut()
                            ) {
                                myTag(
                                    addEditViewmodel.state.value.tagNumber2,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp,
                                    deletable = true
                                ) {
                                    addEditViewmodel.onEvent(
                                        Add_Edit_Events.PressedDeleteTagButton(
                                            "tagNumber2"
                                        )
                                    )
                                    return@myTag Unit
                                }

                            }

                            AnimatedVisibility(
                                visible = addEditViewmodel.state.value.tagNumber3.length >= 13,
                                exit = fadeOut()
                            ) {
                                myTag(
                                    addEditViewmodel.state.value.tagNumber3,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp,
                                    deletable = true
                                ) {
                                    addEditViewmodel.onEvent(
                                        Add_Edit_Events.PressedDeleteTagButton(
                                            "tagNumber3"
                                        )
                                    )
                                    return@myTag Unit
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
                        onClick = {
                            addEditViewmodel.onEvent(Add_Edit_Events.Save)
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
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

                    var characterLimit by remember { mutableStateOf<Byte>(0) }

                    Column(Modifier.fillMaxWidth()) {
                        AnimatedContent(
                            targetState = characterLimit,
                            label = "characterLimit",
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(end = 4.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = "$it / 25",
                                color = MaterialTheme.colors.onSurface.copy(0.5f)
                            )
                        }
                        OutlinedTextField(
                            value = tagValue,

                            enabled = addEditViewmodel.state.value.tagNumber1.isBlank() || addEditViewmodel.state.value.tagNumber2.isBlank() || addEditViewmodel.state.value.tagNumber3.isBlank(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            onValueChange = {

                                if (!it.startsWith(" ") && it.length <= 25) {
                                    characterLimit = it.length.toByte()
                                    tagValue = it

                                }


                            },
                            trailingIcon = {
                                IconButton(enabled = addEditViewmodel.state.value.tagNumber1.isBlank() || addEditViewmodel.state.value.tagNumber2.isBlank() || addEditViewmodel.state.value.tagNumber3.isBlank(),
                                    onClick = {

                                        addEditViewmodel.onEvent(
                                            Add_Edit_Events.PressedAddTagButton(
                                                tagValue
                                            )
                                        )

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
                                backgroundColor = if (mainActivityViewModel.themeData.value) elevatedSurface else Color.Black.copy(
                                    0.12f
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = if (addEditViewmodel.state.value.tagNumber1.isEmpty()) 24.dp else 0.dp)
                        )


                    }


                    AnimatedVisibility(visible = addEditViewmodel.state.value.tagNumber1.isNotEmpty() || addEditViewmodel.state.value.tagNumber2.isNotEmpty() || addEditViewmodel.state.value.tagNumber3.isNotEmpty()) {
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
                                    visible = addEditViewmodel.state.value.tagNumber1.isNotEmpty() && addEditViewmodel.state.value.tagNumber1.length < 25,
                                    exit = fadeOut()
                                ) {
                                    myTag(
                                        addEditViewmodel.state.value.tagNumber1,
                                        MaterialTheme.colors.onSurface,
                                        borderStroke = 1.dp, deletable = true
                                    ) {
                                        addEditViewmodel.onEvent(
                                            Add_Edit_Events.PressedDeleteTagButton(
                                                "tagNumber1"
                                            )
                                        )
                                        return@myTag Unit
                                    }
                                }
                                AnimatedVisibility(
                                    visible = addEditViewmodel.state.value.tagNumber2.isNotEmpty() && addEditViewmodel.state.value.tagNumber2.length < 16,
                                    exit = fadeOut()
                                ) {
                                    myTag(
                                        addEditViewmodel.state.value.tagNumber2,
                                        MaterialTheme.colors.onSurface,
                                        borderStroke = 1.dp, deletable = true
                                    ) {
                                        addEditViewmodel.onEvent(
                                            Add_Edit_Events.PressedDeleteTagButton(
                                                "tagNumber2"
                                            )
                                        )
                                        return@myTag Unit
                                    }
                                }

                                AnimatedVisibility(
                                    visible = addEditViewmodel.state.value.tagNumber3.isNotEmpty() && addEditViewmodel.state.value.tagNumber2.length < 13,
                                    exit = fadeOut()
                                ) {
                                    addEditViewmodel.state.value.tagNumber3.let {
                                        myTag(
                                            it,
                                            MaterialTheme.colors.onSurface,
                                            borderStroke = 1.dp, deletable = true
                                        ) {

                                            addEditViewmodel.onEvent(
                                                Add_Edit_Events.PressedDeleteTagButton(
                                                    "tagNumber3"
                                                )
                                            )
                                            return@myTag Unit
                                        }
                                    }
                                }

                            }
                            AnimatedVisibility(
                                visible = addEditViewmodel.state.value.tagNumber1.isNotEmpty() && addEditViewmodel.state.value.tagNumber1.length >= 25,
                                exit = fadeOut()
                            ) {
                                myTag(
                                    addEditViewmodel.state.value.tagNumber1,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp,
                                    deletable = true
                                ) {
                                    addEditViewmodel.onEvent(
                                        Add_Edit_Events.PressedDeleteTagButton(
                                            "tagNumber1"
                                        )
                                    )
                                    return@myTag Unit
                                }

                            }

                            AnimatedVisibility(
                                visible = addEditViewmodel.state.value.tagNumber2.length >= 16,
                                exit = fadeOut()
                            ) {
                                myTag(
                                    addEditViewmodel.state.value.tagNumber2,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp,
                                    deletable = true
                                ) {
                                    addEditViewmodel.onEvent(
                                        Add_Edit_Events.PressedDeleteTagButton(
                                            "tagNumber2"
                                        )
                                    )
                                    return@myTag Unit
                                }

                            }

                            AnimatedVisibility(
                                visible = addEditViewmodel.state.value.tagNumber3.length >= 13,
                                exit = fadeOut()
                            ) {
                                myTag(
                                    addEditViewmodel.state.value.tagNumber3,
                                    MaterialTheme.colors.onSurface,
                                    borderStroke = 1.dp,
                                    deletable = true
                                ) {
                                    addEditViewmodel.onEvent(
                                        Add_Edit_Events.PressedDeleteTagButton(
                                            "tagNumber3"
                                        )
                                    )
                                    return@myTag Unit
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
                        onClick = { addEditViewmodel.onEvent(Add_Edit_Events.Save) },
                        modifier = Modifier
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


}

