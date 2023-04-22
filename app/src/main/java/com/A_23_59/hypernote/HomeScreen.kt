package com.A_23_59.hypernote

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.A_23_59.hypernote.destinations.AddPageDestination
import com.A_23_59.hypernote.destinations.SettingsPageDestination
import com.A_23_59.hypernote.destinations.SortDialogDestination
import com.A_23_59.hypernote.ui.theme.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

var isNotScrollingUp by mutableStateOf(false)

var searchBtnIsClicked by mutableStateOf(false)

var selectBtnIsClicked by mutableStateOf(false)

var showWelcomeScreen by mutableStateOf(true)

val showTopAppBar by derivedStateOf { !selectBtnIsClicked && !searchBtnIsClicked }
var year by mutableStateOf("")
var month by mutableStateOf("")
var day by mutableStateOf("")
var hour by mutableStateOf("")
var minute by mutableStateOf("")

data class Item(val title: String, val description: String, val color: Color)

val itemList = ArrayList<Item>()


@Composable
fun Task(
    title: String = "",
    description: String = "",
    priorityColor: Color? = null,
    itemIsTask: Boolean = true
) {

    val gradientBackground = when (priorityColor) {
        Color.Red -> Brush.verticalGradient(listOf(lighterRed, darkerRed), startY = -15f)
        Gold200 -> Brush.verticalGradient(listOf(lighterYellow, warning), startY = -10f)
        Color.Blue -> Brush.verticalGradient(listOf(lighterBlue, darkerBlue), startY = -10f)
        else -> throw java.lang.IllegalArgumentException("the color of the background is invalid")
    }

    Card(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), elevation = if (!themeIsDark) 12.dp else 0.dp
    ) {
        ConstraintLayout(
            Modifier
                .width(intrinsicSize = IntrinsicSize.Max)
                .background(gradientBackground)
        ) {

            val (titlePosition, descriptionPosition, checkboxPosition, taskTypePosition, tagsPosition, creationDateTextPosition, creationDatePosition, dueDateTextPosition, dueDatePosition, deletePosition, editPosition) = createRefs()

            val endGuideLine = createGuidelineFromEnd(16.dp)

            Text(
                text = title, style = MaterialTheme.typography.h6,
                color = Color.White,
                modifier = Modifier
                    .constrainAs(titlePosition) {

                        start.linkTo(parent.start, 8.dp)
                        top.linkTo(parent.top, 8.dp)
                        if (itemIsTask) end.linkTo(taskTypePosition.start, 24.dp) else end.linkTo(
                            parent.end,
                            16.dp
                        )
                        width = Dimension.fillToConstraints


                    }
            )
            if (itemIsTask) {
                Icon(
                    painter = painterResource(
                        id = if (taskType == stringResource(id = R.string.persistent)) R.drawable.round_autorenew_24 else R.drawable.round_timelapse_30
                    ),
                    tint = Color.White,
                    contentDescription = "task type",
                    modifier = Modifier.constrainAs(taskTypePosition) {
                        end.linkTo(endGuideLine, (-8).dp)
                        top.linkTo(parent.top, 8.dp)
                    })
            }

            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.90F), style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .constrainAs(descriptionPosition) {
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                            top.linkTo(titlePosition.bottom, 12.dp)
                            start.linkTo(titlePosition.start)
                            end.linkTo(endGuideLine, 24.dp)
                        })
            }
            if (itemIsTask) {
                Checkbox(
                    checked = false,
                    colors = CheckboxDefaults.colors(uncheckedColor = Color.White),
                    onCheckedChange = {},
                    modifier = Modifier.constrainAs(checkboxPosition) {
                        end.linkTo(endGuideLine, (-16).dp)
                        bottom.linkTo(parent.bottom, 2.dp)
                    })
            }

            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(deletePosition) {
                end.linkTo(checkboxPosition.start, (-2).dp)
                bottom.linkTo(parent.bottom, 2.dp)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_delete_outline_24),
                    contentDescription = "delete", tint = Color.White
                )
            }

            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(editPosition) {
                end.linkTo(deletePosition.start)
                bottom.linkTo(parent.bottom, 2.dp)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_edit_24),
                    contentDescription = "edit", tint = Color.White
                )
            }

            val context = LocalContext.current

            if (itemIsTask && taskType == context.getString(R.string.temporary)) {

                Text(
                    text = stringResource(R.string.task_due_date),
                    style = MaterialTheme.typography.body2, fontStyle = FontStyle.Italic,
                    color = Color.White.copy(0.92F),
                    modifier = Modifier.constrainAs(dueDateTextPosition) {
                        if (description.isNotEmpty()) top.linkTo(descriptionPosition.bottom, 16.dp)
                        else top.linkTo(titlePosition.bottom, 16.dp)
                        start.linkTo(titlePosition.start)
                    })

                Text(
                    text = "13/04/2023",
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                    modifier = Modifier.constrainAs(dueDatePosition) {
                        start.linkTo(dueDateTextPosition.end, 12.dp)
                        top.linkTo(dueDateTextPosition.top)
                        bottom.linkTo(dueDateTextPosition.bottom)
                    })
            }
            Text(
                text = if (itemIsTask) stringResource(R.string.creation_date_task) else stringResource(
                    R.string.creation_date_note
                ),
                color = Color.White.copy(0.85F), style = MaterialTheme.typography.body2,
                modifier = Modifier.constrainAs(creationDateTextPosition) {
                    start.linkTo(titlePosition.start)
                    if (taskType == context.getString(R.string.persistent)) {

                        if (description.isNotEmpty()) top.linkTo(
                            descriptionPosition.bottom,
                            16.dp
                        ) else top.linkTo(titlePosition.bottom, 16.dp)

                    } else {

                        if (itemIsTask) top.linkTo(
                            dueDateTextPosition.bottom,
                            12.dp
                        ) else if (description.isNotEmpty()) top.linkTo(
                            descriptionPosition.bottom,
                            16.dp
                        ) else top.linkTo(titlePosition.bottom, 16.dp)
                    }

                })


            Text(
                text = "24/08/2003",
                style = MaterialTheme.typography.body2,
                color = Color.White.copy(0.90F),
                modifier = Modifier.constrainAs(creationDatePosition) {
                    start.linkTo(creationDateTextPosition.end, 10.dp)
                    top.linkTo(creationDateTextPosition.top)
                    bottom.linkTo(creationDateTextPosition.bottom)
                }
            )

            Row(Modifier.constrainAs(tagsPosition) {
                start.linkTo(parent.start, 8.dp)
                top.linkTo(creationDateTextPosition.bottom, 12.dp)
                bottom.linkTo(parent.bottom, 8.dp)
            }, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MyTag("Daily Tasks", Color.White)
                MyTag("Work", Color.White)
                MyTag("Study", Color.White)
            }
        }
    }


}

