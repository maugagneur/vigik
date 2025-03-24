package com.kidor.vigik.ui.animations.carousel

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from the carousel screen.
 */
sealed interface CarouselViewAction : ViewAction {

    /**
     * Action to select the next mode of display for the carousel.
     */
    data object SelectNextMode : CarouselViewAction

    /**
     * Action to select the previous mode of display for the carousel.
     */
    data object SelectPreviousMode : CarouselViewAction
}
