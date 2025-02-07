package com.kidor.vigik.ui.animations.gauge

import com.kidor.vigik.ui.base.ViewState

/**
 * State of the gauge view.
 *
 * @param gaugeValue The value of the gauge.
 */
data class GaugeViewState(val gaugeValue: Float) : ViewState
