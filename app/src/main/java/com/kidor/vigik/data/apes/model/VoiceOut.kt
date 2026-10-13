package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Information about members currently in voice channel.
 *
 * @param now     Current timestamp (UTC).
 * @param members Members in voice channel.
 */
data class VoiceOut(
    @SerializedName("now")
    val now: String,
    @SerializedName("members")
    val members: List<VoiceMember> = emptyList()
)
