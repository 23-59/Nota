package com.A_23_59.hypernote

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.A_23_59.hypernote.ui.theme.*
import kotlinx.coroutines.launch


var isNotScrollingUp by mutableStateOf(false)

var searchBtnIsClicked by mutableStateOf(false)

var selectBtnIsClicked by mutableStateOf(false)

var showWelcomeScreen by mutableStateOf(true)

var selectedItems = mutableStateListOf<Item>()

var tasksTagsList = ArrayList<Tag>()
var notesTagsList = ArrayList<Tag>()

var currentPage by mutableIntStateOf(0)


val showTopAppBar by derivedStateOf { !selectBtnIsClicked && !searchBtnIsClicked }

enum class Priority {
    LOW, MEDIUM, HIGH
}


data class Item(
    val title: String,
    val description: String,
    val priority: Priority = Priority.LOW,
    val tagNumber1: String? = null,
    val tagNumber2: String? = null,
    val tagNumber3: String? = null,
    val dueDate: Array<String>? = null,
    val repeatTime: String? = null,
    var isChecked: Boolean? = false,
    val hasReminder: Boolean = false

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (!dueDate.contentEquals(other.dueDate)) return false

        return true
    }

    override fun hashCode(): Int {
        return dueDate.contentHashCode()
    }
}


val taskList =
    mutableStateListOf<Item>()//TODO  IMPORTANT !!! your list will duplicate items because you don't have ViewModel

val noteList = mutableListOf<Item>()

var itemWidth = 0.dp

