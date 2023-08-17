package com.kidor.vigik.ui.emoji

import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val DEFAULT_EMOJI = "❔"

/**
 * Business logic of Emoji screen.
 */
@HiltViewModel
class EmojiViewModel @Inject constructor() : BaseViewModel<Nothing, EmojiViewState, Nothing>() {

    init {
        _viewState.value = EmojiViewState.SelectedEmoji(DEFAULT_EMOJI)
    }
}
