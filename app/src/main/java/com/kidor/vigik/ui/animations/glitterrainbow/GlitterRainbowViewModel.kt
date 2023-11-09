package com.kidor.vigik.ui.animations.glitterrainbow

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    /**
     * Update the current view state.
     *
     * @param update The operation to perform on view state.
     */
    private fun updateViewState(update: (GlitterRainbowViewState) -> GlitterRainbowViewState) {
        viewModelScope.launch {
            _viewState.value = update(viewState.value ?: GlitterRainbowViewState())
        }
    }
}