@Composable
fun Note(
    title: String = "",
    description: String = "",
    priorityColor: Color?
) {
    Task(
        priorityColor = priorityColor,
        title = title,
        description = description,
        itemIsTask = false
    )
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    val isScrollingToEnd by remember { derivedStateOf { layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1 } }
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (isScrollingToEnd) {
                return@derivedStateOf false
            }
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun ItemsListSection(elements: List<Item>, showPriorityColor: Boolean) {
    val lazyListState = rememberLazyListState()

    isNotScrollingUp = lazyListState.isScrollingUp()
    Column {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(top = 105.dp),
            content = {
                items(elements, key = { it.title }) { item ->

                    if (showPriorityColor)
                        Task(item.title, item.description, item.color)
                    else
                        Note(item.title, item.description, Color.Blue)


                }

            },
        )
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyTabLayout(modifier: Modifier = Modifier, pagerState: PagerState) {

    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = isNotScrollingUp && !selectBtnIsClicked,
        enter = slideInVertically(),
        exit = slideOutVertically() + fadeOut()
    ) {
        TabRow(selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colors.surface,
            modifier = modifier
                .padding(bottom = 8.dp),
            tabs = {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = {
                        Text(
                            text = stringResource(R.string.txt_main_tasks),
                            style = MaterialTheme.typography.body1
                        )
                    })
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = {
                        Text(
                            text = stringResource(R.string.txt_main_notes),
                            style = MaterialTheme.typography.body1
                        )
                    })

            })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyViewPager(pagerState: PagerState) {
    HorizontalPager(pageCount = 2, state = pagerState) { position ->

        var testState by remember {
            mutableStateOf(0)
        }
        testState = pagerState.currentPage

        when (position) {
            0 -> {
                Tasks()
            }

            1 -> {
                Notes()
            }
        }
    }
}


@Composable
fun Notes() {
    ItemsListSection(elements = itemList, showPriorityColor = false)
}

@Composable
fun Tasks() {
    ItemsListSection(elements = itemList, showPriorityColor = true)
}

@Composable
fun MyTag(tagTitle: String = "", color: Color) {
    Surface(
        color = Color.Transparent,
        border = BorderStroke(width = 2.dp, color),
        modifier = Modifier.padding(top = 8.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Text(
            text = tagTitle, color = Color.White, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
fun MyTopAppbar(modifier: Modifier = Modifier, navigator: DestinationsNavigator) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name), fontFamily = pacifico,
                color = MaterialTheme.colors.onSurface, style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = { searchBtnIsClicked = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.search_svgrepo_com__1_),
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = { selectBtnIsClicked = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.list_svgrepo_com__1_),
                    contentDescription = null
                )
            }
            IconButton(onClick = { navigator.navigate(SortDialogDestination) }) {
                Icon(
                    painter = painterResource(id = R.drawable.sort_svgrepo_com),
                    contentDescription = "sort"
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.category_variety_random_shuffle_svgrepo_com),
                    contentDescription = "category"
                )
            }
            IconButton(onClick = { navigator.navigate(SettingsPageDestination) }) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_svgrepo_com__2_),
                    contentDescription = null
                )
            }
            //TODO add sort section

        },
        elevation = 0.dp, backgroundColor = MaterialTheme.colors.surface

    )


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyFab(pagerState: PagerState, navigator: () -> Unit) {

    AnimatedVisibility(
        visible = isNotScrollingUp,
        enter = slideInVertically() + fadeIn(),
        exit = fadeOut() + slideOutVertically()
    ) {
        AnimatedContent(targetState = pagerState.currentPage) {
            ExtendedFloatingActionButton(
                modifier = Modifier.shadow(
                    spotColor = if (themeIsDark) Color.White else Color.Black,
                    elevation = 7.dp,
                    shape = RoundedCornerShape(20.dp)
                ),
                text = {
                    Text(
                        text = if (pagerState.currentPage == 0) stringResource(id = R.string.add_new_task) else stringResource(
                            id = R.string.add_new_note
                        ),
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )


                }, shape = RoundedCornerShape(20.dp),
                onClick = { navigator() },
                icon = {
                    Icon(
                        painter = painterResource(id = if (pagerState.currentPage == 0) R.drawable.round_add_task_24 else R.drawable.round_note_add_24),
                        contentDescription = "add"
                    )
                },
                backgroundColor = MaterialTheme.colors.onSurface,
                contentColor = MaterialTheme.colors.surface
            )
        }

    }

}

