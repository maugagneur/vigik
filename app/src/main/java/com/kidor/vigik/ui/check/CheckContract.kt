package com.kidor.vigik.ui.check

import com.kidor.vigik.ui.base.BaseView
import com.kidor.vigik.ui.base.BaseViewModel

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