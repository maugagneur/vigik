package com.kidor.vigik.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.kidor.vigik.databinding.FragmentHubBinding

class HubFragment : Fragment(), HubContract.HubView {

    private val viewModel by viewModels<HubViewModel>()

    override fun isActive() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentHubBinding.inflate(inflater, container, false).also {
            it.scanButton.setOnClickListener { viewModel.onActionReadTag() }
            it.emulateButton.setOnClickListener { viewModel.onActionEmulateTag() }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setView(this)
    }

    override fun goToEmulatedTag() {
        activity?.runOnUiThread {
            Navigation.findNavController(requireView()).navigate(HubFragmentDirections.goToEmulateTag())
        }
    }

    override fun goToReadTag() {
        activity?.runOnUiThread {
            Navigation.findNavController(requireView()).navigate(HubFragmentDirections.goToScanNfc())
        }
    }
}