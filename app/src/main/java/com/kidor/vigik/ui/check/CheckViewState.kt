package com.kidor.vigik.ui.check

/**
 * Possible states of check view.
 */
sealed class CheckViewState {

    /**
     * State when the NFC state is checked.
     */
    object Loading: CheckViewState()

    /**
     * State when the NFC reader is seen as disabled.
     */
    object NfcIsDisable: CheckViewState()

    override fun toString(): String = javaClass.simpleName
}