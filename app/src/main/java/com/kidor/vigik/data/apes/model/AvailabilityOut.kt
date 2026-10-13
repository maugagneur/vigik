package com.kidor.vigik.data.apes.model

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
data class AvailabilityOut(
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("slots")
    val slots: List<SlotOut> = emptyList(),
    @SerializedName("members")
    val members: List<MemberAvailability> = emptyList()
)
