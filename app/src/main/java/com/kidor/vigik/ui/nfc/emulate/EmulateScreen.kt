package com.kidor.vigik.ui.nfc.emulate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that emulate a NFC tag.
 */
@Composable
fun EmulateScreen(viewModel: EmulateViewModel = hiltViewModel()) {
    ObserveViewState(viewModel) { state ->
        if (state is EmulateViewState.DisplayLogLines) {
            DisplayLogLineState(state.logLines)
        }
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
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
    }
}