@Composable
fun SortSectionDisplay() {
    Surface(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = 0.dp)
                .height(IntrinsicSize.Min)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.txt_main_sort_as),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 2.dp)
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = "close")
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomePage(navigator: DestinationsNavigator) {
    val pagerState = rememberPagerState(0)


    Scaffold(
        floatingActionButton = { MyFab(pagerState) { navigator.navigate(AddPageDestination(if (pagerState.currentPage == 0) 0 else 1)) } },
        floatingActionButtonPosition = FabPosition.Center,
    ) { padding ->


        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
        ) {
            MyViewPager(pagerState = pagerState)

            AnimatedVisibility(
                visible = selectBtnIsClicked,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                SelectMode()
            }

            AnimatedVisibility(
                visible = searchBtnIsClicked,
                enter = slideInVertically(),
                exit = slideOutVertically(), modifier = Modifier.align(Alignment.TopCenter)
            ) {
                SearchTextField()
            }

            AnimatedVisibility(
                visible = showTopAppBar && isNotScrollingUp,
                enter = slideInVertically(),
                exit = slideOutVertically() + fadeOut()
            ) {
                MyTopAppbar(navigator = navigator)
            }
            MyTabLayout(pagerState = pagerState, modifier = Modifier.padding(top = 56.dp))


        }
    }

}

