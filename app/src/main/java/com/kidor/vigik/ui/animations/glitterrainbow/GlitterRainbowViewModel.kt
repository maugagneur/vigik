package com.kidor.vigik.ui.animations.glitterrainbow

import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Business logic of Animation screen.
 */
@HiltViewModel
class GlitterRainbowViewModel @Inject constructor() :
    BaseViewModel<GlitterRainbowViewAction, GlitterRainbowViewState, Nothing>() {

    init {
        _viewState.value = GlitterRainbowViewState()
    }

    override fun handleAction(viewAction: GlitterRainbowViewAction) {
        when (viewAction) {
            is GlitterRainbowViewAction.ChangeSpeedCoefficient -> updateViewState {
                it.copy(speedCoefficient = viewAction.speedCoefficient)
            }

            is GlitterRainbowViewAction.ChangeLifeTime -> updateViewState {
                it.copy(lifeTime = viewAction.lifeTime)
            }
        }
    }
}
