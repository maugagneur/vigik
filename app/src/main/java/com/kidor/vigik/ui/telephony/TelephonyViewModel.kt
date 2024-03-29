package com.kidor.vigik.ui.telephony

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.telephony.TelephonyRepository
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.extensions.awaitAll
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
                    awaitAll(
                        ::refreshAllContactNumber,
                        ::refreshMobileContactNumber,
                        ::refreshAllSms,
                        ::refreshAllPhoneCalls
                    )
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
                        refreshAllSms()
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

    private suspend fun refreshAllContactNumber() {
        telephonyRepository.getAllContact().let { contacts ->
            updateState { it.copy(totalContactNumber = contacts.size) }
        }
    }

    private suspend fun refreshMobileContactNumber() {
        telephonyRepository.getAllMobileContact().let { mobileContacts ->
            updateState { it.copy(mobileContactNumber = mobileContacts.size) }
        }
    }

    private suspend fun refreshAllSms() {
        telephonyRepository.getAllSms().let { sms ->
            updateState { it.copy(sms = sms) }
        }
    }

    private suspend fun refreshAllPhoneCalls() {
        telephonyRepository.getAllPhoneCalls().let { phoneCalls ->
            updateState { it.copy(phoneCalls = phoneCalls) }
        }
    }
}
