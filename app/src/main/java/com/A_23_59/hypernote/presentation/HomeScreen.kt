package com.A_23_59.hypernote.presentation

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.A_23_59.hypernote.R
import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.model.Priority
import com.A_23_59.hypernote.domain.model.Task
import com.A_23_59.hypernote.domain.util.HomeScreenNotesEvent
import com.A_23_59.hypernote.domain.util.HomeScreenTasksEvent
import com.A_23_59.hypernote.domain.util.NoteOrderType
import com.A_23_59.hypernote.domain.util.TaskOrderType
import com.A_23_59.hypernote.ui.theme.blueGradient
import com.A_23_59.hypernote.ui.theme.elevatedSurface
import com.A_23_59.hypernote.ui.theme.goldGradient
import com.A_23_59.hypernote.ui.theme.greyGradient
import com.A_23_59.hypernote.ui.theme.pacifico
import com.A_23_59.hypernote.ui.theme.redGradient
import kotlinx.coroutines.launch


var isNotScrollingUp by mutableStateOf(false)

var searchBtnIsClicked by mutableStateOf(false)

var selectBtnIsClicked by mutableStateOf(false)

var showWelcomeScreen by mutableStateOf(true)

var selectedTasks = mutableStateListOf<Task>()

var currentPage by mutableIntStateOf(0)

data class DistinctTag(val tagName: String?, var isChecked: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tagName)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DistinctTag> {
        override fun createFromParcel(parcel: Parcel): DistinctTag {
            return DistinctTag(parcel)
        }

        override fun newArray(size: Int): Array<DistinctTag?> {
            return arrayOfNulls(size)
        }
    }
}


val showTopAppBar by derivedStateOf { !selectBtnIsClicked && !searchBtnIsClicked }


var itemWidth = 0.dp

