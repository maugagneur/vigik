package com.kidor.vigik.ui.restapi

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.diablo.Diablo4API
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.extensions.awaitAll
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val MINUTES_PER_HOUR = 60
private const val DELAY_THRESHOLD = 60
private const val SHORT_DELAY_BETWEEN_REFRESH: Long = 1 * 60 * 1000 // 1 minute
private const val LONG_DELAY_BETWEEN_REFRESH: Long = 5 * 60 * 1000 // 5 minute

/**
 * Business logic of REST API screen.
 */
@HiltViewModel
class RestApiViewModel @Inject constructor(
    private val diablo4API: Diablo4API,
    private val localization: Localization,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<RestApiViewAction, RestApiViewState, Nothing>() {

    private val trackerData = AtomicReference(Diablo4TrackerData())

    init {
        _viewState.value = RestApiViewState(diablo4TrackerData = trackerData.get())

        // World boss
        viewModelScope.launch(context = ioDispatcher) {
            while (true) {
                val remainingTime = refreshWorldBoss()
                val delay = if (remainingTime > DELAY_THRESHOLD) {
                    LONG_DELAY_BETWEEN_REFRESH
                } else {
                    SHORT_DELAY_BETWEEN_REFRESH
                }
                delay(delay)
            }
        }

        // Hell tide
        viewModelScope.launch(context = ioDispatcher) {
            while (true) {
                val remainingTime = refreshHellTide()
                val delay = if (remainingTime > DELAY_THRESHOLD) {
                    LONG_DELAY_BETWEEN_REFRESH
                } else {
                    SHORT_DELAY_BETWEEN_REFRESH
                }
                delay(delay)
            }
        }
    }

    override fun handleAction(viewAction: RestApiViewAction) {
        when (viewAction) {
            RestApiViewAction.RefreshData -> {
                viewModelScope.launch(ioDispatcher) {
                    // Notify refresh starts
                    _viewState.postValue(viewState.value?.copy(isRefreshing = true))

                    // Refresh all tracker data
                    awaitAll(
                        ::refreshWorldBoss,
                        ::refreshHellTide
                    )

                    // Notify refresh ends
                    _viewState.postValue(viewState.value?.copy(isRefreshing = false))
                }
            }
        }
    }

    /**
     * Fetch next world boss data from API then refresh UI state with it.
     *
     * @return Return the remaining time until next boss spawn or a negative value if an error occurred.
     */
    @Suppress("TooGenericExceptionCaught")
    private suspend fun refreshWorldBoss(): Int {
        var remainingTime: Int? = null
        try {
            diablo4API.getNextWorldBoss().let { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val worldBoss = getWorldBossFromName(responseBody?.name)
                    remainingTime = responseBody?.time
                    _viewState.postValue(
                        RestApiViewState(
                            diablo4TrackerData = trackerData.updateAndGet { previousData ->
                                previousData.copy(
                                    nextBoss = worldBoss,
                                    timeUntilNextBoss = formatDurationToReadableTime(remainingTime)
                                )
                            }
                        )
                    )
                } else {
                    Timber.w("Fail to get next world boss data: ${response.errorBody()?.string()}")
                }
            }
        } catch (exception: Exception) {
            Timber.e(exception, "Error when refreshing world boss data")
        }
        return remainingTime ?: -1
    }

    /**
     * Returns the [Diablo4WorldBoss] matching given name, or null if name is null.
     */
    private fun getWorldBossFromName(name: String?): Diablo4WorldBoss? = name?.let { Diablo4WorldBoss.fromName(it) }

    /**
     * Fetch next hell tide data from API then refresh UI state with it.
     *
     * Return the remaining time until next hell tide rise or a negative value if an error occurred.
     */
    @Suppress("TooGenericExceptionCaught")
    private suspend fun refreshHellTide(): Int {
        var remainingTime: Int? = null
        try {
            diablo4API.getNextHellTide().let { response ->
                if (response.isSuccessful) {
                    remainingTime = response.body()?.time
                    _viewState.postValue(
                        RestApiViewState(
                            diablo4TrackerData = trackerData.updateAndGet { previousData ->
                                previousData.copy(
                                    timeUntilNextHellTide = formatDurationToReadableTime(remainingTime)
                                )
                            }
                        )
                    )
                } else {
                    Timber.w("Fail to get next hell tide data: ${response.errorBody()?.string()}")
                }
            }
        } catch (exception: Exception) {
            Timber.e(exception, "Error when refreshing hell tide data")
        }
        return remainingTime ?: -1
    }

    /**
     * Formats and returns the duration as a String in format "XX h XX min".
     *
     * @param duration The duration to format.
     */
    private fun formatDurationToReadableTime(duration: Int?): String? {
        if (duration == null) return null

        val durationInMinutes = duration.toDuration(DurationUnit.MINUTES)
        val hours = durationInMinutes.inWholeHours
        val minutes = duration % MINUTES_PER_HOUR

        var formattedDuration = ""
        if (hours > 0) {
            formattedDuration = localization.getString(R.string.duration_unit_hour, hours) + " "
        }
        formattedDuration += localization.getString(R.string.duration_unit_minute, minutes)

        return formattedDuration
    }
}
