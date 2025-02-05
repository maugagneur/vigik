package com.kidor.vigik.ui.nfc.scan

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.CollectViewEvent
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions

internal const val FLOATING_ACTION_BUTTON_TEST_TAG = "Floating action button"
internal const val TAG_DATA_TEXT_TEST_TAG = "Tag data text"
internal const val PROGRESS_BAR_TEST_TAG = "Progress bar"

/**
 * View that scan for a NFC tag.
 */
@Composable
fun ScanScreen(viewModel: ScanViewModel = hiltViewModel()) {
    val context = LocalContext.current
    ObserveViewState(viewModel) { state ->
        when (state) {
            is ScanViewState.DisplayTag ->
                DisplayTagState(
                    DisplayTagStateData(
                        state = state,
                        onSaveTagClick = { viewModel.handleAction(ScanViewAction.SaveTag) }
                    )
                )
            ScanViewState.Loading -> LoadingState()
        }
    }
    CollectViewEvent(viewModel) { event ->
        when (event) {
            ScanViewEvent.SaveTagFailure -> promptMessage(context, R.string.nfc_save_tag_fail)
            ScanViewEvent.SaveTagSuccess -> promptMessage(context, R.string.nfc_save_tag_success)
        }
    }
}

private fun promptMessage(context: Context, @StringRes resId: Int) {
    Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun DisplayTagState(
    @PreviewParameter(DisplayTagStateDataProvider::class) displayTagStateData: DisplayTagStateData
) {
    Scaffold(
        floatingActionButton = {
            if (displayTagStateData.state.canBeSaved) {
                FloatingActionButton(
                    onClick = displayTagStateData.onSaveTagClick,
                    modifier = Modifier.testTag(FLOATING_ACTION_BUTTON_TEST_TAG),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                Text(
                    text = displayTagStateData.state.tag.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.dimensions.commonSpaceXSmall)
                        .testTag(TAG_DATA_TEXT_TEST_TAG),
                    fontSize = MaterialTheme.dimensions.textSizeMedium
                )
            }
        }
    )
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
