package com.A_23_59.hypernote

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.A_23_59.hypernote.destinations.SettingsPageDestination
import com.A_23_59.hypernote.destinations.SortDialogDestination
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

private const val TAG = "Tags"

enum class ThemeState {
    DARK, LIGHT, GLASSMORPHISM
}

var themeSpecifier = ThemeSpecifier(ThemeState.GLASSMORPHISM)

var isScrollingUp by mutableStateOf(false)

var searchBtnIsClicked by mutableStateOf(false)

var selectBtnIsClicked by mutableStateOf(false)

val showTopAppBar by derivedStateOf { !selectBtnIsClicked && !searchBtnIsClicked }

data class Item(val title: String, val description: String, val color: Color)

val itemList = ArrayList<Item>()


@Composable
fun SingleItem(

    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
    priorityColor: Color
) {

    //TODO adding remember state to redraw the Composable

    Box(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 2.dp)
    ) {
        if (themeSpecifier.themeState != ThemeState.GLASSMORPHISM) {
            Card(
                modifier = Modifier.wrapContentHeight(),
                elevation = 5.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                ConstraintLayout(
                    Modifier
                        .fillMaxSize()
                ) {

                    val (titlePosition, descriptionPosition, priorityPosition, checkboxPosition, datePosition, taskTypePosition, tagsPosition) = createRefs()

                    Box(
                        modifier = Modifier
                            .background(
                                priorityColor,
                                RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            )
                            .constrainAs(priorityPosition) {
                                width = Dimension.value(15.dp)
                                height = Dimension.fillToConstraints
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                                top.linkTo(parent.top)

                            }
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .constrainAs(titlePosition) {

                                start.linkTo(priorityPosition.end, 8.dp)
                                top.linkTo(parent.top, 2.dp)
                                end.linkTo(taskTypePosition.start, 24.dp)
                                width = Dimension.fillToConstraints


                            }
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_repeat_24),
                        contentDescription = "repeat",
                        modifier = Modifier.constrainAs(taskTypePosition) {
                            end.linkTo(parent.end, 16.dp)
                            top.linkTo(parent.top, 8.dp)
                        })
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .constrainAs(descriptionPosition) {
                                    width = Dimension.fillToConstraints
                                    height = Dimension.wrapContent
                                    top.linkTo(titlePosition.bottom, 8.dp)
                                    start.linkTo(priorityPosition.end, 8.dp)
                                    end.linkTo(checkboxPosition.start, 24.dp)
                                    bottom.linkTo(datePosition.top, 16.dp)

                                })
                    }

                    Checkbox(
                        checked = false,
                        onCheckedChange = {},
                        modifier = Modifier.constrainAs(checkboxPosition) {
                            end.linkTo(parent.end, 4.dp)
                            bottom.linkTo(parent.bottom)
                        })

                    Text(
                        text = "11/27/2022",
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.constrainAs(datePosition) {
                            start.linkTo(priorityPosition.end, 8.dp)
                            if (description.isNotEmpty()) top.linkTo(
                                descriptionPosition.bottom,
                                8.dp
                            ) else top.linkTo(titlePosition.bottom, 8.dp)

                        })

                    Row(Modifier.constrainAs(tagsPosition) {
                        width = Dimension.fillToConstraints
                        start.linkTo(priorityPosition.end, 8.dp)
                        end.linkTo(checkboxPosition.start, 8.dp)
                        top.linkTo(datePosition.top, 20.dp)
                        bottom.linkTo(parent.bottom, 8.dp)
                    }, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MyTag("Daily Tasks", Color.Red)
                        MyTag("Work", Color.Green)
                        MyTag("Study", Color.Blue)
                    }
                }
            }

        } else {
            ConstraintLayout(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.25F), RoundedCornerShape(12.dp))
            ) {

                val (titlePosition, descriptionPosition, priorityPosition, taskTypePosition, datePosition, checkboxPosition, tagsPosition) = createRefs()

                Box(
                    modifier = Modifier
                        .background(
                            priorityColor,
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                        .constrainAs(priorityPosition) {
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                            top.linkTo(parent.top)
                            width = Dimension.value(10.dp)
                            height = Dimension.fillToConstraints

                        }
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .constrainAs(titlePosition) {
                            start.linkTo(priorityPosition.end, 8.dp)
                            top.linkTo(parent.top, 4.dp)
                            end.linkTo(taskTypePosition.start, 16.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent

                        }
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .constrainAs(descriptionPosition) {
                                top.linkTo(titlePosition.bottom, 12.dp)
                                start.linkTo(priorityPosition.end, 8.dp)
                                end.linkTo(checkboxPosition.start)
                                width = Dimension.fillToConstraints
                            })

                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_repeat_24),
                    contentDescription = "repeat",
                    modifier = Modifier.constrainAs(taskTypePosition) {
                        top.linkTo(parent.top, 8.dp)
                        end.linkTo(parent.end, 16.dp)
                    })

                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.constrainAs(checkboxPosition) {
                        end.linkTo(parent.end, 4.dp)
                        bottom.linkTo(parent.bottom)
                    })

                Text(
                    text = "11/28/2022 2:46 AM",
                    color = Color(0xFFE9E9E9),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.constrainAs(datePosition) {
                        start.linkTo(priorityPosition.end, 8.dp)
                        if (description.isNotEmpty())
                            top.linkTo(descriptionPosition.bottom, 16.dp)
                        else top.linkTo(titlePosition.bottom, 8.dp)
                    })
                Row(Modifier.constrainAs(tagsPosition) {
                    width = Dimension.fillToConstraints
                    start.linkTo(priorityPosition.end, 8.dp)
                    end.linkTo(checkboxPosition.start, 16.dp)
                    bottom.linkTo(parent.bottom, 8.dp)
                    top.linkTo(datePosition.bottom, 8.dp)
                }, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MyTag("Daily Tasks", Color.Red)
                    MyTag("Study", Color.Green)
                    MyTag("Work", Color.Blue)
                }
            }
        }
    }

}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
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
fun ItemListSection(elements: List<Item>) {
    val lazyListState = rememberLazyListState()

    isScrollingUp = lazyListState.isScrollingUp()
    Log.i(TAG, "ItemListSection is called ")


    Column {
        LazyColumn(
            state = lazyListState,
            content = {
                items(elements, key = { it.title }) { item ->
                    Column {
                        SingleItem(
                            title = item.title,
                            description = item.description,
                            priorityColor = item.color
                        )
                        Box(Modifier.align(Alignment.End)) {
                            MyActionsSection()
                        }
                    }

                }

            },
        )
    }
}

