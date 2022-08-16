package com.kidor.vigik.ui.hub

import androidx.fragment.app.viewModels
import com.kidor.vigik.ui.base.BaseFragment

/**
 * View that display all sections of the application.
 */
class HubFragment : BaseFragment<HubViewAction, HubViewState, HubViewEvent, HubViewModel>() {
    override val viewModel by viewModels<HubViewModel>()
}
