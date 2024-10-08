package com.kidor.vigik.ui.emoji

import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of Emoji view.
 */
sealed interface EmojiViewState : ViewState {

    /**
     * State when the selected Emoji is shown.
     *
     * @param emoji The Emoji selected.
     */
    data class SelectedEmoji(val emoji: String) : EmojiViewState

    /**
     * State to select a new Emoji.
     */
    data object EmojiPicker : EmojiViewState
}
