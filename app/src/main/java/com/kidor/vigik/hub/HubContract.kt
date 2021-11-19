package com.kidor.vigik.hub

import com.kidor.vigik.base.BaseView
import com.kidor.vigik.base.BaseViewModel

interface HubContract {

    interface HubView : BaseView {
        fun goToEmulatedTag()
        fun goToReadTag()
    }

    interface HubViewModel : BaseViewModel<HubView> {
        fun onActionEmulateTag()
        fun onActionReadTag()
    }
}