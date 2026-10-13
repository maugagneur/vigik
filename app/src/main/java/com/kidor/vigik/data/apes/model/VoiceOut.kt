package com.kidor.vigik.data.apes.model

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

/**
 * Information about members currently in voice channel.
 *
 * @param now     Current timestamp (UTC).
 * @param members Members in voice channel.
 */
@Stable
data class VoiceOut(
    @SerializedName("now")
    val now: String? = null,
    @SerializedName("members")
    val members: List<VoiceMember>? = null
)
