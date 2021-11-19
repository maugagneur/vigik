package com.kidor.vigik.check

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
class CheckFragment : Fragment(), CheckContract.CheckView {

    private lateinit var binding: FragmentCheckNfcBinding
    private val viewModel by viewModels<CheckViewModel>()

    override fun isActive() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCheckNfcBinding.inflate(inflater, container, false).also {
            it.nfcRefreshButton.setOnClickListener { viewModel.onActionRefresh() }
            it.nfcSettingsButton.setOnClickListener { viewModel.onActionSettings() }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setView(this)
    }

    override fun onResume() {
        super.onResume()

        viewModel.onStart()
    }

    override fun displayLoader() {
        activity?.runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
            binding.nfcRefreshButton.visibility = View.GONE
            binding.nfcSettingsButton.visibility = View.GONE
        }
    }

    override fun displayNfcDisableMessage() {
        activity?.runOnUiThread {
            binding.progressBar.visibility = View.GONE
            binding.nfcRefreshButton.visibility = View.VISIBLE
            binding.nfcSettingsButton.visibility = View.VISIBLE
        }
    }

    override fun displayNfcSettings() {
        activity?.runOnUiThread {
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
        }
    }

    override fun goToNextStep() {
        activity?.runOnUiThread {
            Navigation.findNavController(requireView()).navigate(CheckFragmentDirections.goToHub())
        }
    }
}