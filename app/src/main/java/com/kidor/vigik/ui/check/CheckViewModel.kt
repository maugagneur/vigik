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

    // This flag indicates if the first NFC check has been done.
    private var firstNfcCheckDone: Boolean = false

    init {
        _viewState.value = CheckViewState.Loading
    }

    override fun handleAction(viewAction: CheckViewAction) {
        when (viewAction) {
            CheckViewAction.DisplayNfcSettings -> {
                viewModelScope.launch {
                    _viewEvent.emit(CheckViewEvent.NavigateToSettings)
                }
            }
            CheckViewAction.RefreshNfcStatus -> performNfcCheck()
        }
    }

    private fun performNfcCheck() {
        if (firstNfcCheckDone) {
            _viewState.value = CheckViewState.Loading
            viewModelScope.launch {
                // Add a delay for all no-first refresh request to force the UI to reflect this transition state
                delay(TIME_BEFORE_NFC_CHECK)
                checkIfNfcIsAvailable()
            }
        } else {
            // The first NFC check is done right at the launch of the screen so it does not need a delay.
            checkIfNfcIsAvailable()
        }
    }

    private fun checkIfNfcIsAvailable() {
        if (nfcApi.isNfcEnable()) {
            viewModelScope.launch {
                _viewEvent.emit(CheckViewEvent.NavigateToHub)
            }
        } else {
            _viewState.value = CheckViewState.NfcIsDisable
        }
        firstNfcCheckDone = true
    }
}
