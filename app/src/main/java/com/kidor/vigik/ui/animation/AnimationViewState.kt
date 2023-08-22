package com.kidor.vigik.ui.animation

import com.kidor.vigik.ui.base.ViewState

/**
 * State of the animation view.
 *
 * @param speedCoefficient The speed coefficient of glitters.
 * @param lifeTime         The life time of glitters.
 */
data class AnimationViewState(val speedCoefficient: Float = 0.5f, val lifeTime: Int = 100) : ViewState
