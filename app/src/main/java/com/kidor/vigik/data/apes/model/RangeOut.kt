package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Time range returned by the API.
 *
 * @param start  Start time.
 * @param end    End time.
 * @param status Status value.
 */
data class RangeOut(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String,
    @SerializedName("status")
    val status: Int? = null
)
