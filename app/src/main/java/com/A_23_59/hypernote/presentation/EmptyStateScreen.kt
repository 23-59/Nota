package com.A_23_59.hypernote.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.A_23_59.hypernote.R
import com.A_23_59.hypernote.ui.theme.iranYekan

@Composable
fun EmptyState() {
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = if (currentPage ==0) painterResource(id = R.drawable.check_square_broken) else painterResource(
                    id = R.drawable.file_06
                ),
                contentDescription = "",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(if (currentPage == 0) R.string.task_list_is_empty else R.string.note_list_is_empty),
                fontFamily = iranYekan,
                style = MaterialTheme.typography.h5
            )
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun EmptyStatePreview() {
    EmptyState()
}