package com.kidor.vigik.ui.scan

import androidx.lifecycle.ViewModel
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.nfc.api.NfcApiListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val nfcApi: NfcApi
) : ViewModel(), NfcApiListener, ScanContract.ScanViewModel {

    private lateinit var view: ScanContract.ScanView

    override fun setView(view: ScanContract.ScanView) {
        this.view = view
    }

    override fun onStart() {
        nfcApi.register(this)
    }

    override fun onStop() {
        nfcApi.unregister(this)
    }

    override fun onNfcTagRead(tagInfo: String) {
        if (view.isActive()) {
            view.displayScanResult(tagInfo)
        }
    }
}