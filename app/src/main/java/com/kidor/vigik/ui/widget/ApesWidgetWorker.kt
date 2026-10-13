package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kidor.vigik.data.apes.ApesApi
import com.kidor.vigik.data.apes.ApesDateHelper
import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.utils.NetworkHelper
import com.kidor.vigik.utils.NetworkResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

private const val STATUS_PROBABLE_THRESHOLD = 66

/**
 * Worker that refreshes the state of [ApesWidget].
 */
@HiltWorker
class ApesWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val glanceAppWidgetManager: GlanceAppWidgetManager,
    private val updateApesWidgetStateUseCase: UpdateApesWidgetStateUseCase,
    private val apesApi: ApesApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val glanceId = glanceAppWidgetManager
            .getGlanceIds(ApesWidget::class.java)
            .firstOrNull()
            ?: return Result.failure()

        // Set widget loading state to 'true'
        updateApesWidgetStateUseCase.execute(context, glanceId) { preferences ->
            ApesWidgetStateHelper.setLoading(preferences = preferences, isLoading = true)
        }

        // 1. Fetch voice data
        var voiceCount = 0
        var voiceMembersStr = ""
        when (val voiceResult = NetworkHelper.handleApi { apesApi.getVoice() }) {
            is NetworkResult.Success -> {
                val members = voiceResult.data?.members ?: emptyList()
                voiceCount = members.size
                voiceMembersStr = members.joinToString(separator = ", ") { it.displayName }
            }
            is NetworkResult.Error -> Timber.w("Fail to get voice data: ${voiceResult.errorMessage}")
            is NetworkResult.Exception -> Timber.e(voiceResult.throwable, "Exception getting voice data")
        }

        // 2. Fetch availability for today's evening
        val eveningDate = ApesDateHelper.getCurrentEveningDate()
        val fromDate = ApesDateHelper.formatDate(eveningDate)
        val toDate = ApesDateHelper.formatDate(eveningDate.plusDays(2))

        var eveningSummary = "Planning non disponible"
        when (val availResult = NetworkHelper.handleApi { apesApi.getAvailability(fromDate, toDate) }) {
            is NetworkResult.Success -> {
                eveningSummary = buildEveningSummary(availResult.data)
            }
            is NetworkResult.Error -> Timber.w("Fail to get availability: ${availResult.errorMessage}")
            is NetworkResult.Exception -> Timber.e(availResult.throwable, "Exception getting availability")
        }

        updateApesWidgetStateUseCase.execute(context, glanceId) { preferences ->
            ApesWidgetStateHelper.setData(
                preferences = preferences,
                voiceCount = voiceCount,
                voiceMembers = voiceMembersStr,
                eveningSummary = eveningSummary
            )
        }

        return Result.success()
    }

    private fun buildEveningSummary(data: AvailabilityOut?): String {
        if (data == null || data.slots.isEmpty() || data.members.isEmpty()) {
            return "Pas de dispos ce soir"
        }

        val eveningDate = ApesDateHelper.getCurrentEveningDate()
        var maxPlayers = 0
        var peakTimeLabel = ""

        data.slots.forEachIndexed { index, slot ->
            if (ApesDateHelper.isSlotInEvening(slot.local, eveningDate)) {
                val count = data.members.count { member ->
                    val status = member.statuses.getOrNull(index)
                    status != null && status >= STATUS_PROBABLE_THRESHOLD
                }
                if (count > maxPlayers) {
                    maxPlayers = count
                    peakTimeLabel = ApesDateHelper.formatSlotHour(slot.local)
                }
            }
        }

        return if (maxPlayers > 0) {
            if (peakTimeLabel.isNotEmpty()) {
                "Pic à $maxPlayers joueurs à $peakTimeLabel"
            } else {
                "Pic à $maxPlayers joueurs ce soir"
            }
        } else {
            "Aucune dispo confirmée"
        }
    }
}
