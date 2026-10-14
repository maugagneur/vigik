package com.kidor.vigik.ui.restapi

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.apes.ApesDateHelper
import com.kidor.vigik.data.apes.ApesRepository
import com.kidor.vigik.data.apes.AvailabilityPreset
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.ui.base.BaseViewModel
import com.kidor.vigik.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Business logic of REST API (APES) screen.
 */
@HiltViewModel
class RestApiViewModel @Inject constructor(
    private val apesRepository: ApesRepository,
    private val localization: Localization,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<RestApiViewAction, RestApiViewState, Nothing>() {

    init {
        _viewState.value = RestApiViewState(isLoading = true)

        // Observe user token
        viewModelScope.launch {
            apesRepository.getUserToken().collectLatest { token ->
                updateViewState { it.copy(userToken = token) }
                if (!token.isNullOrBlank()) {
                    fetchMe(token)
                } else {
                    updateViewState { it.copy(meData = null) }
                }
            }
        }

        // Observe preset start and end times
        viewModelScope.launch {
            apesRepository.getPresetStartTime().collectLatest { startTime ->
                updateViewState { it.copy(presetStartTime = startTime) }
            }
        }
        viewModelScope.launch {
            apesRepository.getPresetEndTime().collectLatest { endTime ->
                updateViewState { it.copy(presetEndTime = endTime) }
            }
        }

        // Initial fetch
        viewModelScope.launch {
            fetchData()
        }
    }

    override fun handleAction(viewAction: RestApiViewAction) {
        when (viewAction) {
            RestApiViewAction.RefreshData -> {
                viewModelScope.launch {
                    updateViewState { it.copy(isRefreshing = true) }
                    fetchData()
                    viewState.value?.userToken?.takeIf { it.isNotBlank() }?.let { token ->
                        fetchMe(token)
                    }
                }
            }

            is RestApiViewAction.SaveUserToken -> {
                viewModelScope.launch(ioDispatcher) {
                    apesRepository.saveUserToken(viewAction.token)
                }
            }

            RestApiViewAction.ClearUserToken -> {
                viewModelScope.launch(ioDispatcher) {
                    apesRepository.clearUserToken()
                }
            }

            is RestApiViewAction.ApplyPreset -> {
                applyPreset(viewAction.preset)
            }

            is RestApiViewAction.SavePresetTimes -> {
                viewModelScope.launch(ioDispatcher) {
                    apesRepository.savePresetTimes(viewAction.startTime, viewAction.endTime)
                    val successMsg = localization.getString(R.string.apes_preset_times_updated_success)
                    updateViewState {
                        it.copy(userMessage = successMsg, errorMessage = null)
                    }
                }
            }

            RestApiViewAction.DismissMessage -> {
                updateViewState { it.copy(userMessage = null, errorMessage = null) }
            }
        }
    }

    private suspend fun fetchData() {
        val eveningDate = ApesDateHelper.getCurrentEveningDate()
        val fromDate = ApesDateHelper.formatDate(eveningDate)
        val toDate = ApesDateHelper.formatDate(eveningDate.plusDays(2))

        val voiceDeferred = viewModelScope.async(ioDispatcher) { apesRepository.getVoice() }
        val statusNowDeferred = viewModelScope.async(ioDispatcher) { apesRepository.getStatusNow() }
        val availabilityDeferred = viewModelScope.async(ioDispatcher) { apesRepository.getAvailability(fromDate, toDate) }

        val voiceResult = voiceDeferred.await()
        val statusNowResult = statusNowDeferred.await()
        val availabilityResult = availabilityDeferred.await()

        val voice = when (voiceResult) {
            is NetworkResult.Success -> voiceResult.data
            else -> null
        }

        val statusNow = when (statusNowResult) {
            is NetworkResult.Success -> statusNowResult.data
            else -> null
        }

        val availability = when (availabilityResult) {
            is NetworkResult.Success -> availabilityResult.data
            else -> null
        }

        updateViewState { current ->
            current.copy(
                isLoading = false,
                isRefreshing = false,
                voiceData = voice ?: current.voiceData,
                nowData = statusNow ?: current.nowData,
                availabilityData = availability ?: current.availabilityData
            )
        }
    }

    private suspend fun fetchMe(token: String) {
        when (val result = withContext(ioDispatcher) { apesRepository.getMe(token) }) {
            is NetworkResult.Success -> {
                updateViewState { it.copy(meData = result.data) }
            }
            is NetworkResult.Error -> {
                Timber.w("Failed to get me: ${result.errorMessage}")
                updateViewState { it.copy(meData = null) }
            }
            is NetworkResult.Exception -> {
                Timber.e(result.throwable, "Exception getting me")
                updateViewState { it.copy(meData = null) }
            }
        }
    }

    private fun applyPreset(preset: AvailabilityPreset) {
        viewModelScope.launch {
            updateViewState { it.copy(isSubmittingPreset = true) }
            val eveningDate = ApesDateHelper.formatDate(ApesDateHelper.getCurrentEveningDate())

            when (val result = withContext(ioDispatcher) { apesRepository.applyPreset(eveningDate, preset) }) {
                is NetworkResult.Success -> {
                    val successMsg = localization.getString(R.string.apes_dispo_updated_success)
                    updateViewState {
                        it.copy(
                            userMessage = successMsg,
                            errorMessage = null,
                            isSubmittingPreset = false
                        )
                    }
                    // Immediate refresh of availability to reflect changes
                    fetchData()
                }
                is NetworkResult.Error -> {
                    val errorMsg = localization.getString(
                        R.string.apes_dispo_update_error,
                        result.errorMessage ?: "Erreur ${result.code}"
                    )
                    updateViewState {
                        it.copy(
                            errorMessage = errorMsg,
                            userMessage = null,
                            isSubmittingPreset = false
                        )
                    }
                }
                is NetworkResult.Exception -> {
                    val errorMsg = localization.getString(
                        R.string.apes_dispo_update_error,
                        result.throwable.localizedMessage ?: "Erreur réseau"
                    )
                    updateViewState {
                        it.copy(
                            errorMessage = errorMsg,
                            userMessage = null,
                            isSubmittingPreset = false
                        )
                    }
                }
            }
        }
    }
}
