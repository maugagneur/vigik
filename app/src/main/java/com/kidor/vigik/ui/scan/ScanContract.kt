package com.kidor.vigik.ui.scan

import com.kidor.vigik.ui.base.BaseView
import com.kidor.vigik.ui.base.BaseViewModel

interface ScanContract {

    interface ScanView : BaseView {
        fun displayScanResult(tagInfo: String)
        fun hideSaveButton()
        fun showSaveButton()
        fun promptSaveSuccess()
        fun promptSaveFail()
    }

    interface ScanViewModel : BaseViewModel<ScanView> {
        fun saveTag()
    }
}