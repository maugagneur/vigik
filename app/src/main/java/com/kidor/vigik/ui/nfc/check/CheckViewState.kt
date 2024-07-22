package com.kidor.vigik.ui.nfc.check

import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of check view.
 */
sealed interface CheckViewState : ViewState {

    /**
     * State when the NFC state is checked.
     */
    data object Loading : CheckViewState

    /**
     * State when the NFC reader is seen as disabled.
     */
    data object NfcIsDisable : CheckViewState
}
