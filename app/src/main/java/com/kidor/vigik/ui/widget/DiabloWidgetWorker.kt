package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.diablo.Diablo4API
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Worker that refresh state of [DiabloWidget].
 */
@HiltWorker
class DiabloWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val diablo4API: Diablo4API,
    private val localization: Localization
) : CoroutineWorker(context, workerParams) {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun doWork(): Result {
        val glanceId = GlanceAppWidgetManager(context)
            .getGlanceIds(DiabloWidget::class.java)
            .firstOrNull()
            ?: return Result.failure()

        // Set widget loading state to 'true'
        updateWidgetState(glanceId) { preferences ->
            DiabloWidgetStateHelper.setLoading(preferences = preferences, isLoading = true)
        }

        return try {
            // Fetch data
            diablo4API.getNextWorldBoss().let { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    updateWidgetState(glanceId) { preferences ->
                        DiabloWidgetStateHelper.setData(
                            preferences = preferences,
                            bossName = responseBody?.name,
                            spawnDelay = responseBody?.time ?: 0,
                            localization = localization
                        )
                    }
                    Result.success()
                } else {
                    Timber.w("Fail to get next world boss data: ${response.errorBody()?.string()}")
                    updateWidgetState(glanceId) { preferences ->
                        DiabloWidgetStateHelper.setLoading(preferences = preferences, isLoading = false)
                    }
                    Result.failure()
                }
            }
        } catch (exception: Exception) {
            Timber.e(exception, "Error when refreshing diablo widget data")
            updateWidgetState(glanceId) { preferences ->
                DiabloWidgetStateHelper.setLoading(preferences = preferences, isLoading = false)
            }
            Result.failure()
        }
    }

    /**
     * Updates the state of the widget then run its composition.
     *
     * @param glanceId The ID of the widget.
     * @param update   The operation to perform on preferences state.
     */
    private suspend fun updateWidgetState(glanceId: GlanceId, update: (MutablePreferences) -> Unit) {
        DiabloWidget().apply {
            updateAppWidgetState(context, glanceId) { preferences ->
                update(preferences)
            }
            update(context, glanceId)
        }
    }
}
