package com.kidor.vigik.ui.history

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.extensions.toHex
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme
import com.kidor.vigik.ui.usecases.FormatDateUseCase

internal const val DELETE_ICON_TEST_TAG_PREFIX = "Delete tag "
internal const val TAGS_LIST_TEST_TAG = "Tags list"
internal const val TAGS_LIST_ROW_TEST_TAG = "Tags list row"
internal const val PROGRESS_BAR_TEST_TAG = "Progress bar"

/**
 * View that display all tags saved in the database.
 */
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    ObserveViewState(viewModel) { state ->
        when (state) {
            HistoryViewState.Initializing -> LoadingState()
            is HistoryViewState.DisplayTags -> DisplayTags(
                DisplayTagsStateData(
                    tags = state.tags,
                    onViewAction = { action -> viewModel.handleAction(action) }
                )
            )
            HistoryViewState.NoTag -> DisplayTags(DisplayTagsStateData(emptyList()))
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag(PROGRESS_BAR_TEST_TAG),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun DisplayTags(@PreviewParameter(DisplayTagsStateProvider::class) displayTagsStateData: DisplayTagsStateData) {
    if (displayTagsStateData.tags.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_data_label),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.testTag(TAGS_LIST_TEST_TAG)
        ) {
            items(displayTagsStateData.tags) { tag ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TAGS_LIST_ROW_TEST_TAG),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FormatDateUseCase().invoke(tag.timestamp),
                        modifier = Modifier.weight(0.5f),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = AppTheme.dimensions.textSizeMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = tag.uid?.toHex() ?: "",
                        modifier = Modifier.weight(0.3f),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = AppTheme.dimensions.textSizeMedium,
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = { displayTagsStateData.onViewAction(HistoryViewAction.DeleteTag(tag)) },
                        modifier = Modifier
                            .weight(0.2f)
                            .testTag(DELETE_ICON_TEST_TAG_PREFIX + (tag.timestamp ?: 0))
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete tag",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
