package com.kidor.vigik.ui.scan

import androidx.fragment.app.viewModels
import com.kidor.vigik.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * View that scan for a NFC tag.
 */
@AndroidEntryPoint
class ScanFragment : BaseFragment<ScanViewAction, ScanViewState, ScanViewEvent, ScanViewModel>() {
    override val viewModel by viewModels<ScanViewModel>()
}
