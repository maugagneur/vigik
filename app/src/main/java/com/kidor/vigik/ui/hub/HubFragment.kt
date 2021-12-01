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

class HubFragment : BaseFragment<Nothing, HubViewEvent, HubViewModel>() {

    override val viewModel by viewModels<HubViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentHubBinding.inflate(inflater, container, false).also {
            it.scanButton.setOnClickListener { viewModel.onActionReadTag() }
            it.historyButton.setOnClickListener { viewModel.onActionTagHistory() }
            it.emulateButton.setOnClickListener { viewModel.onActionEmulateTag() }
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