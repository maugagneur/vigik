package com.kidor.vigik.ui.check

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.ui.base.BaseViewModel
import com.kidor.vigik.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@VisibleForTesting
internal const val TIME_BEFORE_NFC_CHECK = 1000L

@HiltViewModel
class CheckViewModel @Inject constructor(
    private val nfcApi: NfcApi
) : BaseViewModel<CheckViewState, CheckViewEvent>() {

    init {
        performNfcCheck()
    }

    fun onActionRefresh() {
        performNfcCheck()
    }

    fun onActionSettings() {
        _viewEvent.value = Event(CheckViewEvent.NavigateToSettings)
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
            _viewEvent.value = Event(CheckViewEvent.NavigateToHub)
        } else {
            _viewState.value = CheckViewState.NfcIsDisable
        }
    }
}
