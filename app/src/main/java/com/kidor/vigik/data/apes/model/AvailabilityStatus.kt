package com.kidor.vigik.data.apes.model

/**
 * Discrete availability status levels mapped from API availability score (0..100).
 *
 * The declaration order reflects the priority from lowest ([UNKNOWN]) to highest ([CONFIRMED]).
 *
 * @param threshold Minimum score associated with this status level.
 */
enum class AvailabilityStatus(val threshold: Int) {
    UNKNOWN(-1),
    UNAVAILABLE(0),
    UNCERTAIN(1),
    POSSIBLE(33),
    PROBABLE(66),
    CONFIRMED(100);

    /**
     * Returns true if this status counts as available for evening quorum/peak calculations.
     */
    val isAvailable: Boolean
        get() = this >= PROBABLE

    companion object {
        /**
         * Resolves the [AvailabilityStatus] corresponding to a raw API [score].
         */
        fun fromScore(score: Int?): AvailabilityStatus = when {
            score == null -> UNKNOWN
            score >= CONFIRMED.threshold -> CONFIRMED
            score >= PROBABLE.threshold -> PROBABLE
            score >= POSSIBLE.threshold -> POSSIBLE
            score >= UNCERTAIN.threshold -> UNCERTAIN
            score == UNAVAILABLE.threshold -> UNAVAILABLE
            else -> UNKNOWN
        }
    }
}
