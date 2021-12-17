package com.kidor.vigik.ui.scan

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import com.kidor.vigik.R
import com.kidor.vigik.databinding.FragmentScanNfcBinding
import com.kidor.vigik.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanFragment : BaseFragment<ScanViewAction, ScanViewState, ScanViewEvent, ScanViewModel>() {

    private lateinit var binding: FragmentScanNfcBinding
    override val viewModel by viewModels<ScanViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentScanNfcBinding.inflate(inflater, container, false).also {
            it.saveFab.setOnClickListener { viewModel.handleAction(ScanViewAction.SaveTag) }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_scan, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_stop_scan -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun stateRender(viewState: ScanViewState) {
        when (viewState) {
            is ScanViewState.DisplayTag -> {
                binding.progressBar.visibility = View.GONE
                binding.tagInformationTextview.visibility = View.VISIBLE
                binding.tagInformationTextview.text = viewState.tag.toString()
                binding.saveFab.visibility = if (viewState.canBeSaved) View.VISIBLE else View.GONE

            }
            ScanViewState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tagInformationTextview.visibility = View.GONE
                binding.saveFab.visibility = View.GONE
            }
        }
    }

    override fun eventRender(viewEvent: ScanViewEvent) {
        when (viewEvent) {
            ScanViewEvent.SaveTagFailure -> promptMessage(R.string.save_tag_fail)
            ScanViewEvent.SaveTagSuccess -> promptMessage(R.string.save_tag_success)
        }
    }

    private fun promptMessage(@StringRes resId: Int) {
        activity?.runOnUiThread {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
        }
    }
}