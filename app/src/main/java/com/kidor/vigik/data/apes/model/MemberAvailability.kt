package com.kidor.vigik.data.apes.model

import com.google.gson.annotations.SerializedName

/**
 * Availability of a member across requested slots.
 *
 * @param id          Opaque public member ID.
 * @param displayName Display name of the member.
 * @param avatarUrl   Relative URL of member avatar or null.
 * @param statuses    Statuses list for each slot (null = unknown, 0 = unavailable, 33 = possible,
 *                    66 = probable, 100 = confirmed).
 */
data class MemberAvailability(
    @SerializedName("id")
    val id: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("statuses")
    val statuses: List<Int?> = emptyList()
)
