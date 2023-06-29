package com.kidor.vigik.data.diablo.model

import androidx.annotation.StringRes
import com.kidor.vigik.R

/**
 * Existing world boss in Diablo IV.
 *
 * @param description The internal name that identifies the world boss.
 * @param resId       The resource ID associated with the world boss.
 */
enum class Diablo4WorldBoss(val description: String, @StringRes val resId: Int) {
    ASHAVA("ashava", R.string.diablo_boss_name_ashava),
    AVARICE("avarice", R.string.diablo_boss_name_avarice),
    WANDERING_DEATH("wandering death", R.string.diablo_boss_name_wandering_death),
    UNKNOWN("", R.string.diablo_boss_name_unknown);

    companion object {
        /**
         * Returns the [Diablo4WorldBoss] matching the given name or null if not found.
         *
         * @param name The world boss's name.
         */
        fun fromName(name: String): Diablo4WorldBoss {
            values().forEach { boss ->
                if (boss.description.equals(other = name, ignoreCase = true)) {
                    return boss
                }
            }
            return UNKNOWN
        }
    }
}
