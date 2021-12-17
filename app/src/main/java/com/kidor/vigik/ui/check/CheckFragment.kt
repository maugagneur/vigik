package com.kidor.vigik.ui.check

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.kidor.vigik.databinding.FragmentCheckNfcBinding
import com.kidor.vigik.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckFragment : BaseFragment<CheckViewAction, CheckViewState, CheckViewEvent, CheckViewModel>() {

    private lateinit var binding: FragmentCheckNfcBinding
    override val viewModel by viewModels<CheckViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCheckNfcBinding.inflate(inflater, container, false).also {
            it.nfcRefreshButton.setOnClickListener { viewModel.handleAction(CheckViewAction.RefreshNfcStatus) }
            it.nfcSettingsButton.setOnClickListener { viewModel.handleAction(CheckViewAction.DisplayNfcSettings) }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.handleAction(CheckViewAction.RefreshNfcStatus)
    }

    override fun stateRender(viewState: CheckViewState) {
        when (viewState) {
            CheckViewState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.nfcRefreshButton.visibility = View.GONE
                binding.nfcSettingsButton.visibility = View.GONE
            }
            CheckViewState.NfcIsDisable -> {
                binding.progressBar.visibility = View.GONE
                binding.nfcRefreshButton.visibility = View.VISIBLE
                binding.nfcSettingsButton.visibility = View.VISIBLE
            }
        }
    }

    override fun eventRender(viewEvent: CheckViewEvent) {
        when (viewEvent) {
            CheckViewEvent.NavigateToHub ->
                Navigation.findNavController(requireView()).navigate(CheckFragmentDirections.goToHub())
            CheckViewEvent.NavigateToSettings ->
                startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
        }
    }
}