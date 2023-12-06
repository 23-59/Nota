package com.A_23_59.hypernote.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.A_23_59.hypernote.R
import com.A_23_59.hypernote.currentPage
import com.A_23_59.hypernote.notesTagsList
import com.A_23_59.hypernote.tasksTagsList

data class Tag(var tagName: String, var isChecked: Boolean = false)

var showTagsDialog by mutableStateOf(false)

@Composable
fun TagsSelectionDialog() {

    var enableApplyButton by rememberSaveable { mutableStateOf(false) }

    Dialog(onDismissRequest = { showTagsDialog = false }) {
        Card(shape = RoundedCornerShape(15.dp), modifier = Modifier.padding(horizontal = 24.dp)) {
            Column {
                LazyColumn(Modifier.height(300.dp), state = rememberLazyListState()) {

                    items(if (currentPage == 0) tasksTagsList else notesTagsList) { tag ->

                        var currentTagIsChecked by rememberSaveable { mutableStateOf(tag.isChecked) }
                        enableApplyButton = if (currentPage == 0)
                            tasksTagsList.any { it.isChecked }
                        else
                            notesTagsList.any { it.isChecked }


                        Column(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentTagIsChecked = !currentTagIsChecked
                                        tag.isChecked = currentTagIsChecked
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
                                    })
                                Text(
                                    text = tag.tagName,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)
                                )
                            }
                            Divider(color = MaterialTheme.colors.onSurface.copy(0.3f))
                        }
                    }


                }
                Button(
                    onClick = { showTagsDialog = false },
                    enabled = enableApplyButton,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.filter_tags),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TagsDialogPreview() {
    TagsSelectionDialog()
}