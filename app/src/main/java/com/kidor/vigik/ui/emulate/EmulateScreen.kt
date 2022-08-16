package com.kidor.vigik.ui.emulate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that emulate a NFC tag.
 */
@Composable
fun EmulateScreen(viewModel: EmulateViewModel = hiltViewModel()) {
    viewModel.viewState.observeAsState().let {
        it.value?.let { state -> StateRender(state) }
    }
}

@Composable
private fun StateRender(viewState: EmulateViewState) {
    if (viewState is EmulateViewState.DisplayLogLines) {
        DisplayLogLineState(viewState.logLines)
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun DisplayLogLineState(@PreviewParameter(LogProvider::class) logLines: String) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(AppTheme.dimensions.commonSpaceSmall),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = logLines,
            modifier = Modifier.fillMaxWidth(),
            fontSize = AppTheme.dimensions.textSizeMedium
        )
    }
}
