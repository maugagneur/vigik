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

    init {
        performNfcCheck()
    }

    override fun handleAction(viewAction: CheckViewAction) {
        when (viewAction) {
            CheckViewAction.DisplayNfcSettings -> _viewEvent.value = CheckViewEvent.NavigateToSettings.wrap()
            CheckViewAction.RefreshNfcStatus -> performNfcCheck()
        }
    }

    private fun performNfcCheck() {
        _viewState.value = CheckViewState.Loading
        viewModelScope.launch {
            delay(TIME_BEFORE_NFC_CHECK)
            checkIfNfcIsAvailable()
        }
    }

    private fun checkIfNfcIsAvailable() {
        if (nfcApi.isNfcEnable()) {
            _viewEvent.value = CheckViewEvent.NavigateToHub.wrap()
        } else {
            _viewState.value = CheckViewState.NfcIsDisable
        }
    }
}
