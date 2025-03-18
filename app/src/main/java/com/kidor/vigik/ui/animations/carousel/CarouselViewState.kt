package com.kidor.vigik.ui.animations.carousel

import com.kidor.vigik.ui.base.ViewState

/**
 * State of the carousel screen.
 *
 * @param carouselMode The current mode of the carousel.
 */
data class CarouselViewState(val carouselMode: CarouselMode) : ViewState
