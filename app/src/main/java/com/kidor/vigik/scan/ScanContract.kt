package com.kidor.vigik.scan

import com.kidor.vigik.base.BaseView
import com.kidor.vigik.base.BaseViewModel

interface ScanContract {

    interface ScanView : BaseView {
        fun displayScanResult(tagInfo: String)
    }

    interface ScanViewModel : BaseViewModel<ScanView>
}