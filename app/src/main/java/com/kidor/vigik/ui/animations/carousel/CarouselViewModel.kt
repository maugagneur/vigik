package com.kidor.vigik.ui.animations.carousel

import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Business logic of carousel screen.
 */
@HiltViewModel
class CarouselViewModel @Inject constructor() : BaseViewModel<CarouselViewAction, CarouselViewState, Nothing>() {

    init {
        _viewState.value = CarouselViewState(carouselMode = CarouselMode.CLASSIC)
    }

    override fun handleAction(viewAction: CarouselViewAction) {
        when (viewAction) {
            CarouselViewAction.SelectNextMode -> {
                viewState.value?.carouselMode?.let { currentMode ->
                    val modes = CarouselMode.entries
                    val nextMode = modes[(currentMode.ordinal + 1) % modes.size]
                    _viewState.value = CarouselViewState(carouselMode = nextMode)
                }
            }

            CarouselViewAction.SelectPreviousMode -> {
                viewState.value?.carouselMode?.let { currentMode ->
                    val modes = CarouselMode.entries
                    var previousIndex = currentMode.ordinal - 1
                    if (previousIndex < 0) {
                        previousIndex += CarouselMode.entries.size
                    }
                    val previousMode = modes[previousIndex]
                    _viewState.value = CarouselViewState(carouselMode = previousMode)
                }
            }
        }
    }
}