@Composable
fun SearchTextField() {

    Box(Modifier.fillMaxWidth()) {
        var textFieldValue by remember { mutableStateOf("") }
        TextField(
            value = textFieldValue, modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), trailingIcon = {
                IconButton(onClick = { searchBtnIsClicked = false }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "close",
                        tint = Color.White
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.DarkGray,
                textColor = Color.White,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(0.dp),
            onValueChange = { textFieldValue = it }
        )
        AnimatedVisibility(visible = textFieldValue.isEmpty(), exit = fadeOut(), enter = fadeIn()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.txt_main_search),
                    modifier = Modifier.padding(start = 16.dp),
                    color = Color.White.copy(0.8F)
                )
            }
        }

    }


}

@Destination
@Composable
fun SettingsPage(navigator: DestinationsNavigator) {
    SettingsP()
    val context = LocalContext.current
    BackHandler {
        Toast.makeText(context, "back button is pressed", Toast.LENGTH_SHORT).show()
        navigator.navigateUp()
    }
}

@Composable
fun SelectMode() {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.DarkGray)
    ) {

        val (deleteIcon, selectAllIcon, closeIcon, markAsCheckedIcon, itemsSelectedText) = createRefs()

        Text(
            text = stringResource(R.string.txt_main_items_selected), color = Color.White,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.constrainAs(itemsSelectedText) {
                start.linkTo(parent.start, 8.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            })

        IconButton(
            onClick = { selectBtnIsClicked = false },
            modifier = Modifier.constrainAs(closeIcon) {
                end.linkTo(deleteIcon.start)
                bottom.linkTo(parent.bottom)
                top.linkTo(parent.top)
            }) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "check",
                tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(deleteIcon) {
            end.linkTo(markAsCheckedIcon.start)
            bottom.linkTo(parent.bottom)
            top.linkTo(parent.top)
        }) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        }

        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(selectAllIcon) {
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.round_checklist_24),
                contentDescription = "mark as checked", tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(markAsCheckedIcon) {
            end.linkTo(selectAllIcon.start)
            bottom.linkTo(parent.bottom)
            top.linkTo(parent.top)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.round_check_box_24),
                contentDescription = "check all", tint = Color.White
            )
        }
    }


}

