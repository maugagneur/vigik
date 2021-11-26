package com.kidor.vigik.ui.hub

import com.kidor.vigik.ui.base.BaseView
import com.kidor.vigik.ui.base.BaseViewModel

interface HubContract {

    interface HubView : BaseView {
        fun goToEmulatedTag()
        fun goToReadTag()
        fun goToTagHistory()
    }

    interface HubViewModel : BaseViewModel<HubView> {
        fun onActionEmulateTag()
        fun onActionReadTag()
        fun onActionTagHistory()
    }
}