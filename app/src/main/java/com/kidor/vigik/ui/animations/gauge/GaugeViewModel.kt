package com.kidor.vigik.ui.animations.gauge

import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val GAUGE_INITIAL_VALUE = 50f
private const val GAUGE_MIN_RANGE_VALUE = 0f
private const val GAUGE_MAX_RANGE_VALUE = 100f
private const val DELTA = 2f
private val GAUGE_VALUE_RANGE = GAUGE_MIN_RANGE_VALUE..GAUGE_MAX_RANGE_VALUE

/**
 * Business logic of the gauge animation screen.
 */
@HiltViewModel
class GaugeViewModel @Inject constructor() : BaseViewModel<GaugeViewAction, GaugeViewState, Nothing>() {

    init {
        _viewState.value = GaugeViewState(gaugeValue = GAUGE_INITIAL_VALUE)
    }

    override fun handleAction(viewAction: GaugeViewAction) {
        when (viewAction) {
            GaugeViewAction.DecreaseValue -> viewState.value?.gaugeValue?.let { currentValue ->
                val nextValue = (currentValue - DELTA).coerceIn(GAUGE_VALUE_RANGE)
                _viewState.value = GaugeViewState(gaugeValue = nextValue)
            }

            GaugeViewAction.IncreaseValue -> viewState.value?.gaugeValue?.let { currentValue ->
                val nextValue = (currentValue + DELTA).coerceIn(GAUGE_VALUE_RANGE)
                _viewState.value = GaugeViewState(gaugeValue = nextValue)
            }
        }
    }
}
