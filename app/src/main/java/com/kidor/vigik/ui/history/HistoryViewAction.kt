package com.kidor.vigik.ui.history

import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from history view.
 */
sealed class HistoryViewAction : ViewAction {

    /**
     * Delete a tag from database.
     *
     * @param tag The tag to delete.
     */
    data class DeleteTag(val tag: Tag) : HistoryViewAction()
}
