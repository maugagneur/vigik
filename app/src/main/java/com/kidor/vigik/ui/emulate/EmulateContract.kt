package com.kidor.vigik.ui.emulate

import com.kidor.vigik.ui.base.BaseView
import com.kidor.vigik.ui.base.BaseViewModel

interface EmulateContract {

    interface EmulateView : BaseView {
        fun addLogLine(newLine: String)
    }

    interface EmulateViewModel : BaseViewModel<EmulateView>
}