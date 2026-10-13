package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Result of day declaration.
 *
 * @param date   Actual evening date written (YYYY-MM-DD).
 * @param ranges Written ranges.
 */
data class DayOut(
    @SerializedName("date")
    val date: String,
    @SerializedName("ranges")
    val ranges: List<RangeOut> = emptyList()
)
