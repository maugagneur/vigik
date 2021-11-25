package com.kidor.vigik.ui.scan

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kidor.vigik.R
import com.kidor.vigik.databinding.FragmentScanNfcBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanFragment : Fragment(), ScanContract.ScanView {

    private lateinit var binding: FragmentScanNfcBinding
    private val viewModel by viewModels<ScanViewModel>()

    override fun isActive() = isAdded

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
        viewModel.setView(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onStop() {
        viewModel.onStop()
        super.onStop()
    }

    override fun displayScanResult(tagInfo: String) {
        activity?.runOnUiThread {
            binding.progressBar.visibility = View.GONE
            binding.tagInformationTextview.visibility = View.VISIBLE
            binding.tagInformationTextview.text = tagInfo
        }
    }

    override fun hideSaveButton() {
        activity?.runOnUiThread {
            binding.saveFab.visibility = View.GONE
        }
    }

    override fun showSaveButton() {
        activity?.runOnUiThread {
            binding.saveFab.visibility = View.VISIBLE
        }
    }

    override fun promptSaveSuccess() {
        activity?.runOnUiThread {
            Toast.makeText(context, R.string.save_tag_success, Toast.LENGTH_SHORT).show()
        }
    }

    override fun promptSaveFail() {
        activity?.runOnUiThread {
            Toast.makeText(context, R.string.save_tag_fail, Toast.LENGTH_SHORT).show()
        }
    }
}