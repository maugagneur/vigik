package com.kidor.vigik.data.diablo.model

import androidx.annotation.StringRes
import com.kidor.vigik.R

/**
 * Existing world boss in Diablo IV.
 *
 * @param resId The resource ID associated with the world boss.
 */
enum class Diablo4WorldBoss(@StringRes val resId: Int) {
    ASHAVA(R.string.diablo_boss_name_ashava),
    AVARICE(R.string.diablo_boss_name_avarice),
    WANDERING_DEATH(R.string.diablo_boss_name_wandering_death)
}
