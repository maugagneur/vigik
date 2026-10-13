package com.kidor.vigik.data.apes.model

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

/**
 * Calculated availability for all members across slots.
 *
 * @param fromDate Start date included (YYYY-MM-DD).
 * @param toDate   End date excluded (YYYY-MM-DD).
 * @param timezone Timezone string (e.g. Europe/Paris).
 * @param slots    List of slots.
 * @param members  List of member availabilities.
 */
@Stable
data class AvailabilityOut(
    @SerializedName("from_date")
    val fromDate: String? = null,
    @SerializedName("to_date")
    val toDate: String? = null,
    @SerializedName("timezone")
    val timezone: String? = null,
    @SerializedName("slots")
    val slots: List<SlotOut>? = null,
    @SerializedName("members")
    val members: List<MemberAvailability>? = null
)
