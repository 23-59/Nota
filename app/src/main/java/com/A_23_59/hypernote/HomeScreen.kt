package com.A_23_59.hypernote

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.A_23_59.hypernote.destinations.AddPageDestination
import com.A_23_59.hypernote.destinations.SettingsPageDestination
import com.A_23_59.hypernote.ui.theme.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var isNotScrollingUp by mutableStateOf(false)

var searchBtnIsClicked by mutableStateOf(false)

var selectBtnIsClicked by mutableStateOf(false)

var showWelcomeScreen by mutableStateOf(true)


val showTopAppBar by derivedStateOf { !selectBtnIsClicked && !searchBtnIsClicked }


data class Item(
    val title: String,
    val description: String,
    val color: Color,
    val tagNumber1: String? = null,
    val tagNumber2: String? = null,
    val tagNumber3: String? = null,

    )


val taskList =
    ArrayList<Item>()  //TODO  IMPORTANT !!! your list will duplicate items because you don't have ViewModel


@Composable
fun TaskOrNote(
    title: String = "",
    description: String = "",
    priorityColor: Color? = null,
    tags: Array<String?> = arrayOf("", "", ""),
    itemIsTask: Boolean = true,

    ) {
    var expandButtonIsClicked by remember { mutableStateOf(false) }

    val gradientBackground = when (priorityColor) {
        Color.Red -> redGradient
        Gold200 -> goldGradient
        Color.Blue -> blueGradient
        else -> throw java.lang.IllegalArgumentException("the color of the background is invalid")
    }

    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), elevation = 11.dp
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .background(gradientBackground)

        ) {

            val (titlePosition, descriptionPosition, dividerPosition, checkboxPosition, expandButtonPosition, taskTypePosition, tagsPosition, creationDateTextPosition, creationDatePosition, dueDateTextPosition, dueDatePosition, deletePosition, editPosition) = createRefs()

            val endGuideLine = createGuidelineFromEnd(16.dp)
            Text(
                text = title, style = MaterialTheme.typography.h6,
                color = Color.White,
                modifier = Modifier
                    .constrainAs(titlePosition) {

                        start.linkTo(dividerPosition.end, 12.dp)
                        if (itemIsTask) {
                            top.linkTo(taskTypePosition.top)
                            end.linkTo(taskTypePosition.start, 24.dp)
                        } else {
                            top.linkTo(parent.top, 8.dp)
                            end.linkTo(parent.end, 8.dp)
                        }
                        width = Dimension.fillToConstraints

                    }
            )

            if (description.length >= 40) {
                val expandButtonIcon by remember { derivedStateOf { if (expandButtonIsClicked) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown } }
                AnimatedContent(
                    targetState = expandButtonIcon,
                    modifier = Modifier.constrainAs(expandButtonPosition) {


                        if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                            top.linkTo(creationDateTextPosition.bottom, 8.dp)
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
                AnimatedContent(targetState = if (expandButtonIsClicked) Int.MAX_VALUE else 1,
                    label = "descriptionAnimation", transitionSpec = {
                        ContentTransform(
                            fadeIn(), fadeOut()
                        )
                    }, modifier = Modifier.constrainAs(descriptionPosition) {
                        width = Dimension.fillToConstraints
                        top.linkTo(titlePosition.bottom, 12.dp)
                        start.linkTo(titlePosition.start)
                        end.linkTo(endGuideLine, 24.dp)
                    }
                ) {
                    Text(
                        text = description,
                        textAlign = TextAlign.Start,
                        maxLines = it,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White.copy(alpha = 0.90F),
                        style = MaterialTheme.typography.body1,


                        )
                }

            }
            if (itemIsTask) {
                Checkbox(
                    checked = false,
                    colors = CheckboxDefaults.colors(uncheckedColor = Color.White),
                    onCheckedChange = {},
                    modifier = Modifier.constrainAs(checkboxPosition) {
                        start.linkTo(parent.start)
                        top.linkTo(taskTypePosition.top)

                    })
            }

            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(deletePosition) {
                start.linkTo(parent.start)

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_delete_outline_24),
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
                    painter = painterResource(id = R.drawable.ic_outline_edit_24),
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

            if (itemIsTask && taskType == context.getString(R.string.temporary)) {

                Text(
                    text = stringResource(R.string.task_due_date),
                    style = MaterialTheme.typography.body2, fontStyle = FontStyle.Italic,
                    color = Color.White.copy(0.92F),
                    modifier = Modifier.constrainAs(dueDateTextPosition) {
                        if (description.isNotEmpty()) {
                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                                top.linkTo(descriptionPosition.bottom, 45.dp)
                            else
                                top.linkTo(descriptionPosition.bottom, 24.dp)
                        } else {
                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                                top.linkTo(titlePosition.bottom, 32.dp)
                            else
                                top.linkTo(titlePosition.bottom, 16.dp)
                        }
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
                        if (description.isNotEmpty()) {
                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                                top.linkTo(descriptionPosition.bottom, 32.dp)
                            else
                                top.linkTo(
                                    descriptionPosition.bottom,
                                    16.dp
                                )
                        } else
                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                                bottom.linkTo(parent.bottom, 12.dp)
                            else
                                top.linkTo(titlePosition.bottom, 16.dp)

                    } else {

                        if (itemIsTask) {
                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                                top.linkTo(dueDateTextPosition.bottom, 24.dp)
                            else
                                top.linkTo(
                                    dueDateTextPosition.bottom,
                                    16.dp
                                )
                        } else if (description.isNotEmpty()) {
                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                                top.linkTo(descriptionPosition.bottom, 24.dp)
                            else
                                top.linkTo(
                                    descriptionPosition.bottom,
                                    16.dp
                                )
                        } else

                            if (tagNumber1.isEmpty() && tagNumber2.isEmpty() && tagNumber3.isEmpty())
                                bottom.linkTo(parent.bottom, 12.dp)
                            else

                                top.linkTo(titlePosition.bottom, 16.dp)
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

            Column(
                Modifier
                    .constrainAs(tagsPosition) {
                        start.linkTo(dividerPosition.start, 12.dp)
                        end.linkTo(parent.end, 12.dp)
                        width = Dimension.fillToConstraints

                        top.linkTo(creationDateTextPosition.bottom, 16.dp)
                        if (description.length < 40)
                            bottom.linkTo(parent.bottom, 12.dp)

                    }
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (tagNumber1.isNotEmpty() && tagNumber1.length < 25) {
                        tags[0]?.let {
                            MyTag(
                                it,
                                Color.White,
                                borderStroke = 1.dp
                            ) {
                                tagNumber--
                                tagNumber1 = ""
                                return@MyTag Unit
                            }
                        }
                    }
                    if (tagNumber2.isNotEmpty() && tagNumber2.length < 20) {
                        tags[1]?.let {
                            MyTag(
                                it,
                                Color.White,
                                borderStroke = 1.dp
                            ) {
                                tagNumber--
                                tagNumber2 = ""
                                return@MyTag Unit
                            }
                        }
                    }

                    if (tagNumber3.isNotEmpty() && tagNumber3.length < 20) {
                        tags[2]?.let {
                            MyTag(
                                it,
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
                if (tagNumber1.isNotEmpty() && tagNumber1.length >= 25) {
                    tags[0]?.let {
                        MyTag(
                            it,
                            Color.White,
                            borderStroke = 1.dp
                        ) {
                            tagNumber--
                            tagNumber1 = ""
                            return@MyTag Unit
                        }
                    }

                }

                if (tagNumber2.length >= 20) {
                    tags[1]?.let {
                        MyTag(
                            it,
                            Color.White,
                            borderStroke = 1.dp
                        ) {
                            tagNumber--
                            tagNumber2 = ""
                            return@MyTag Unit
                        }
                    }

                }

                if (tagNumber3.length >= 20) {
                    tags[2]?.let {
                        MyTag(
                            it,
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


}


@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    val isScrollingToEnd by remember { derivedStateOf { layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1 } }
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
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

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipsSection() {
    val context = LocalContext.current
    val chipsList = arrayOf(
        context.getString(R.string.txt_main_importance),
        context.getString(R.string.txt_main_date),
        context.getString(R.string.txt_main_ascending),
        context.getString(R.string.txt_main_descending)
    )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (chipName in chipsList) {
                Chip(
                    onClick = { /*TODO*/ },
                    colors = ChipDefaults.chipColors(backgroundColor = MaterialTheme.colors.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
                ) {
                    Text(text = chipName)
                }
            }
        }



}

@Composable
fun ItemsListSection(elements: List<Item>, itemsAreTasks: Boolean) {
    val lazyListState = rememberLazyListState()
    var key = ""
    isNotScrollingUp = lazyListState.isScrollingUp()
    Column {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(top = 105.dp),

            content = {
                item { ChipsSection() }
                items(elements, key = {
                    key = it.title
                    it.title
                }, contentType = { it }) { item ->

                    Log.i(TAG, "ItemsListSection: this method is called and key is $key")
                    if (itemsAreTasks)
                        TaskOrNote(
                            item.title,
                            item.description,
                            item.color,
                            arrayOf(item.tagNumber1, item.tagNumber2, item.tagNumber3)
                        ) // this one is task
                    else
                        TaskOrNote(
                            item.title,
                            item.description,
                            Color.Blue,
                            tags = arrayOf(item.tagNumber1, item.tagNumber2, item.tagNumber3),
                            itemIsTask = false,
                        ) // this one is note


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
    HorizontalPager(state = pagerState) { position ->

        when (position) {
            0 -> Tasks()
            1 -> Notes()
        }
    }
}


@Composable
fun Notes() {
    ItemsListSection(elements = taskList, itemsAreTasks = false)
    Log.i(TAG, "Notes: ")
}

@Composable
fun Tasks() {

    ItemsListSection(elements = taskList, itemsAreTasks = true)
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

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyFab(pagerState: PagerState, navigator: () -> Unit) {

    AnimatedVisibility(
        visible = isNotScrollingUp,
        enter = slideInVertically() + fadeIn(),
        exit = fadeOut() + slideOutVertically()
    ) {
        AnimatedContent(targetState = pagerState.currentPage, label = "Test") {
            ExtendedFloatingActionButton(
                modifier = Modifier.shadow(
                    spotColor = if (themeIsDark) Color.White else Color.Black,
                    elevation = 7.dp,
                    shape = RoundedCornerShape(30.dp)
                ),
                text = {
                    Text(
                        text = if (pagerState.currentPage == 0) stringResource(id = R.string.add_new_task) else stringResource(
                            id = R.string.add_new_note
                        ),
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )


                }, shape = RoundedCornerShape(30.dp),
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
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { 2 }



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

@RequiresApi(Build.VERSION_CODES.N)
@Destination
@Composable
fun EditPage(navigator: DestinationsNavigator) {
    AddNewItem(edit_or_add = 'E', navigator = navigator)
}

@RequiresApi(Build.VERSION_CODES.N)
@Destination
@Composable
fun AddPage(navigator: DestinationsNavigator, pagerState: Int) {
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

    ItemsListSection(taskList, itemsAreTasks = true)
}

@Preview
@Composable
fun MyTagPreview() {
    MyTag("Daily", Color.Blue)
}

@Preview(locale = "fa")
@Composable
fun SingleTaskPreview() {
    TaskOrNote(
        title = "این اولین وظیفه ایه که من میخوام تو این برنامه تعریف کنم",
        description = "شاید این متن بلند بتونه کمکم کنه که ایراد های برنامه رو پیدا کنم",
        priorityColor = Color.Red
    )
}

@Preview
@Composable
fun SingleNotePreview() {
    TaskOrNote(
        "این اولین یادداشتی هست که میخوام برای این برنامه بنویسم",
        "شاید این متن ها به نظر عجیب و غیرمنطقی باشه ولی برای تست کردن ظاهر برنامه لازمه",
        Color.Blue,
        itemIsTask = false
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