package com.kidor.vigik.ui.check

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import com.kidor.vigik.nfc.api.NfcApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@VisibleForTesting
internal const val TIME_BEFORE_NFC_CHECK = 1000L

@HiltViewModel
class CheckViewModel @Inject constructor(
    private val nfcApi: NfcApi
) : ViewModel(), CheckContract.CheckViewModel {

    private lateinit var view: CheckContract.CheckView

    override fun setView(view: CheckContract.CheckView) {
        this.view = view
    }

    override fun onStart() {
        performNfcCheck()
    }

    override fun onActionRefresh() {
        performNfcCheck()
    }

    override fun onActionSettings() {
        if (view.isActive()) {
            view.displayNfcSettings()
        }
    }

    private fun performNfcCheck() {
        if (view.isActive()) {
            view.displayLoader()
            startDelayedTask(
                { checkIfNfcIsAvailable() },
                TIME_BEFORE_NFC_CHECK
            )
        }
    }

    private fun checkIfNfcIsAvailable() {
        if (view.isActive()) {
            if (nfcApi.isNfcEnable()) {
                view.goToNextStep()
            } else {
                view.displayNfcDisableMessage()
            }
        }
    }
}
