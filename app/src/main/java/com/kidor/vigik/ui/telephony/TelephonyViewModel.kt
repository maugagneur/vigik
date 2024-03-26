package com.kidor.vigik.ui.telephony

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.telephony.TelephonyRepository
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DELAY_BEFORE_REFRESH_SMS_NUMBER = 1_000L

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
        when (viewAction) {
            is TelephonyViewAction.PermissionsGranted -> {
                viewModelScope.launch(ioDispatcher) {
                    val allContacts = telephonyRepository.getAllContact()
                    val mobileContacts = telephonyRepository.getAllMobileContact()
                    val allSms = telephonyRepository.getAllSms()
                    val allPhoneCalls = telephonyRepository.getAllPhoneCalls()
                    updateState {
                        it.copy(
                            totalContactNumber = allContacts.size,
                            mobileContactNumber = mobileContacts.size,
                            sms = allSms,
                            phoneCalls = allPhoneCalls
                        )
                    }
                }
            }

            is TelephonyViewAction.SendSms -> {
                if (viewAction.message.isNotBlank()) {
                    viewModelScope.launch(ioDispatcher) {
                        telephonyRepository.sendSms(
                            phoneNumber = viewAction.phoneNumber,
                            message = viewAction.message
                        )
                        // Refresh number of SMS after a short delay
                        delay(DELAY_BEFORE_REFRESH_SMS_NUMBER)
                        val totalSmsNumber = telephonyRepository.getAllSms()
                        updateState { it.copy(sms = totalSmsNumber) }
                    }
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