@Destination(style = MyDialogStyle::class)
@Composable
fun ChooseDateTimeDialog(navigator: DestinationsNavigator) {

    var minuteError by remember { mutableStateOf(false) }
    var hourError by remember { mutableStateOf(false) }
    var dayError by remember { mutableStateOf(false) }
    var monthError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var setDateAndTimeIsEnabled by remember { mutableStateOf(false) }
    setDateAndTimeIsEnabled =
        !hourError && !minuteError && !dayError && !monthError && !yearError && minute.isNotEmpty() && hour.isNotEmpty() && year.isNotEmpty() && month.isNotEmpty() && day.isNotEmpty()


    Card(
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = stringResource(R.string.date),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                TextField(value = day,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = stringResource(R.string.day)) },
                    modifier = Modifier
                        .width(60.dp)
                        .weight(1f), isError = dayError,
                    onValueChange = {
                        try {
                            if (it.length < 3) day = it
                            if (day.isNotEmpty())
                                dayError = day.toInt() > 31 || day.toInt() == 0
                        }
                        catch (e:Exception){
                           Toast.makeText(context,context.getString(R.string.invalid_input),Toast.LENGTH_SHORT).show()
                           day = ""
                        }

                    })
                TextField(value = month,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = stringResource(R.string.month)) },
                    modifier = Modifier
                        .width(60.dp)
                        .weight(1f), isError = monthError,
                    onValueChange = {
                        try {
                            if (it.length < 3) month = it // TODO  these text fields won't work with 04 like values
                            if (month.isNotEmpty())
                                monthError = month.toInt() > 12 || month.toInt() ==0
                        }
                        catch (e:Exception){
                            Toast.makeText(context,context.getString(R.string.invalid_input),Toast.LENGTH_SHORT).show()
                            month = ""
                        }

                    })
                TextField(value = year,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = stringResource(R.string.year)) },
                    modifier = Modifier
                        .width(60.dp)
                        .weight(1f), isError = yearError,
                    onValueChange = {
                        try {
                            if (it.length < 5) year = it
                            if (year.isNotEmpty())
                                yearError =
                                    if (selectedLocale == "fa-ir") year.toInt() < 1402 else year.toInt() < 2023
                        }
                        catch (e:Exception){
                            Toast.makeText(context, context.getString(R.string.invalid_input),Toast.LENGTH_SHORT).show()
                            year=""
                        }

                    })
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.time),
                style = MaterialTheme.typography.h6
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                TextField(
                    value = hour,
                    placeholder = { Text(text = stringResource(R.string.hour)) },
                    onValueChange = {
                        try {
                            if (it.length < 3) hour = it
                            if (hour.isNotEmpty())
                                hourError = hour.toInt() > 23 || hour.toInt() == 0
                        }
                        catch (e:Exception){
                            Toast.makeText(context, context.getString(R.string.invalid_input),Toast.LENGTH_SHORT).show()
                            hour=""
                        }

                    },
                    modifier = Modifier
                        .width(60.dp)
                        .weight(1F), isError = hourError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text(text = ":", style = MaterialTheme.typography.h4)
                TextField(
                    value = minute,
                    placeholder = { Text(text = stringResource(R.string.minute)) },
                    onValueChange = {
                        try {
                            if (it.length < 3) minute = it
                            if (minute.isNotEmpty())
                                minuteError = minute.toInt() > 59 || minute.toInt() == 0
                        }
                        catch (e :Exception){
                            Toast.makeText(context, context.getString(R.string.invalid_input),Toast.LENGTH_SHORT).show()
                            minute=""
                        }

                    }, isError = minuteError,
                    modifier = Modifier
                        .width(60.dp)
                        .weight(1F),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    date[0] = day.toInt()
                    date[1] = month.toInt()
                    date[2] = year.toInt()
                    time[0] = hour.toInt()
                    time[1] = minute.toInt()
                    dateAndTime =
                        "${context.getString(R.string.due_date)} ${date[0]}/${date[1]}/${date[2]}  ${
                            context.getString(
                                R.string.at
                            )
                        } ${time[1]} : ${time[0]}"
                    navigator.navigateUp()
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

@Destination(style = MyDialogStyle::class)
@Composable
fun SortDialog(navigator: DestinationsNavigator) {

    val items = listOf(
        stringResource(R.string.txt_main_ascending),
        stringResource(R.string.txt_main_descending),
        stringResource(
            R.string.txt_main_importance
        ),
        stringResource(R.string.txt_main_date)
    )
    var selectedItem by remember { mutableStateOf("") }
    Card(shape = RoundedCornerShape(15.dp)) {
        Column(Modifier.selectableGroup()) {
            Text(
                text = stringResource(R.string.sort_as),
                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp),
                style = MaterialTheme.typography.h6
            )
            items.forEach { label ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = label == selectedItem,
                            onClick = {
                                CoroutineScope(Main).launch {
                                    selectedItem = label
                                    delay(500)
                                    navigator.navigateUp()
                                }
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedItem == label,
                        onClick = null,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Text(text = label)
                }
            }
        }

    }
}

@Destination
@Composable
fun EditPage(navigator: DestinationsNavigator) {
    AddNewItem(edit_or_add = 'E', navigator = navigator)
}

@Destination
@Composable
fun AddPage(navigator: DestinationsNavigator, pagerState: Int) {
    val context = LocalContext.current
    BackHandler {
        dateAndTime = context.getString(R.string.due_date)
        for (i in date.indices) {
            date[i] = 0
        }
        navigator.navigateUp()
    }
    AddNewItem(task_or_note = if (pagerState == 0) 'T' else 'N', navigator = navigator)
}


@Composable
fun SettingsP() {
    Surface(Modifier.fillMaxSize()) {
        Text(text = "This is Settings Page")
    }
}

@Preview
@Composable
fun SettingsPreview() {
    SettingsP()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchPreview() {
    SearchTextField()
}

@Preview
@Composable
fun SelectItemsBtnPreview() {
    SelectMode()
}

@Preview
@Composable
fun SortSectionPreview() {
    SortSectionDisplay()
}

@Preview
@Composable
fun MyActionSectionPreview() {
}

@Preview
@Composable
fun ItemListSectionPreview() {

    ItemsListSection(itemList, showPriorityColor = true)
}

@Preview
@Composable
fun MyTagPreview() {
    MyTag("Daily", Color.Blue)
}

@Preview
@Composable
fun SingleItemPreview() {
    Task(
        title = "First Task",
        description = "this is the first task that i will do in this app",
        priorityColor = Color.Red
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
fun HomePagePreviewDark() {

}

@Preview(showSystemUi = true)
@Composable
fun HomePagePreviewLight() {

}