@SuppressLint("SuspiciousIndentation")
@Composable
fun TaskOrNote(
    itemIsTask: Boolean = true,
    item: Item,

    ) {
    var isSelected by rememberSaveable { mutableStateOf(false) }
    val interactionSource = MutableInteractionSource()
    var expandButtonIsClicked by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current
    val gradientBackground = remember(key1 = item.priority) {
        when (item.priority) {
            Priority.HIGH -> redGradient
            Priority.MEDIUM -> goldGradient
            Priority.LOW -> blueGradient
        }
    }

    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp)
            .fillMaxWidth()
            .onGloballyPositioned {
                itemWidth = with(density) {
                    it.size.width.toDp()
                }
            }
            .clickable(
                enabled = selectBtnIsClicked,
                indication = null,
                interactionSource = interactionSource
            ) {
                isSelected = !isSelected
                if (isSelected)
                    selectedItems.add(item)
                else
                    selectedItems.remove(item)

            },
        shape = RoundedCornerShape(12.dp), elevation = 16.dp
    ) {
        if (!selectBtnIsClicked)
            isSelected = false
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .background(if (isSelected) greyGradient else gradientBackground),

            ) {

            val (titlePosition, descriptionPosition, dividerPosition, reminderPosition, checkboxPosition, expandButtonPosition, taskTypePosition, tagsPosition, dueDateTextPosition, dueDatePosition, deletePosition, editPosition) = createRefs()

            Text(
                text = item.title, style = MaterialTheme.typography.h6,
                color = Color.White,
                modifier = Modifier
                    .constrainAs(titlePosition) {

                        start.linkTo(dividerPosition.end, 12.dp)
                        if (itemIsTask && selectedRepeatTaskOption.isNotEmpty()) {
                            top.linkTo(taskTypePosition.top)
                            end.linkTo(taskTypePosition.start, 24.dp)
                        } else {
                            top.linkTo(parent.top, 8.dp)
                            end.linkTo(parent.end, 8.dp)
                        }
                        width = Dimension.fillToConstraints

                    }
            )

            if (item.description.length >= 40) {
                val expandButtonIcon by remember { derivedStateOf { if (expandButtonIsClicked) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown } }
                AnimatedContent(
                    targetState = expandButtonIcon,
                    modifier = Modifier.constrainAs(expandButtonPosition) {


                        if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                            top.linkTo(descriptionPosition.bottom, 8.dp)
                        else
                            top.linkTo(tagsPosition.bottom, 8.dp)

                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints

                    },
                    label = "expandButton"
                ) {
                    IconButton(
                        onClick = { expandButtonIsClicked = !expandButtonIsClicked },
                    ) {
                        Icon(
                            imageVector = it,
                            contentDescription = "expand",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }

            }

            if (itemIsTask) {
                if (item.repeatTime?.isNotEmpty() == true)
                    Icon(
                        painter = painterResource(
                            id = R.drawable.clock_refresh
                        ),
                        tint = Color.White.copy(0.7f),
                        contentDescription = "task type",
                        modifier = Modifier
                            .constrainAs(taskTypePosition) {
                                end.linkTo(parent.end, 8.dp)
                                top.linkTo(parent.top, 8.dp)
                            }
                            .size(24.dp))

                if (item.hasReminder)
                    Icon(tint = Color.White.copy(0.7f),
                        painter = painterResource(id = R.drawable.bell_01),
                        contentDescription = "reminder",
                        modifier = Modifier
                            .constrainAs(reminderPosition) {
                                if (item.repeatTime?.isNotEmpty() == true) {
                                    top.linkTo(taskTypePosition.bottom, 16.dp)
                                    start.linkTo(taskTypePosition.start)
                                    end.linkTo(taskTypePosition.end)
                                } else {
                                    top.linkTo(parent.top, 8.dp)
                                    end.linkTo(parent.end, 8.dp)
                                }


                            }
                            .size(24.dp)
                    )

            }

            if (item.description.isNotEmpty()) {
                AnimatedContent(targetState = if (expandButtonIsClicked) Int.MAX_VALUE else 1,
                    label = "descriptionAnimation", transitionSpec = {
                        ContentTransform(
                            fadeIn(), fadeOut()
                        )
                    }, modifier = Modifier.constrainAs(descriptionPosition) {
                        width = Dimension.fillToConstraints
                        top.linkTo(titlePosition.bottom, 12.dp)
                        start.linkTo(titlePosition.start)
                        end.linkTo(taskTypePosition.start, 16.dp)
                    }
                ) {
                    Text(
                        text = item.description,
                        textAlign = TextAlign.Start,
                        maxLines = it,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White.copy(alpha = 0.90F),
                        style = MaterialTheme.typography.body1,


                        )
                }

            }
            if (itemIsTask) {
                item.isChecked?.let {
                    AnimatedContent(
                        targetState = item.isChecked,
                        label = "itemIsChecked",
                        modifier = Modifier
                            .constrainAs(checkboxPosition) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)

                            }) { checked ->
                        IconButton(onClick = { item.isChecked = !item.isChecked!! }) {
                            Icon(
                                painter = if (checked == true) painterResource(id = R.drawable.check_square) else painterResource(
                                    id = R.drawable.square
                                ), tint = Color.White, contentDescription = "CheckedStatus"
                            )
                        }
                    }
                }
            }

            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(deletePosition) {
                start.linkTo(parent.start)

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.trash_01),
                    contentDescription = "delete", tint = Color.White
                )
            }

            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(editPosition) {
                start.linkTo(parent.start)
                if (itemIsTask)
                    top.linkTo(checkboxPosition.bottom, 4.dp)
                else
                    top.linkTo(parent.top)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_02),
                    contentDescription = "edit", tint = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .background(Color.White)
                    .constrainAs(dividerPosition) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(editPosition.end)
                        height = Dimension.fillToConstraints
                    })

            if (itemIsTask)
                createVerticalChain(
                    checkboxPosition,
                    editPosition,
                    deletePosition,
                    chainStyle = ChainStyle.Spread
                )
            else
                createVerticalChain(editPosition, deletePosition, chainStyle = ChainStyle.Spread)

            val context = LocalContext.current

            if (itemIsTask && item.dueDate != null) {

                Text(
                    text = stringResource(R.string.task_due_date),
                    style = MaterialTheme.typography.body2,
                    color = Color.White.copy(0.92F),
                    modifier = Modifier.constrainAs(dueDateTextPosition) {
                        if (item.description.isNotEmpty()) {
//                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                            top.linkTo(descriptionPosition.bottom, 16.dp)
//                            else
//                                top.linkTo(descriptionPosition.bottom, 24.dp)
                        } else {
                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                                top.linkTo(titlePosition.bottom, 32.dp)
                            else
                                top.linkTo(titlePosition.bottom, 16.dp)
                        }
                        start.linkTo(titlePosition.start)
                    })

                Text(
                    text = "${item.dueDate?.get(0)}/${item.dueDate?.get(1)}/${item.dueDate?.get(2)}  |  ${item.dueDate?.get(3)}:${item.dueDate?.get(4)}",
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                    modifier = Modifier.constrainAs(dueDatePosition) {
                        start.linkTo(dueDateTextPosition.end, 12.dp)
                        top.linkTo(dueDateTextPosition.top)
                        bottom.linkTo(dueDateTextPosition.bottom)
                    })
            }

            var columnLayoutWidth by remember { mutableStateOf(0.dp) }


            val density = LocalDensity.current

            Column(
                Modifier
                    .constrainAs(tagsPosition) {
                        start.linkTo(dividerPosition.start, 12.dp)
                        width = Dimension.fillToConstraints
                        if (itemIsTask)
                            if (item.dueDate != null)
                                top.linkTo(dueDateTextPosition.bottom, 16.dp)
                            else
                                top.linkTo(descriptionPosition.bottom, 16.dp)
                        else
                            top.linkTo(descriptionPosition.bottom, 16.dp)
                        if (item.description.length < 40)
                            bottom.linkTo(parent.bottom, 12.dp)

                    }
                    .wrapContentWidth()
                    .padding(vertical = 2.dp)
                    .onGloballyPositioned {
                        if (columnLayoutWidth == 0.dp)
                            columnLayoutWidth = with(density) {
                                it.size.width.toDp()
                            }
                        Log.i(
                            TAG,
                            "TaskOrNote: the column layout width is equal to : $columnLayoutWidth"
                        )
                    },
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    Modifier
                        .wrapContentWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (tagNumber1.isNotEmpty() && columnLayoutWidth < itemWidth - 20.dp) {
                        MyTag(
                            tagNumber1,
                            Color.White,
                            borderStroke = 1.dp
                        ) {
                            tagNumber--
                            tagNumber1 = ""
                            return@MyTag Unit
                        }
                    }
                    if (tagNumber2.isNotEmpty() && columnLayoutWidth < itemWidth - 20.dp) {
                        MyTag(
                            tagNumber2,
                            Color.White,
                            borderStroke = 1.dp
                        ) {
                            tagNumber--
                            tagNumber2 = ""
                            return@MyTag Unit
                        }
                    }

                    if (tagNumber3.isNotEmpty() && columnLayoutWidth < itemWidth - 20.dp) {
                        MyTag(
                            tagNumber3,
                            Color.White,
                            borderStroke = 1.dp
                        ) {
                            tagNumber--
                            tagNumber3 = ""
                            return@MyTag Unit
                        }
                    }

                }
                if (tagNumber1.isNotEmpty() && columnLayoutWidth > itemWidth - 20.dp) {
                    MyTag(
                        tagNumber1,
                        Color.White,
                        borderStroke = 1.dp
                    ) {
                        tagNumber--
                        tagNumber1 = ""
                        return@MyTag Unit
                    }

                }

                if (columnLayoutWidth > itemWidth - 20.dp) {
                    MyTag(
                        tagNumber2,
                        Color.White,
                        borderStroke = 1.dp
                    ) {
                        tagNumber--
                        tagNumber2 = ""
                        return@MyTag Unit
                    }

                }

                if (columnLayoutWidth > itemWidth - 20.dp) {
                    MyTag(
                        tagNumber3,
                        Color.White,
                        borderStroke = 1.dp
                    ) {
                        tagNumber--
                        tagNumber3 = ""
                        return@MyTag Unit
                    }
                }


            }

        }
    }


}


