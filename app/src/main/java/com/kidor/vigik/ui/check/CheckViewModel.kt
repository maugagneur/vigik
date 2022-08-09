package com.kidor.vigik.ui.check

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TIME_BEFORE_NFC_CHECK = 1000L

/**
 * Business logic for checking if all prerequisite to use NFC are met.
 */
@HiltViewModel
class CheckViewModel @Inject constructor(
    private val nfcApi: NfcApi
) : BaseViewModel<CheckViewAction, CheckViewState, CheckViewEvent>() {

    /**
     * Indicates if the application is ready to dismiss the splashscreen and to display data.
     */
    var isReady: Boolean = false
        private set

    init {
        _viewState.value = CheckViewState.Loading
    }

    override fun handleAction(viewAction: CheckViewAction) {
        when (viewAction) {
            CheckViewAction.DisplayNfcSettings -> _viewEvent.value = CheckViewEvent.NavigateToSettings.wrap()
            CheckViewAction.RefreshNfcStatus -> performNfcCheck()
        }
    }

    private fun performNfcCheck() {
        if (isReady) {
            _viewState.value = CheckViewState.Loading
            viewModelScope.launch {
                // Add a delay for all no-first refresh request to force the UI to reflect this transition state
                delay(TIME_BEFORE_NFC_CHECK)
                checkIfNfcIsAvailable()
            }
        } else {
            checkIfNfcIsAvailable()
        }
    }

    private fun checkIfNfcIsAvailable() {
        if (nfcApi.isNfcEnable()) {
            _viewEvent.value = CheckViewEvent.NavigateToHub.wrap()
        } else {
            _viewState.value = CheckViewState.NfcIsDisable
        }
        isReady = true
    }
}
