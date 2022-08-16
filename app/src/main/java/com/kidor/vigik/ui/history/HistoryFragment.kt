package com.kidor.vigik.ui.history

import androidx.fragment.app.viewModels
import com.kidor.vigik.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * View that display all tags saved in the database.
 */
@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewAction, HistoryViewState, Nothing, HistoryViewModel>() {
    override val viewModel by viewModels<HistoryViewModel>()
}
