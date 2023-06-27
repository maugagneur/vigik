package com.kidor.vigik.ui.hub

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from hub view.
 */
sealed class HubViewAction : ViewAction {

    /**
     * Display the emulate tag view.
     */
    object DisplayEmulateTagView : HubViewAction()

    /**
     * Display the scan view.
     */
    object DisplayScanTagView : HubViewAction()

    /**
     * Display the tag history view.
     */
    object DisplayTagHistoryView : HubViewAction()

    /**
     * Display the biometric view.
     */
    object DisplayBiometricView : HubViewAction()
}