@SuppressLint("SuspiciousIndentation")
@Composable
fun TaskOrNote(
    modifier: Modifier = Modifier,
    itemIsTask: () -> Boolean = { true },
    task: Task? = null,
    note: Note? = null,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    navController: NavController? = null,
    onDelete: () -> Unit,
    onEdit: () -> Unit,

    ) {
    var isSelected by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var textHasOverflow by remember { mutableStateOf(false) }
    var expandButtonIsClicked by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current
    val gradientBackground = remember(key1 = task?.priority) {
        when (task?.priority) {
            Priority.HIGH -> redGradient
            Priority.MEDIUM -> goldGradient
            Priority.LOW -> blueGradient
            null -> blueGradient
        }
    }

    Card(elevation = 12.dp,
        modifier = modifier
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
                    task?.let { selectedTasks.add(it) }
                else
                    selectedTasks.remove(task)

            },
        shape = RoundedCornerShape(12.dp)
    ) {
        if (!selectBtnIsClicked)
            isSelected = false
        ConstraintLayout(
            modifier
                .fillMaxWidth()
                .background(if (isSelected) greyGradient else gradientBackground),

            ) {

            val (titlePosition, descriptionPosition, dividerPosition, reminderPosition, checkboxPosition, expandButtonPosition, taskTypePosition, tagsPosition, dueDateTextPosition, dueDatePosition, deletePosition, editPosition) = createRefs()

            if (task != null) {
                Text(
                    text = task.title, style = MaterialTheme.typography.h6,
                    color = Color.White,
                    modifier = Modifier
                        .constrainAs(titlePosition) {

                            start.linkTo(dividerPosition.end, 12.dp)
                            if (task.repeatTime?.isNotEmpty() == true) {
                                top.linkTo(taskTypePosition.top)
                                end.linkTo(taskTypePosition.start, 8.dp)
                            }
                            width = Dimension.fillToConstraints

                        }
                )
            } else
                note?.let {
                    Text(
                        text = it.title, style = MaterialTheme.typography.h6,
                        color = Color.White,
                        modifier = Modifier
                            .constrainAs(titlePosition) {
                                top.linkTo(parent.top, 8.dp)
                                end.linkTo(parent.end, 8.dp)
                                start.linkTo(dividerPosition.end, 12.dp)
                                width = Dimension.fillToConstraints

                            }
                    )
                }

            if (textHasOverflow) {
                val expandButtonIcon by remember { derivedStateOf { if (expandButtonIsClicked) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown } }
                AnimatedContent(
                    targetState = expandButtonIcon,
                    modifier = Modifier.constrainAs(expandButtonPosition) {


                        if (note?.tagNumber1?.isBlank() ?: return@constrainAs && note.tagNumber2?.isBlank() ?: return@constrainAs && note.tagNumber3?.isBlank() ?: return@constrainAs)
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

            if (itemIsTask()) {
                if (task?.repeatTime?.isNotEmpty() == true)
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

                if (task?.hasReminder == true)
                    Icon(tint = Color.White.copy(0.7f),
                        painter = painterResource(id = R.drawable.bell_01),
                        contentDescription = "reminder",
                        modifier = Modifier
                            .constrainAs(reminderPosition) {
                                if (task.repeatTime?.isNotEmpty() == true) {
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

            if (task != null) {
                if (task.description.isNotBlank()) {
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
                            text = task.description,
                            textAlign = TextAlign.Start,
                            maxLines = it,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White.copy(alpha = 0.90F),
                            style = MaterialTheme.typography.body1,
                            onTextLayout = { textInfo ->
                                if (textInfo.hasVisualOverflow)
                                    textHasOverflow = true

                            },

                            )
                    }

                }
            } else
                note?.let { nonNullNote ->
                    if (nonNullNote.description.isNotBlank()) {
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
                                text = nonNullNote.description,
                                textAlign = TextAlign.Start,
                                maxLines = it,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White.copy(alpha = 0.90F),
                                style = MaterialTheme.typography.body1,
                                onTextLayout = { textInfo ->
                                    if (textInfo.hasVisualOverflow)
                                        textHasOverflow = true

                                },

                                )
                        }

                    }
                }


            if (itemIsTask()) {
                task?.isChecked?.let {
                    AnimatedContent(
                        targetState = task.isChecked,
                        label = "itemIsChecked",
                        modifier = Modifier
                            .constrainAs(checkboxPosition) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)

                            }) { checked ->
                        IconButton(onClick = { task.isChecked = !task.isChecked!! }) {
                            Icon(
                                painter = if (checked == true) painterResource(id = R.drawable.check_square) else painterResource(
                                    id = R.drawable.square
                                ), tint = Color.White, contentDescription = "CheckedStatus"
                            )
                        }
                    }
                }
            }

            IconButton(onClick = {
                onDelete()
            }, modifier = Modifier.constrainAs(deletePosition) {
                start.linkTo(parent.start)

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.trash_01),
                    contentDescription = "delete", tint = Color.White
                )
            }

            IconButton(onClick = {
                onEdit()
            }, modifier = Modifier.constrainAs(editPosition) {
                start.linkTo(parent.start)
                if (itemIsTask())
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

            if (itemIsTask())
                createVerticalChain(
                    checkboxPosition,
                    editPosition,
                    deletePosition,
                    chainStyle = ChainStyle.Spread
                )
            else
                createVerticalChain(editPosition, deletePosition, chainStyle = ChainStyle.Spread)

            if (itemIsTask() && task?.dueDate != null) {

                Text(
                    text = stringResource(R.string.task_due_date),
                    style = MaterialTheme.typography.body2,
                    color = Color.White.copy(0.92F),
                    modifier = Modifier.constrainAs(dueDateTextPosition) {
                        if (task.description.isNotEmpty()) {
//                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                            top.linkTo(descriptionPosition.bottom, 16.dp)
//                            else
//                                top.linkTo(descriptionPosition.bottom, 24.dp)
                        } else {
                            if (note?.tagNumber1?.isBlank() ?: return@constrainAs && note.tagNumber2?.isBlank() ?: return@constrainAs && note.tagNumber3?.isBlank() ?: return@constrainAs)
                                top.linkTo(titlePosition.bottom, 32.dp)
                            else
                                top.linkTo(titlePosition.bottom, 16.dp)
                        }
                        start.linkTo(titlePosition.start)
                    })

                Text(
                    text = task.dueDate.toString(),
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
                        if (itemIsTask())
                            if (task?.dueDate != null)
                                top.linkTo(dueDateTextPosition.bottom, 16.dp)
                            else
                                top.linkTo(descriptionPosition.bottom, 16.dp)
                        else
                            top.linkTo(descriptionPosition.bottom, 16.dp)
                        if (note != null) {
                            if (note.description.length < 40)
                                bottom.linkTo(parent.bottom, 12.dp)
                        }

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

                    if (note?.tagNumber1?.isNotBlank() ?: return@Row && columnLayoutWidth < itemWidth - 20.dp) { // TODO !! TAGS WILL OVERFLOW !!
                        myTag(
                            note.tagNumber1,
                            Color.White,
                            borderStroke = 1.dp
                        )
                    }
                    if (note.tagNumber2?.isNotBlank() ?: return@Row && columnLayoutWidth < itemWidth - 20.dp) {
                        myTag(
                            note.tagNumber2,
                            Color.White,
                            borderStroke = 1.dp
                        )
                    }

                    if (note.tagNumber3?.isNotEmpty() ?: return@Row && columnLayoutWidth < itemWidth - 20.dp) {
                        myTag(
                            note.tagNumber3,
                            Color.White,
                            borderStroke = 1.dp
                        )
                    }

                }
                if (note?.tagNumber1?.isNotEmpty() ?: return@Column && columnLayoutWidth > itemWidth - 20.dp) {
                    myTag(
                        note.tagNumber1,
                        Color.White,
                        borderStroke = 1.dp
                    )

                }

                if (columnLayoutWidth > itemWidth - 20.dp) {
                    note.tagNumber2?.let {
                        myTag(
                            it,
                            Color.White,
                            borderStroke = 1.dp
                        )
                    }

                }

                if (columnLayoutWidth > itemWidth - 20.dp) {
                    note.tagNumber3?.let {
                        myTag(
                            it,
                            Color.White,
                            borderStroke = 1.dp
                        )
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
fun ChipsSection(itemsAreTasks: Boolean, viewModel: HomeScreenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var selectedOrder by rememberSaveable { mutableStateOf("") }
    val tagsName = arrayListOf<String>()


    val taskOrderList = arrayOf(
        context.getString(R.string.txt_main_tags),
        context.getString(R.string.txt_main_importance),
        context.getString(R.string.txt_main_date),
        context.getString(R.string.txt_completed_tasks),
        context.getString(R.string.txt_undone),
        context.getString(R.string.txt_main_ascending),
        context.getString(R.string.txt_main_descending)
    )
    val noteOrderList = arrayOf(
        context.getString(R.string.txt_main_tags),
        context.getString(R.string.txt_main_ascending),
        context.getString(R.string.txt_main_descending)
    )

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


            items(
                if (itemsAreTasks) taskOrderList else noteOrderList
            ) { item ->


                val selectedItemAnimation by animateColorAsState(
                    targetValue = if (selectedOrder == item) MaterialTheme.colors.primary.copy(
                        0.6f
                    )
                    else MaterialTheme.colors.onSurface.copy(
                        0.1f
                    ), label = "selectedItemAnimation"
                )
                Chip(
                    onClick = {
                        if (currentPage == 0)
                            when (item) {
                                context.getString(R.string.txt_main_tags) -> {
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.HomeScreenTasksOrder(
                                            TaskOrderType.Tags(tagsName)
                                        )
                                    )
                                }

                                context.getString(R.string.txt_main_importance) -> {
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.HomeScreenTasksOrder(
                                            TaskOrderType.Priority
                                        )
                                    )
                                }

                                context.getString(R.string.txt_main_date) -> {
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.HomeScreenTasksOrder(
                                            TaskOrderType.DueDate
                                        )
                                    )
                                }

                                context.getString(R.string.txt_completed_tasks) -> {
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.HomeScreenTasksOrder(
                                            TaskOrderType.Completed
                                        )
                                    )
                                }

                                context.getString(R.string.txt_undone) -> {
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.HomeScreenTasksOrder(
                                            TaskOrderType.Undone
                                        )
                                    )
                                }

                                context.getString(R.string.txt_main_ascending) -> {
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.HomeScreenTasksOrder(
                                            TaskOrderType.Ascending
                                        )
                                    )
                                }

                                context.getString(R.string.txt_main_descending) -> {
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.HomeScreenTasksOrder(
                                            TaskOrderType.Descending
                                        )
                                    )
                                }
                            }
                        else {
                            when (item) {
                                context.getString(R.string.txt_main_tags) -> {
                                    viewModel.onEvent(
                                        HomeScreenNotesEvent.OrderNote(
                                            NoteOrderType.Tags(tagsName),
                                            tagOrderIsActive = true
                                        )
                                    )
                                }

                                context.getString(R.string.txt_main_ascending) -> {
                                    viewModel.onEvent(HomeScreenNotesEvent.OrderNote(NoteOrderType.Ascending))
                                }

                                context.getString(R.string.txt_main_descending) -> {
                                    viewModel.onEvent(HomeScreenNotesEvent.OrderNote(NoteOrderType.Descending))
                                }
                            }
                        }
                        selectedOrder = item
                        showTagsDialog = selectedOrder == context.getString(R.string.txt_main_tags)


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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsListSection(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    itemsAreTasks: Boolean,
    navController: NavController
) {


    val lazyListState = rememberLazyListState()

    isNotScrollingUp = lazyListState.isNotScrollingUp()

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(top = 56.dp, bottom = 60.dp),

        content = {

            item { ChipsSection(itemsAreTasks) }

            if (currentPage == 0)
                items(
                    items = viewModel.taskState.value.tasks, key = { it.id!! }) { taskItem ->
                    TaskOrNote(
                        modifier = Modifier.animateItemPlacement(),
                        task = taskItem,
                        onDelete = {
                            viewModel.onEvent(HomeScreenTasksEvent.DeleteTask(taskItem))
                        },
                        onEdit = {
                            navController.navigate("add_note_screen?id=${taskItem.id}")
                        })
                }
            else
                items(
                    items = viewModel.noteState.value.notes
                ) { noteItem ->
                    TaskOrNote(
                        modifier = Modifier.animateItemPlacement(),
                        itemIsTask = { false },
                        note = noteItem,
                        onDelete = {
                            viewModel.onEvent(HomeScreenNotesEvent.DeleteNote(noteItem))
                        },
                        onEdit = {
                            navController.navigate("add_note_screen?id=${noteItem.id}")
                        })
                }
        })

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyViewPager(
    navController: NavController,
    pagerState: PagerState,
    modifier: Modifier,
) {

    HorizontalPager(state = pagerState, userScrollEnabled = false) { position ->

        when (position) {
            0 -> Tasks(navController = navController)
            1 -> Notes(navController = navController)
        }


    }
}


@Composable
fun Notes(elements: HomeScreenViewModel = hiltViewModel(), navController: NavController) {

    AnimatedVisibility(
        visible = elements.noteState.value.notes.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        EmptyState()
    }

    ItemsListSection(itemsAreTasks = false, navController = navController)
    Log.i(TAG, "Notes: ")
}

@Composable
fun Tasks(elements: HomeScreenViewModel = hiltViewModel(), navController: NavController) {

    AnimatedVisibility(
        visible = elements.taskState.value.tasks.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        EmptyState()
    }

    ItemsListSection(itemsAreTasks = true, navController = navController)
    Log.i(TAG, "Tasks: ")
}

@Composable
fun myTag(
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
                    interactionSource = remember { MutableInteractionSource() }
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
fun MyTopAppbar(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
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
                IconButton(onClick = {
                    searchBtnIsClicked = true
                    viewModel.onEvent(HomeScreenNotesEvent.ToggleSearchbarVisibility)
                }) {
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    var taskIsSelected by rememberSaveable { mutableStateOf(true) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        2
    }
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
                        .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    backgroundColor = Color.Transparent
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (themeIsDark) elevatedSurface else MaterialTheme.colors.surface)

                    ) {
                        BottomNavigationItem(
                            selected = taskIsSelected,
                            alwaysShowLabel = false,
                            onClick = {
                                coroutineScope.launch {
                                    taskIsSelected = true
                                    pagerState.animateScrollToPage(0)
                                    viewModel.onEvent(HomeScreenTasksEvent.TaskScreenLoaded)

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
                                    viewModel.onEvent(HomeScreenNotesEvent.NoteScreenLoaded)

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

            MyViewPager(navController,
                pagerState = pagerState,
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
fun SearchTextField(viewModel: HomeScreenViewModel = hiltViewModel()) {

    var value by remember { mutableStateOf("") }

    Box(Modifier.fillMaxWidth()) {
        TextField(
            value = value, modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), trailingIcon = {
                IconButton(onClick = {
                    searchBtnIsClicked = false
                    viewModel.onEvent(HomeScreenNotesEvent.ClearSearchValue)
                }) {
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
            onValueChange = {
                value = it
                viewModel.onEvent(HomeScreenNotesEvent.SearchValueChanged(value))
            }
        )
        AnimatedVisibility(
            visible = viewModel.noteState.value.searchBarValue.isBlank(),
            exit = fadeOut(),
            enter = fadeIn()
        ) {
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
            targetState = selectedTasks.size,
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
                selectedTasks.clear()
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
fun MyTagPreview() {
    myTag("Daily", Color.Blue)
}
