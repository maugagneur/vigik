package com.kidor.vigik.ui.animations.glitterrainbow

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from animation view.
 */
sealed interface GlitterRainbowViewAction : ViewAction {

    /**
     * Action to change the speed coefficient of glitters.
     *
     * @param speedCoefficient The new speed coefficient value.
     */
    data class ChangeSpeedCoefficient(val speedCoefficient: Float) : GlitterRainbowViewAction

    /**
     * Action to change the life time of glitters.
     *
     * @param lifeTime The new life time value.
     */
    data class ChangeLifeTime(val lifeTime: Int) : GlitterRainbowViewAction
}
