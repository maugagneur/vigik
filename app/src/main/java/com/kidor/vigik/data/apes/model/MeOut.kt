package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Current authenticated member info.
 *
 * @param id          Opaque public member ID.
 * @param displayName Display name of the member.
 */
data class MeOut(
    @SerializedName("id")
    val id: String,
    @SerializedName("display_name")
    val displayName: String
)
