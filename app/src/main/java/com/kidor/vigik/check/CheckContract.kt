package com.kidor.vigik.check

import com.kidor.vigik.base.BaseView
import com.kidor.vigik.base.BaseViewModel

interface CheckContract {

    interface CheckView : BaseView {
        fun displayLoader()
        fun displayNfcDisableMessage()
        fun displayNfcSettings()
        fun goToNextStep()
    }

    interface CheckViewModel : BaseViewModel<CheckView> {
        fun onActionRefresh()
        fun onActionSettings()
    }
}