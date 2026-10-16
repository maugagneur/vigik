package com.kidor.vigik.data.apes.model

import androidx.compose.runtime.Stable
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
@Stable
data class MemberAvailability(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("display_name")
    val displayName: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("statuses")
    val statuses: List<Int?>? = null
) {

    /**
     * Returns the [AvailabilityStatus] of the member at the given [slotIndex].
     */
    fun getStatusAt(slotIndex: Int): AvailabilityStatus {
        return AvailabilityStatus.fromScore(statuses?.getOrNull(slotIndex))
    }

    /**
     * Checks if the member is considered available (probable or confirmed) at the given [slotIndex].
     */
    fun isAvailableAt(slotIndex: Int): Boolean {
        return getStatusAt(slotIndex).isAvailable
    }

    /**
     * Returns the highest [AvailabilityStatus] of the member across the given [eveningSlots].
     */
    fun getEveningStatus(eveningSlots: List<Pair<Int, SlotOut>>): AvailabilityStatus {
        return eveningSlots.maxOfOrNull { (index, _) -> getStatusAt(index) } ?: AvailabilityStatus.UNKNOWN
    }
}



