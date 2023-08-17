package com.kidor.vigik.ui.emoji

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from Emoji view.
 */
sealed class EmojiViewAction : ViewAction {

    /**
     * Action when clicking on previously selected Emoji.
     */
    data object ClickOnEmoji : EmojiViewAction()

    /**
     * Action to change the selected Emoji.
     *
     * @param emoji The new Emoji selected.
     */
    data class ChangeSelectedEmoji(val emoji: String) : EmojiViewAction()
}
