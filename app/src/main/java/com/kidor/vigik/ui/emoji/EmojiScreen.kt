package com.kidor.vigik.ui.emoji

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.ui.base.ObserveViewState

private const val EMOJI_GRID_COLUMN = 8
private const val EMOJI_SCALE_ANIMATION_DURATION = 1000
private const val EMOJI_SCALE_ANIMATION_INITIAL_VALUE = 1f
private const val EMOJI_SCALE_ANIMATION_TARGET_VALUE = 1.2f

/**
 * View that display the section dedicated to Emoji.
 */
@Composable
fun EmojiScreen(
    viewModel: EmojiViewModel = hiltViewModel()
) {
    ObserveViewState(viewModel) { state ->
        when (state) {
            EmojiViewState.EmojiPicker -> AndroidView(
                factory = { context ->
                    EmojiPickerView(context).apply {
                        emojiGridColumns = EMOJI_GRID_COLUMN
                        setOnEmojiPickedListener {
                            viewModel.handleAction(EmojiViewAction.ChangeSelectedEmoji(it.emoji))
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            is EmojiViewState.SelectedEmoji -> ShowEmoji(
                state = state,
                onEmojiClicked = { viewModel.handleAction(EmojiViewAction.ClickOnEmoji) }
            )
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 100)
private fun ShowEmoji(
    state: EmojiViewState.SelectedEmoji = EmojiViewState.SelectedEmoji("❔"),
    onEmojiClicked: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Scale infinite transition")
    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = EMOJI_SCALE_ANIMATION_INITIAL_VALUE,
        targetValue = EMOJI_SCALE_ANIMATION_TARGET_VALUE,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = EMOJI_SCALE_ANIMATION_DURATION),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale animation"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = state.emoji,
            modifier = Modifier
                .clickable(onClick = onEmojiClicked)
                .scale(scaleAnimation),
            fontSize = 64.sp
        )
    }
}
