package com.kidor.vigik.ui.check

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import com.kidor.vigik.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

internal const val PROGRESS_BAR_TEST_TAG = "Progress bar"

/**
 * View that check if all prerequisite to use NFC are met.
 */
@AndroidEntryPoint
class CheckFragment : BaseFragment<CheckViewAction, CheckViewState, CheckViewEvent, CheckViewModel>() {

    override val viewModel by viewModels<CheckViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Wait until the view model is ready to dismiss the splashscreen
                return if (viewModel.isReady) {
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        viewModel.handleAction(CheckViewAction.RefreshNfcStatus)
    }
}
