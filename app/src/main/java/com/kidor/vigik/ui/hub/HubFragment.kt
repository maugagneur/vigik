package com.kidor.vigik.ui.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.kidor.vigik.databinding.FragmentHubBinding
import com.kidor.vigik.ui.base.BaseFragment

/**
 * View that display all sections of the application.
 */
class HubFragment : BaseFragment<HubViewAction, Nothing, HubViewEvent, HubViewModel>() {

    override val viewModel by viewModels<HubViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentHubBinding.inflate(inflater, container, false).also {
            it.scanButton.setOnClickListener { viewModel.handleAction(HubViewAction.DisplayScanTagView) }
            it.historyButton.setOnClickListener { viewModel.handleAction(HubViewAction.DisplayTagHistoryView) }
            it.emulateButton.setOnClickListener { viewModel.handleAction(HubViewAction.DisplayEmulateTagView) }
        }
        return binding.root
    }

    override fun eventRender(viewEvent: HubViewEvent) {
        when (viewEvent) {
            HubViewEvent.NavigateToEmulateView -> navigateTo(HubFragmentDirections.goToEmulateTag())
            HubViewEvent.NavigateToHistoryView -> navigateTo(HubFragmentDirections.goToTagHistory())
            HubViewEvent.NavigateToScanView -> navigateTo(HubFragmentDirections.goToScanNfc())
        }
    }

    private fun navigateTo(direction: NavDirections) {
        Navigation.findNavController(requireView()).navigate(direction)
    }
}
