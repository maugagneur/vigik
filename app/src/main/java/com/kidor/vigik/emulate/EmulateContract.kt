package com.kidor.vigik.emulate

import com.kidor.vigik.base.BaseView
import com.kidor.vigik.base.BaseViewModel

interface EmulateContract {

    interface EmulateView : BaseView {
        fun addLogLine(newLine: String)
    }

    interface EmulateViewModel : BaseViewModel<EmulateView>
}