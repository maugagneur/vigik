package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Member status right now.
 *
 * @param id          Opaque public member ID.
 * @param displayName Display name of the member.
 * @param avatarUrl   Relative URL of member avatar or null.
 * @param status      Status right now (null = unknown, 0 = unavailable, 33 = possible, 66 = probable, 100 = confirmed).
 */
data class NowMember(
    @SerializedName("id")
    val id: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("status")
    val status: Int? = null
)
