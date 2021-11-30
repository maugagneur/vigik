package com.kidor.vigik.ui.emulate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kidor.vigik.databinding.FragmentEmulateTagBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmulateFragment : Fragment() {

    private lateinit var binding: FragmentEmulateTagBinding
    private val viewModel by viewModels<EmulateViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEmulateTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(this) { viewState ->
            if (viewState is EmulateViewState.DisplayLogLine) binding.logTextview.append("\n" + viewState.newLine)
        }
    }
}