package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kidor.vigik.R
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
                val members = voiceResult.data?.members.orEmpty()
                voiceCount = members.size
                voiceMembersStr = members.mapNotNull { it.displayName?.takeIf { name -> name.isNotBlank() } }
                    .joinToString(separator = ", ")
            }
            is NetworkResult.Error -> Timber.w("Fail to get voice data: ${voiceResult.errorMessage}")
            is NetworkResult.Exception -> Timber.e(voiceResult.throwable, "Exception getting voice data")
        }

        // 2. Fetch availability for today's evening
        val eveningDate = ApesDateHelper.getCurrentEveningDate()
        val fromDate = ApesDateHelper.formatDate(eveningDate)
        val toDate = ApesDateHelper.formatDate(eveningDate.plusDays(2))

        var eveningSummary = context.getString(R.string.widget_apes_schedule_unavailable)
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
        val slots = data?.slots.orEmpty()
        val members = data?.members.orEmpty()
        if (data == null || slots.isEmpty() || members.isEmpty()) {
            return context.getString(R.string.widget_apes_no_availability_tonight)
        }

        val eveningDate = ApesDateHelper.getCurrentEveningDate()
        var maxPlayers = 0
        var peakTimeLabel = ""

        slots.forEachIndexed { index, slot ->
            if (ApesDateHelper.isSlotInEvening(slot.local, eveningDate)) {
                val count = members.count { member ->
                    val status = member.statuses?.getOrNull(index)
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
                context.resources.getQuantityString(
                    R.plurals.widget_apes_peak_players_at_time,
                    maxPlayers,
                    maxPlayers,
                    peakTimeLabel
                )
            } else {
                context.resources.getQuantityString(
                    R.plurals.widget_apes_peak_players_tonight,
                    maxPlayers,
                    maxPlayers
                )
            }
        } else {
            context.getString(R.string.widget_apes_no_confirmed_availability)
        }
    }
}
