package com.kidor.vigik.ui.scan

import com.kidor.vigik.ui.base.BaseView
import com.kidor.vigik.ui.base.BaseViewModel

interface ScanContract {

    interface ScanView : BaseView {
        fun displayScanResult(tagInfo: String)
    }

    interface ScanViewModel : BaseViewModel<ScanView>
}