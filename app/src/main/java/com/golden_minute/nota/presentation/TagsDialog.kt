package com.golden_minute.nota.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.golden_minute.nota.R
import com.golden_minute.nota.domain.model.Tag
import com.golden_minute.nota.domain.util.HomeScreenNotesEvent
import com.golden_minute.nota.domain.util.HomeScreenTasksEvent
import com.golden_minute.nota.ui.theme.deleteWarning
import kotlinx.coroutines.launch


var showTagsDialog by mutableStateOf(false)


@Composable
fun TagsSelectionDialog(viewModel: HomeScreenViewModel) {

    val coroutineScope = rememberCoroutineScope()

    val selectedTags =
        remember {
            mutableStateListOf<Tag>()
        }


    var enableApplyButton by rememberSaveable { mutableStateOf(false) }



    Dialog(onDismissRequest = { showTagsDialog = false }) {
        Card(shape = RoundedCornerShape(15.dp), modifier = Modifier.padding(horizontal = 24.dp)) {
            if (viewModel.tagsList.any { it.taskID != null }&& currentPage == 0)
                Column {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 16.dp), state = rememberLazyListState()
                    ) {

                        items(if (currentPage == 0)
                            viewModel.tagsList.filter { it.taskID != null }
                                .distinctBy { it.tagName }
                        else viewModel.tagsList.filter { it.noteID != null }
                            .distinctBy { it.tagName },
                            key = { it.tagName }) { tag ->


                            var currentTagIsChecked by rememberSaveable { mutableStateOf(false) }
                            enableApplyButton = selectedTags.isNotEmpty()


                            Column(Modifier.fillMaxSize()) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            currentTagIsChecked = !currentTagIsChecked

                                            if (currentTagIsChecked && selectedTags.none { it.tagName == tag.tagName })
                                                selectedTags.add(tag)
                                            else
                                                selectedTags.remove(tag)


//                                        tag.isChecked = currentTagIsChecked
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(modifier = Modifier.padding(start = 8.dp),
                                        colors = CheckboxDefaults.colors(
                                            MaterialTheme.colors.primary,
                                            checkmarkColor = Color.White
                                        ),
                                        checked = currentTagIsChecked,
                                        onCheckedChange = {
                                            currentTagIsChecked = !currentTagIsChecked
                                            tag.isChecked = currentTagIsChecked
                                            if (currentTagIsChecked)
                                                selectedTags.add(tag)
                                            else
                                                selectedTags.remove(tag)
                                        })
                                    Text(
                                        text = tag.tagName,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 14.dp
                                        )
                                    )
                                }
                                Divider(color = MaterialTheme.colors.onSurface.copy(0.3f))


                            }

                        }


                    }
                    if (viewModel.tagsList.isNotEmpty()) {

                        Button(
                            onClick = {
                                if (currentPage == 1)
                                    viewModel.onEvent(
                                        HomeScreenNotesEvent.OnNotesFilteredByTags(
                                            selectedTags
                                        )
                                    )
                                else
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.OnTasksFilteredByTags(
                                            selectedTags
                                        )
                                    )
                                showTagsDialog = false
                            },
                            enabled = enableApplyButton,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.filter_tags),
                                color = Color.White
                            )
                        }
                        OutlinedButton(enabled = enableApplyButton,
                            border = BorderStroke(
                                ButtonDefaults.OutlinedBorderSize,
                                color = if (enableApplyButton) deleteWarning else MaterialTheme.colors.onSurface.copy(
                                    0.2f
                                )
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = deleteWarning),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 6.dp),
                            onClick = {
                                coroutineScope.launch {


                                    if (currentPage == 1)
                                        viewModel.onEvent(
                                            HomeScreenNotesEvent.OnDeleteTagsInDialog(
                                                selectedTags
                                            )
                                        )
                                    else
                                        viewModel.onEvent(
                                            HomeScreenTasksEvent.OnDeleteTagsInDialog(
                                                selectedTags
                                            )
                                        )
//                                    viewModel.tagsUseCases.deleteTags(selectedTags)

//                                    viewModel.getTags()
                                    showTagsDialog = false
                                }
                            }) {
                            Text(text = stringResource(R.string.delete_tags))
                        }


                    }

                }
            else if (viewModel.tagsList.any { it.noteID != null } && currentPage == 1)
                Column {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 16.dp), state = rememberLazyListState()
                    ) {

                        items(if (currentPage == 0)
                            viewModel.tagsList.filter { it.taskID != null }
                                .distinctBy { it.tagName }
                        else viewModel.tagsList.filter { it.noteID != null }
                            .distinctBy { it.tagName },
                            key = { it.tagName }) { tag ->


                            var currentTagIsChecked by rememberSaveable { mutableStateOf(false) }
                            enableApplyButton = selectedTags.isNotEmpty()


                            Column(Modifier.fillMaxSize()) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            currentTagIsChecked = !currentTagIsChecked

                                            if (currentTagIsChecked && selectedTags.none { it.tagName == tag.tagName })
                                                selectedTags.add(tag)
                                            else
                                                selectedTags.remove(tag)


//                                        tag.isChecked = currentTagIsChecked
                                        }, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(modifier = Modifier.padding(start = 8.dp),
                                        colors = CheckboxDefaults.colors(
                                            MaterialTheme.colors.primary,
                                            checkmarkColor = Color.White
                                        ),
                                        checked = currentTagIsChecked,
                                        onCheckedChange = {
                                            currentTagIsChecked = !currentTagIsChecked
                                            tag.isChecked = currentTagIsChecked
                                            if (currentTagIsChecked)
                                                selectedTags.add(tag)
                                            else
                                                selectedTags.remove(tag)
                                        })
                                    Text(
                                        text = tag.tagName,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 14.dp
                                        )
                                    )
                                }
                                Divider(color = MaterialTheme.colors.onSurface.copy(0.3f))


                            }

                        }


                    }
                    if (viewModel.tagsList.isNotEmpty()) {

                        Button(
                            onClick = {
                                if (currentPage == 1)
                                    viewModel.onEvent(
                                        HomeScreenNotesEvent.OnNotesFilteredByTags(
                                            selectedTags
                                        )
                                    )
                                else
                                    viewModel.onEvent(
                                        HomeScreenTasksEvent.OnTasksFilteredByTags(
                                            selectedTags
                                        )
                                    )
                                showTagsDialog = false
                            },
                            enabled = enableApplyButton,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.filter_tags),
                                color = Color.White
                            )
                        }
                        OutlinedButton(enabled = enableApplyButton,
                            border = BorderStroke(
                                ButtonDefaults.OutlinedBorderSize,
                                color = if (enableApplyButton) deleteWarning else MaterialTheme.colors.onSurface.copy(
                                    0.2f
                                )
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = deleteWarning),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 6.dp),
                            onClick = {
                                coroutineScope.launch {


                                    if (currentPage == 1)
                                        viewModel.onEvent(
                                            HomeScreenNotesEvent.OnDeleteTagsInDialog(
                                                selectedTags
                                            )
                                        )
                                    else
                                        viewModel.onEvent(
                                            HomeScreenTasksEvent.OnDeleteTagsInDialog(
                                                selectedTags
                                            )
                                        )
//                                    viewModel.tagsUseCases.deleteTags(selectedTags)

//                                    viewModel.getTags()
                                    showTagsDialog = false
                                }
                            }) {
                            Text(text = stringResource(R.string.delete_tags))
                        }


                    }

                }
            else
                Box(Modifier.height(300.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.tag_s_list_is_empty),
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }
        }
    }

}

