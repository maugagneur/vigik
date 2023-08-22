package com.kidor.vigik.ui.animation

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from animation view.
 */
sealed class AnimationViewAction : ViewAction {

    /**
     * Action to change the speed coefficient of glitters.
     *
     * @param speedCoefficient The new speed coefficient value.
     */
    data class ChangeSpeedCoefficient(val speedCoefficient: Float) : AnimationViewAction()

    /**
     * Action to change the life time of glitters.
     *
     * @param lifeTime The new life time value.
     */
    data class ChangeLifeTime(val lifeTime: Int) : AnimationViewAction()
}
