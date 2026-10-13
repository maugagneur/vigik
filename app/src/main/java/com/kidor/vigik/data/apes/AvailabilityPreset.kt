package com.kidor.vigik.data.apes

/**
 * Presets for fast availability submission.
 */
enum class AvailabilityPreset {
    /** 21:30 -> 01:00 at 100%. */
    PRESENT_TONIGHT,
    /** 20:00 -> 03:00 at 0%. */
    ABSENT_TONIGHT,
    /** Clear availability. */
    CLEAR
}
