package com.kidor.vigik.ui.scan

import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of scan view.
 */
sealed class ScanViewState : ViewState() {

    /**
     * Initial state when the NFC sensor is looking for tag.
     */
    object Loading : ScanViewState()

    /**
     * State when the view displays a tag.
     *
     * @param tag        The tag to display.
     * @param canBeSaved Indicate if the tag can be save in local database.
     */
    data class DisplayTag(val tag: Tag, val canBeSaved: Boolean) : ScanViewState()
}
