package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Payload to declare day availability.
 *
 * @param ranges List of ranges (empty list clears the day).
 * @param mode   Merge mode ("replace", "merge", "skip"). Default is null (replace).
 */
data class RangesIn(
    @SerializedName("ranges")
    val ranges: List<RangeIn>,
    @SerializedName("mode")
    val mode: String? = null
)
