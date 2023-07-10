package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.diablo.Diablo4API
import com.kidor.vigik.utils.SystemWrapper
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
    private val glanceAppWidgetManager: GlanceAppWidgetManager,
    private val updateDiabloWidgetStateUseCase: UpdateDiabloWidgetStateUseCase,
    private val diablo4API: Diablo4API,
    private val localization: Localization,
    private val systemWrapper: SystemWrapper
) : CoroutineWorker(context, workerParams) {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun doWork(): Result {
        val glanceId = glanceAppWidgetManager
            .getGlanceIds(DiabloWidget::class.java)
            .firstOrNull()
            ?: return Result.failure()

        // Set widget loading state to 'true'
        updateDiabloWidgetStateUseCase.execute(context, glanceId) { preferences ->
            DiabloWidgetStateHelper.setLoading(preferences = preferences, isLoading = true)
        }

        return try {
            // Fetch data
            diablo4API.getNextWorldBoss().let { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    updateDiabloWidgetStateUseCase.execute(context, glanceId) { preferences ->
                        DiabloWidgetStateHelper.setData(
                            preferences = preferences,
                            bossName = responseBody?.name,
                            spawnDelay = responseBody?.time ?: 0,
                            localization = localization,
                            system = systemWrapper
                        )
                    }
                    Result.success()
                } else {
                    Timber.w("Fail to get next world boss data: ${response.errorBody()?.string()}")
                    updateDiabloWidgetStateUseCase.execute(context, glanceId) { preferences ->
                        DiabloWidgetStateHelper.setLoading(preferences = preferences, isLoading = false)
                    }
                    Result.failure()
                }
            }
        } catch (exception: Exception) {
            Timber.e(exception, "Error when refreshing diablo widget data")
            updateDiabloWidgetStateUseCase.execute(context, glanceId) { preferences ->
                DiabloWidgetStateHelper.setLoading(preferences = preferences, isLoading = false)
            }
            Result.failure()
        }
    }
}
