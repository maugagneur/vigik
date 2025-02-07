package com.kidor.vigik.ui.animations.gauge

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from the gauge animation view.
 */
sealed interface GaugeViewAction : ViewAction {

    /**
     * Action to decrease the current value of the gauge.
     */
    data object DecreaseValue : GaugeViewAction

    /**
     * Action to increase the current value of the gauge.
     */
    data object IncreaseValue : GaugeViewAction
}