@Composable
private fun LazyListState.isNotScrollingUp(): Boolean {  // this piece of code might have some bugs in future
    val isScrollingToEnd by remember { derivedStateOf { layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1 } }
    val previousLastItem by remember { mutableIntStateOf(layoutInfo.visibleItemsInfo.lastIndex) }
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (isScrollingToEnd && previousLastItem == layoutInfo.visibleItemsInfo.lastIndex) {
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


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipsSection(navController: NavController) {
    val context = LocalContext.current
    var selectedChip by rememberSaveable { mutableStateOf("") }
    val chipsList = arrayOf(
        context.getString(R.string.txt_main_tags),
        context.getString(R.string.txt_main_importance),
        context.getString(R.string.txt_main_date),
        context.getString(R.string.txt_main_ascending),
        context.getString(R.string.txt_main_descending)
    )
    taskList.forEach { taskItem ->
        taskItem.tagNumber1?.let { taskTag ->
            if (tasksTagsList.none { tagItem -> tagItem.tagName == taskTag } && taskTag.isNotEmpty())
                tasksTagsList.add(Tag(taskTag))
        }

        taskItem.tagNumber2?.let { taskTag ->
            if (tasksTagsList.none { tagItem -> tagItem.tagName == taskTag } && taskTag.isNotEmpty())
                tasksTagsList.add(Tag(taskTag))
        }

        taskItem.tagNumber3?.let { taskTag ->
            if (tasksTagsList.none { tagItem -> tagItem.tagName == taskTag } && taskTag.isNotEmpty())
                tasksTagsList.add(Tag(taskTag))
        }
    }

    noteList.forEach { noteItem ->
        noteItem.tagNumber1?.let { noteTag ->
            if (notesTagsList.none { tagItem -> tagItem.tagName == noteTag } && noteTag.isNotEmpty())
                notesTagsList.add(Tag(noteTag))
        }

        noteItem.tagNumber2?.let { noteTag ->
            if (notesTagsList.none { tagItem -> tagItem.tagName == noteTag } && noteTag.isNotEmpty())
                notesTagsList.add(Tag(noteTag))
        }

        noteItem.tagNumber3?.let { noteTag ->
            if (notesTagsList.none { tagItem -> tagItem.tagName == noteTag } && noteTag.isNotEmpty())
                notesTagsList.add(Tag(noteTag))
        }
    }




    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(top = 4.dp)
    ) {


        LazyRow(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {


            items(chipsList, key = { it }, contentType = { String }) { item ->


                val selectedItemAnimation by animateColorAsState(
                    targetValue = if (selectedChip == item) MaterialTheme.colors.primary.copy(
                        0.6f
                    )
                    else MaterialTheme.colors.onSurface.copy(
                        0.1f
                    ), label = "selectedItemAnimation"
                )
                Chip(
                    onClick = {
                        selectedChip = item
                        if (selectedChip == context.getString(R.string.txt_main_tags))
                            showTagsDialog = true
                        else {
                            if (currentPage == 0)
                                tasksTagsList.filter { it.isChecked }
                                    .forEach { it.isChecked = false }
                            else
                                notesTagsList.filter { it.isChecked }
                                    .forEach { it.isChecked = false }
                        }


                    },
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface),
                    shape = RoundedCornerShape(12.dp),
                    colors = ChipDefaults.chipColors(backgroundColor = selectedItemAnimation),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
            }


        }
        Divider(Modifier.fillMaxWidth(), color = MaterialTheme.colors.onSurface.copy(0.2f))
    }


}


@Composable
fun ItemsListSection(
    navController: NavController,
    elements: List<Item>,
    itemsAreTasks: Boolean
) {

    val lazyListState = rememberLazyListState()

    isNotScrollingUp = lazyListState.isNotScrollingUp()
    Column {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(top = 56.dp, bottom = 60.dp),

            content = {
                item { ChipsSection(navController) }

                items(elements, key = { it.title }, contentType = { it }) { item ->
                    if (itemsAreTasks)
                        TaskOrNote(item = item) // this one is task
                    else
                        TaskOrNote(itemIsTask = false, item) // this one is note


                }

            },
        )
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyViewPager(navController: NavController, pagerState: PagerState, modifier: Modifier) {

    HorizontalPager(state = pagerState, userScrollEnabled = false) { position ->

        when (position) {
            0 -> Tasks(navController)
            1 -> Notes(navController)
        }


    }
}


@Composable
fun Notes(navController: NavController) {
    ItemsListSection(navController, elements = noteList, itemsAreTasks = false)
    Log.i(TAG, "Notes: ")
}

@Composable
fun Tasks(navController: NavController) {

    ItemsListSection(navController, elements = taskList, itemsAreTasks = true)
    Log.i(TAG, "Tasks: ")
}

@Composable
fun MyTag(
    tagTitle: String = "",
    color: Color,
    modifier: Modifier = Modifier, textAlignment: TextAlign = TextAlign.Center,
    textModifier: Modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
    roundedCornerValue: Dp = 5.dp,
    deletable: Boolean = false,
    borderStroke: Dp = 2.dp,
    onClick: (() -> Unit?)? = null,
): Boolean {


    Surface(
        color = Color.Transparent,
        border = BorderStroke(width = borderStroke, color),
        modifier = modifier,
        shape = RoundedCornerShape(roundedCornerValue)
    ) {
        if (deletable)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tagTitle,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    textAlign = textAlignment,
                    modifier = textModifier,
                    style = MaterialTheme.typography.caption,
                )
                IconButton(
                    onClick = {
                        if (onClick != null) {
                            onClick()
                        }
                    },
                    modifier = Modifier.size(26.dp),
                    interactionSource = MutableInteractionSource()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "close",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }


            }
        else
            Text(
                text = tagTitle,
                color = color,
                fontWeight = FontWeight.Bold,
                textAlign = textAlignment,
                modifier = textModifier,
                style = MaterialTheme.typography.caption,
            )

    }
    return tagTitle.length > 15
}

@Composable
fun MyTopAppbar(navController: NavController) {
    Column {
        TopAppBar(
            elevation = 0.dp,
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
                        painter = painterResource(id = R.drawable.search_lg),
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = { selectBtnIsClicked = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = null
                    )
                }
                IconButton(onClick = { navController.navigate("settings_screen") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings_02),
                        contentDescription = null
                    )
                }
                //TODO add sort section

            }, backgroundColor = MaterialTheme.colors.surface

        )
        Divider(Modifier.fillMaxWidth(), MaterialTheme.colors.onSurface.copy(0.2f))
    }


}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun MyFab(navigator: () -> Unit) {

    AnimatedVisibility(
        visible = isNotScrollingUp && !selectBtnIsClicked && !searchBtnIsClicked,
        enter = slideInVertically() + fadeIn(),
        exit = fadeOut() + slideOutVertically()
    ) {
        FloatingActionButton(
            shape = CircleShape,
            onClick = { navigator() },
            backgroundColor = MaterialTheme.colors.primary,
        ) {
            Icon(
                painter = painterResource(R.drawable.plus),
                contentDescription = "add",
                tint = Color.White
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
@Composable
fun HomePage(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    var taskIsSelected by rememberSaveable { mutableStateOf(true) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { 2 }
    currentPage = pagerState.currentPage
    Scaffold(
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            MyFab {
                if (currentPage == 0) navController.navigate("add_task_screen")
                else navController.navigate("add_note_screen")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            AnimatedVisibility(
                visible = isNotScrollingUp && !selectBtnIsClicked && !searchBtnIsClicked,
                enter = slideInVertically() + fadeIn(),
                exit = fadeOut() + slideOutVertically()
            ) {

                BottomAppBar(
                    elevation = 0.dp,
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, bottom = 0.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    backgroundColor = Color.Transparent
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .background(if (themeIsDark) darkBottomBar else lightBottomBar)
                    ) {
                        BottomNavigationItem(
                            selected = taskIsSelected,
                            alwaysShowLabel = false,
                            onClick = {
                                coroutineScope.launch {
                                    taskIsSelected = true
                                    pagerState.animateScrollToPage(0)

                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.check_square_broken),
                                    contentDescription = "tasks"
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.txt_main_tasks),

                                    )
                            })
                        Spacer(modifier = Modifier.width(80.dp))
                        BottomNavigationItem(
                            selected = !taskIsSelected, alwaysShowLabel = false,
                            onClick = {
                                coroutineScope.launch {
                                    taskIsSelected = false
                                    pagerState.animateScrollToPage(1)

                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.file_06),
                                    contentDescription = "notes"
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.txt_main_notes),

                                    )
                            })
                    }

                }


            }

        },
    ) { padding ->


        ConstraintLayout(  // you will use ConstraintLayout for rootLayer
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
        ) {
            val (selectBtnPosition, searchBtnPosition, topAppBarPosition, viewpagerPosition) = createRefs()

            MyViewPager(navController, pagerState = pagerState,
                Modifier
                    .padding(top = 8.dp)
                    .constrainAs(viewpagerPosition) {
                        top.linkTo(topAppBarPosition.bottom)
                        width = Dimension.matchParent
                    })

            AnimatedVisibility(
                visible = selectBtnIsClicked,
                enter = slideInVertically(),
                exit = slideOutVertically(),
                modifier = Modifier.constrainAs(selectBtnPosition) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            ) {
                SelectMode()
            }

            AnimatedVisibility(
                visible = searchBtnIsClicked,
                enter = slideInVertically(),
                exit = slideOutVertically(), modifier = Modifier.constrainAs(searchBtnPosition) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            ) {
                SearchTextField()
            }

            AnimatedVisibility(
                visible = showTopAppBar && isNotScrollingUp,
                enter = slideInVertically(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier.constrainAs(topAppBarPosition) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            ) {
                MyTopAppbar(navController)
            }
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
                        painter = painterResource(id = R.drawable.x_close),
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


@Composable
fun SelectMode() {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.DarkGray)
    ) {

        val (deleteIcon, selectAllIcon, closeIcon, markAsCheckedIcon, itemsSelectedText) = createRefs()
        AnimatedContent(
            targetState = selectedItems.size,
            label = "selection animation",
            modifier = Modifier.constrainAs(itemsSelectedText) {
                start.linkTo(parent.start, 8.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }) {
            Text(
                text = if (it == 0) "0 ${stringResource(R.string.txt_main_items_selected)}" else "$it ${
                    stringResource(
                        R.string.txt_main_items_selected
                    )
                }",
                color = Color.White,
                style = MaterialTheme.typography.h6,
            )
        }


        IconButton(
            onClick = {
                selectBtnIsClicked = false
                selectedItems.clear()
            },
            modifier = Modifier.constrainAs(closeIcon) {
                end.linkTo(selectAllIcon.start)
                bottom.linkTo(parent.bottom)
                top.linkTo(parent.top)
            }) {
            Icon(
                painter = painterResource(id = R.drawable.x_close),
                contentDescription = "check",
                tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(deleteIcon) {
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            top.linkTo(parent.top)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.trash_01),
                contentDescription = "Delete",
                tint = Color.White
            )
        }

        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(selectAllIcon) {
            end.linkTo(markAsCheckedIcon.start)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.list),
                contentDescription = "mark as checked", tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(markAsCheckedIcon) {
            end.linkTo(deleteIcon.start)
            bottom.linkTo(parent.bottom)
            top.linkTo(parent.top)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.check_square),
                contentDescription = "check all", tint = Color.White
            )
        }
    }


}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePagePreview() {
    HomePage(navController = rememberNavController())
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
fun MyTagPreview() {
    MyTag("Daily", Color.Blue)
}
