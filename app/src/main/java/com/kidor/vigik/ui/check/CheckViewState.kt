package com.kidor.vigik.ui.check

import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of check view.
 */
sealed class CheckViewState : ViewState() {

    /**
     * State when the NFC state is checked.
     */
    object Loading : CheckViewState()

    /**
     * State when the NFC reader is seen as disabled.
     */
    object NfcIsDisable : CheckViewState()
}