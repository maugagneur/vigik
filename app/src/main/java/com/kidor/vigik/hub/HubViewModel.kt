package com.kidor.vigik.hub

import androidx.lifecycle.ViewModel

class HubViewModel : ViewModel(), HubContract.HubViewModel {

    private lateinit var view: HubContract.HubView

    override fun setView(view: HubContract.HubView) {
        this.view = view
    }

    override fun onStart() {
        // Nothing to do
    }

    override fun onActionEmulateTag() {
        if (view.isActive()) {
            view.goToEmulatedTag()
        }
    }

    override fun onActionReadTag() {
        if (view.isActive()) {
            view.goToReadTag()
        }
    }
}