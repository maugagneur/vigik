package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Member currently in voice channel.
 *
 * @param id          Opaque public member ID.
 * @param displayName Display name of the member.
 * @param avatarUrl   Relative URL of member avatar or null.
 * @param since       Timestamp when member entered voice channel (UTC).
 */
data class VoiceMember(
    @SerializedName("id")
    val id: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("since")
    val since: String
)
