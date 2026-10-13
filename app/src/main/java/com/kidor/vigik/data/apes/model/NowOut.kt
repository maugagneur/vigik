package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Group snapshot right now.
 *
 * @param now      Current timestamp (UTC).
 * @param timezone Group timezone.
 * @param members  List of members with their status now.
 */
data class NowOut(
    @SerializedName("now")
    val now: String? = null,
    @SerializedName("timezone")
    val timezone: String? = null,
    @SerializedName("members")
    val members: List<NowMember>? = null
)
