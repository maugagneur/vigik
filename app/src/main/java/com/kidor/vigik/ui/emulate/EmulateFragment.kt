package com.kidor.vigik.ui.emulate

import androidx.fragment.app.viewModels
import com.kidor.vigik.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * View that emulate a NFC tag.
 */
@AndroidEntryPoint
class EmulateFragment : BaseFragment<Nothing, EmulateViewState, Nothing, EmulateViewModel>() {
    override val viewModel by viewModels<EmulateViewModel>()
}
