package com.kidor.vigik.ui.emoji

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.ui.base.ObserveViewState

/**
 * View that display the section dedicated to Emoji.
 */
@Composable
fun EmojiScreen(
    viewModel: EmojiViewModel = hiltViewModel()
) {
    ObserveViewState(viewModel) { state ->
        when (state) {
            is EmojiViewState.SelectedEmoji -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.emoji,
                        modifier = Modifier.clickable { viewModel.handleAction(EmojiViewAction.ClickOnEmoji) },
                        fontSize = 64.sp
                    )
                }
            }
        }
    }
}
