package com.kidor.vigik.ui.emulate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.kidor.vigik.databinding.FragmentEmulateTagBinding
import com.kidor.vigik.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmulateFragment : BaseFragment<EmulateViewState, Nothing,  EmulateViewModel>() {

    private lateinit var binding: FragmentEmulateTagBinding
    override val viewModel by viewModels<EmulateViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEmulateTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun stateRender(viewState: EmulateViewState) {
        if (viewState is EmulateViewState.DisplayLogLine) binding.logTextview.append("\n" + viewState.newLine)
    }
}