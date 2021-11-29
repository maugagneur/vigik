package com.kidor.vigik.ui.scan

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kidor.vigik.R
import com.kidor.vigik.databinding.FragmentScanNfcBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanNfcBinding
    private val viewModel by viewModels<ScanViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentScanNfcBinding.inflate(inflater, container, false).also {
            it.saveFab.setOnClickListener { viewModel.saveTag() }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(this) { viewState ->
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

        viewModel.viewEvent.observe(this) {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    ScanViewEvent.SaveTagFailure -> promptMessage(R.string.save_tag_fail)
                    ScanViewEvent.SaveTagSuccess -> promptMessage(R.string.save_tag_success)
                }
            }
        }
    }

    private fun promptMessage(@StringRes resId: Int) {
        activity?.runOnUiThread {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
        }
    }
}