@Composable
fun MyActionsSection(modifier: Modifier = Modifier) {
    Surface(
        Modifier.padding(end = 16.dp, bottom = 16.dp),
        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
        color = Color.Transparent.copy(0F)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_delete_outline_24),
                    contentDescription = "delete"
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_edit_24),
                    contentDescription = "edit"
                )
            }


        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyTabLayout(modifier: Modifier = Modifier) {

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()



    Row(Modifier.fillMaxWidth()) {
        Column() {
            TabRow(selectedTabIndex = pagerState.currentPage,
                backgroundColor = Color.Transparent,
                contentColor = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    .clip(
                        RoundedCornerShape(10.dp)
                    ),
                tabs = {

                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                        text = { Text(text = "TASKS") })
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                        text = { Text(text = "NOTES") })

                })
            HorizontalPager(count = 2, state = pagerState) {

                when (it) {
                    0 -> ItemListSection(elements = itemList)
                    1 -> Tasks()
                }
            }
        }


    }


}

@Composable
fun Tasks() {
    Text(text = "this is tasks page")
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
            text = tagTitle,
            modifier = Modifier.padding(all = 4.dp),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MyTopAppbar(modifier: Modifier = Modifier, navigator: DestinationsNavigator) {

    TopAppBar(
        title = { Text(text = "HyperNote") },
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
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.surface.copy(0F)
    )
}

@Composable
fun MyFab(navigator: () -> Unit) {
    AnimatedVisibility(visible = isScrollingUp, enter = fadeIn(), exit = fadeOut()) {
        FloatingActionButton(
            onClick = { navigator() },
            backgroundColor = if (themeSpecifier.themeState == ThemeState.LIGHT)
                MaterialTheme.colors.secondary
            else {
                Color.White
            },
            shape = RoundedCornerShape(15.dp), contentColor = MaterialTheme.colors.surface
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_add_24),
                contentDescription = "add"
            )
        }
    }

}

