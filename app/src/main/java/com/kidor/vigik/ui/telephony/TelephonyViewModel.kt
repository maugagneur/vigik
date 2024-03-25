package com.kidor.vigik.ui.telephony

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.telephony.TelephonyRepository
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Business logic of Telephony screen.
 */
@HiltViewModel
class TelephonyViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val telephonyRepository: TelephonyRepository
) : BaseViewModel<TelephonyViewAction, TelephonyViewState, Nothing>() {

    init {
        // Initial state
        _viewState.value = TelephonyViewState.CheckPermission
    }

    override fun handleAction(viewAction: TelephonyViewAction) {
        if (viewAction is TelephonyViewAction.PermissionsGranted) {
            viewModelScope.launch(ioDispatcher) {
                val allContacts = telephonyRepository.getAllContact()
                val mobileContacts = telephonyRepository.getAllMobileContact()
                val totalSmsNumber = telephonyRepository.getSmsTotalNumber()
                updateState {
                    it.copy(
                        totalContactNumber = allContacts.size,
                        mobileContactNumber = mobileContacts.size,
                        totalSmsNumber = totalSmsNumber
                    )
                }
            }
        }
    }

    private fun updateState(update: (TelephonyViewState.ShowData) -> TelephonyViewState.ShowData) {
        viewModelScope.launch {
            viewState.value.let { currentViewState ->
                when (currentViewState) {
                    is TelephonyViewState.ShowData -> _viewState.value = update(currentViewState)
                    else -> _viewState.value = update(TelephonyViewState.ShowData())
                }
            }
        }
    }
}
