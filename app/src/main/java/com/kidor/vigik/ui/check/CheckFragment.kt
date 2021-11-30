package com.kidor.vigik.ui.check

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.kidor.vigik.databinding.FragmentCheckNfcBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckFragment : Fragment() {

    private lateinit var binding: FragmentCheckNfcBinding
    private val viewModel by viewModels<CheckViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCheckNfcBinding.inflate(inflater, container, false).also {
            it.nfcRefreshButton.setOnClickListener { viewModel.onActionRefresh() }
            it.nfcSettingsButton.setOnClickListener { viewModel.onActionSettings() }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(this) { viewState ->
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

        viewModel.viewEvent.observe(this) {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    CheckViewEvent.NavigateToHub ->
                        Navigation.findNavController(requireView()).navigate(CheckFragmentDirections.goToHub())
                    CheckViewEvent.NavigateToSettings ->
                        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onActionRefresh()
    }
}