package com.kidor.vigik.ui.animation

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Business logic of Animation screen.
 */
@HiltViewModel
class AnimationViewModel @Inject constructor() : BaseViewModel<AnimationViewAction, AnimationViewState, Nothing>() {

    init {
        _viewState.value = AnimationViewState()
    }

    override fun handleAction(viewAction: AnimationViewAction) {
        when (viewAction) {
            is AnimationViewAction.ChangeSpeedCoefficient -> updateViewState {
                it.copy(speedCoefficient = viewAction.speedCoefficient)
            }
        }
    }

    /**
     * Update the current view state.
     *
     * @param update The operation to perform on view state.
     */
    private fun updateViewState(update: (AnimationViewState) -> AnimationViewState) {
        viewModelScope.launch {
            _viewState.value = update(viewState.value ?: AnimationViewState())
        }
    }
}
