package com.kidor.vigik.ui.restapi

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.apes.ApesDateHelper
import com.kidor.vigik.data.apes.ApesRepository
import com.kidor.vigik.data.apes.DispoPreset
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.ui.base.BaseViewModel
import com.kidor.vigik.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
        viewModelScope.launch(ioDispatcher) {
            apesRepository.getUserToken().collectLatest { token ->
                _viewState.postValue(viewState.value?.copy(userToken = token))
                if (!token.isNullOrBlank()) {
                    fetchMe(token)
                } else {
                    _viewState.postValue(viewState.value?.copy(meData = null))
                }
            }
        }

        // Initial fetch
        viewModelScope.launch(ioDispatcher) {
            fetchData()
            _viewState.postValue(viewState.value?.copy(isLoading = false))
        }
    }

    override fun handleAction(viewAction: RestApiViewAction) {
        when (viewAction) {
            RestApiViewAction.RefreshData -> {
                viewModelScope.launch(ioDispatcher) {
                    _viewState.postValue(viewState.value?.copy(isRefreshing = true))
                    fetchData()
                    viewState.value?.userToken?.let { token ->
                        if (token.isNotBlank()) fetchMe(token)
                    }
                    _viewState.postValue(viewState.value?.copy(isRefreshing = false))
                }
            }

            is RestApiViewAction.SaveUserToken -> {
                viewModelScope.launch(ioDispatcher) {
                    apesRepository.saveUserToken(viewAction.token)
                    fetchMe(viewAction.token)
                }
            }

            RestApiViewAction.ClearUserToken -> {
                viewModelScope.launch(ioDispatcher) {
                    apesRepository.clearUserToken()
                    _viewState.postValue(viewState.value?.copy(userToken = null, meData = null))
                }
            }

            is RestApiViewAction.ApplyPreset -> {
                applyPreset(viewAction.preset)
            }

            RestApiViewAction.DismissMessage -> {
                _viewState.postValue(viewState.value?.copy(userMessage = null, errorMessage = null))
            }
        }
    }

    private suspend fun fetchData() {
        val eveningDate = ApesDateHelper.getCurrentEveningDate()
        val fromDate = ApesDateHelper.formatDate(eveningDate)
        val toDate = ApesDateHelper.formatDate(eveningDate.plusDays(2))

        val voiceDeferred = viewModelScope.async(ioDispatcher) { apesRepository.getVoice() }
        val nowDeferred = viewModelScope.async(ioDispatcher) { apesRepository.getStatusNow() }
        val availDeferred = viewModelScope.async(ioDispatcher) { apesRepository.getAvailability(fromDate, toDate) }

        val voiceResult = voiceDeferred.await()
        val nowResult = nowDeferred.await()
        val availResult = availDeferred.await()

        val voice = when (voiceResult) {
            is NetworkResult.Success -> voiceResult.data
            else -> viewState.value?.voiceData
        }

        val now = when (nowResult) {
            is NetworkResult.Success -> nowResult.data
            else -> viewState.value?.nowData
        }

        val avail = when (availResult) {
            is NetworkResult.Success -> availResult.data
            else -> viewState.value?.availabilityData
        }

        _viewState.postValue(
            viewState.value?.copy(
                voiceData = voice,
                nowData = now,
                availabilityData = avail
            )
        )
    }

    private suspend fun fetchMe(token: String) {
        when (val result = apesRepository.getMe(token)) {
            is NetworkResult.Success -> {
                _viewState.postValue(viewState.value?.copy(meData = result.data))
            }
            is NetworkResult.Error -> {
                Timber.w("Failed to get me: ${result.errorMessage}")
                _viewState.postValue(viewState.value?.copy(meData = null))
            }
            is NetworkResult.Exception -> {
                Timber.e(result.throwable, "Exception getting me")
                _viewState.postValue(viewState.value?.copy(meData = null))
            }
        }
    }

    private fun applyPreset(preset: DispoPreset) {
        viewModelScope.launch(ioDispatcher) {
            _viewState.postValue(viewState.value?.copy(isSubmittingPreset = true))
            val eveningDate = ApesDateHelper.formatDate(ApesDateHelper.getCurrentEveningDate())

            when (val result = apesRepository.applyPreset(eveningDate, preset)) {
                is NetworkResult.Success -> {
                    val successMsg = localization.getString(R.string.apes_dispo_updated_success)
                    _viewState.postValue(
                        viewState.value?.copy(
                            userMessage = successMsg,
                            errorMessage = null
                        )
                    )
                    // Immediate refresh of availability to reflect changes
                    fetchData()
                }
                is NetworkResult.Error -> {
                    val errorMsg = localization.getString(
                        R.string.apes_dispo_update_error,
                        result.errorMessage ?: "Erreur ${result.code}"
                    )
                    _viewState.postValue(
                        viewState.value?.copy(
                            errorMessage = errorMsg,
                            userMessage = null
                        )
                    )
                }
                is NetworkResult.Exception -> {
                    val errorMsg = localization.getString(
                        R.string.apes_dispo_update_error,
                        result.throwable.localizedMessage ?: "Erreur réseau"
                    )
                    _viewState.postValue(
                        viewState.value?.copy(
                            errorMessage = errorMsg,
                            userMessage = null
                        )
                    )
                }
            }
            _viewState.postValue(viewState.value?.copy(isSubmittingPreset = false))
        }
    }
}
