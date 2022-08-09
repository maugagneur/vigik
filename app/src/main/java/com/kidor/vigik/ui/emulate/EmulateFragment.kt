package com.kidor.vigik.ui.emulate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.fragment.app.viewModels
import com.kidor.vigik.ui.base.BaseFragment
import com.kidor.vigik.ui.compose.AppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * View that emulate a NFC tag.
 */
@AndroidEntryPoint
class EmulateFragment : BaseFragment<Nothing, EmulateViewState, Nothing, EmulateViewModel>() {

    override val viewModel by viewModels<EmulateViewModel>()

    @Composable
    override fun StateRender(viewState: EmulateViewState) {
        if (viewState is EmulateViewState.DisplayLogLines) {
            DisplayLogLineState(viewState.logLines)
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun DisplayLogLineState(@PreviewParameter(LogProvider::class) logLines: String) {
    Box(
        modifier = Modifier.padding(AppTheme.dimensions.commonSpaceSmall),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = logLines,
            modifier = Modifier.fillMaxWidth(),
            fontSize = AppTheme.dimensions.textSizeMedium
        )
    }
}
