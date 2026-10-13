package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Slot time boundary.
 *
 * @param local    Local wall-clock label, e.g. 2026-09-22T20:00.
 * @param startUtc Slot start in UTC.
 * @param endUtc   Slot end in UTC.
 */
data class SlotOut(
    @SerializedName("local")
    val local: String,
    @SerializedName("start_utc")
    val startUtc: String,
    @SerializedName("end_utc")
    val endUtc: String
)
