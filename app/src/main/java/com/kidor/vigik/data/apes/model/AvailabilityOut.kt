package com.kidor.vigik.data.apes.model

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName
import com.kidor.vigik.data.apes.ApesDateHelper
import java.time.LocalDate

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
) {

    /**
     * Returns the list of indexed slots belonging to the given [eveningDate].
     */
    fun getEveningSlots(eveningDate: LocalDate): List<Pair<Int, SlotOut>> {
        return slots.orEmpty()
            .mapIndexed { index, slot -> index to slot }
            .filter { (_, slot) -> ApesDateHelper.isSlotInEvening(slot.local, eveningDate) }
    }

    /**
     * Returns the number of members available at the given [slotIndex].
     */
    fun countAvailableMembers(slotIndex: Int): Int {
        return members.orEmpty().count { it.isAvailableAt(slotIndex) }
    }

    /**
     * Finds the peak evening slot with the highest number of available members for [eveningDate].
     *
     * @return A pair containing the maximum count of available members and the formatted hour of the peak slot.
     */
    fun findPeakEveningSlot(eveningDate: LocalDate): Pair<Int, String> {
        return findPeakEveningSlot(getEveningSlots(eveningDate))
    }

    /**
     * Finds the peak evening slot with the highest number of available members among [eveningSlots].
     *
     * @return A pair containing the maximum count of available members and the formatted hour of the peak slot.
     */
    fun findPeakEveningSlot(eveningSlots: List<Pair<Int, SlotOut>>): Pair<Int, String> {
        var peakCount = 0
        var peakSlotHour = ""
        eveningSlots.forEach { (index, slot) ->
            val count = countAvailableMembers(index)
            if (count > peakCount) {
                peakCount = count
                peakSlotHour = ApesDateHelper.formatSlotHour(slot.local)
            }
        }
        return peakCount to peakSlotHour
    }
}

