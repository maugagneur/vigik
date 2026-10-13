package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Time range input for availability declaration.
 *
 * @param start  Start time (HH:MM aligned on :00 or :30, from 00:00 to 27:00).
 * @param end    End time (HH:MM, from 00:00 to 27:00; end <= start means crossing midnight).
 * @param status Status value (0 = unavailable, 33 = possible, 66 = probable, 100 = confirmed, null = unknown).
 */
data class RangeIn(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String,
    @SerializedName("status")
    val status: Int? = null
)