@Composable
fun SortSectionDisplay() {
    Surface(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = if (themeSpecifier.themeState != ThemeState.GLASSMORPHISM) 12.dp else 0.dp,
        color = if (themeSpecifier.themeState != ThemeState.GLASSMORPHISM) MaterialTheme.colors.secondary else Color.Transparent.copy(
            alpha = 0F
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = 0.dp)
                .height(IntrinsicSize.Min)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sort As : need a parameter",
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

@RootNavGraph(start = true)
@Destination
@Composable
fun HomePage(navigator: DestinationsNavigator) {
    val toastContext = LocalContext.current
    BackHandler() {
        Toast.makeText(toastContext, "press one more time to exit", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        floatingActionButton = { MyFab { navigator.navigate(SettingsPageDestination) } },
        floatingActionButtonPosition = FabPosition.Center,
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (themeSpecifier.themeState == ThemeState.GLASSMORPHISM) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(15.dp)


                ) {

                    Image(
                        painter = painterResource(id = R.drawable.abstract_2),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )

                }
            }
            Column(modifier = Modifier.fillMaxHeight()) {
                AnimatedVisibility(visible = selectBtnIsClicked) {
                    SelectItemsBtn()
                }

                AnimatedVisibility(visible = searchBtnIsClicked) {
                    SearchTextField()
                }
                AnimatedVisibility(visible = showTopAppBar) {
                    MyTopAppbar(navigator = navigator)
                }
                MyTabLayout()
                Spacer(modifier = Modifier.height(10.dp))
                SortSectionDisplay()
//                    ItemListSection(elements = itemList)
            }
        }
    }

}


@Composable
fun SearchTextField() {
    var textFieldValue by remember { mutableStateOf("") }
    Box(Modifier.fillMaxWidth()) {
        TextField(
            value = textFieldValue,
            shape = RoundedCornerShape(0.dp),
            onValueChange = { textFieldValue = it },
            placeholder = {
                Text(
                    text = "Search..."
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        IconButton(
            onClick = { searchBtnIsClicked = false },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(imageVector = Icons.Rounded.Close, contentDescription = "close")
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
fun SelectItemsBtn() {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White.copy(0.15F))
    ) {

        val (deleteIcon, selectAllIcon, closeIcon, markAsCheckedIcon, itemsSelectedText) = createRefs()

        Text(
            text = "0 Items Selected",
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
            Icon(imageVector = Icons.Rounded.Close, contentDescription = "check")
        }
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(deleteIcon) {
            end.linkTo(markAsCheckedIcon.start)
            bottom.linkTo(parent.bottom)
            top.linkTo(parent.top)
        }) {
            Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
        }

        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(selectAllIcon) {
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.round_checklist_24),
                contentDescription = "mark as checked"
            )
        }
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(markAsCheckedIcon) {
            end.linkTo(selectAllIcon.start)
            bottom.linkTo(parent.bottom)
            top.linkTo(parent.top)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.round_check_box_24),
                contentDescription = "check all"
            )
        }
    }


}

@Destination(style = MyDialogStyle::class)
@Composable
fun SortDialog() {
    SortD()
}

@Composable
fun SortD() {
    Card(
        Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White.copy(0.6F),
        contentColor = Color.Black
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "this is the sort dialog")
        }
    }
}

@Destination
@Composable
fun EditPage(navigator: DestinationsNavigator) {

}


@Composable
fun SettingsP() {
    if (themeSpecifier.themeState == ThemeState.GLASSMORPHISM) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(15.dp)


        ) {

            Image(
                painter = painterResource(id = R.drawable.abstract_2),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

        }
    } else {
        Surface(Modifier.fillMaxSize()) {
            Text(text = "This is Settings Page")
        }
    }
}

@Preview
@Composable
fun SettingsPreview() {
    SettingsP()
}

@Preview
@Composable
fun SortDialogPreview() {
    SortD()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchPreview() {
    SearchTextField()
}

@Preview
@Composable
fun SelectItemsBtnPreview() {
    SelectItemsBtn()
}

@Preview
@Composable
fun SortSectionPreview() {
    SortSectionDisplay()
}

@Preview
@Composable
fun MyActionSectionPreview() {
    MyActionsSection()
}


@Preview
@Composable
fun ItemListSectionPreview() {

    ItemListSection(itemList)
}

@Preview
@Composable
fun MyTagPreview() {
    MyTag("Daily", Color.Blue)
}

@Preview
@Composable
fun SingleItemPreview() {
    SingleItem(
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