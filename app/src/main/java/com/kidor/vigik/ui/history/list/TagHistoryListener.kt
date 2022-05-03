package com.kidor.vigik.ui.history.list

import com.kidor.vigik.nfc.model.Tag

/**
 * Callback used to forward interactions with each element of the tag history list.
 */
interface TagHistoryListener {

    /**
     * Called when clicking on the 'delete' icon of the tag element view.
     */
    fun onDeleteTagClick(tag: Tag)
}
