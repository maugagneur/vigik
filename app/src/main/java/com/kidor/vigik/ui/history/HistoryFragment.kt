package com.kidor.vigik.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kidor.vigik.databinding.FragmentHistoryBinding
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.ui.history.list.HistoryAdapter
import com.kidor.vigik.ui.history.list.OnDeleteTagClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment(), HistoryContract.HistoryView, OnDeleteTagClickListener {

    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by viewModels<HistoryViewModel>()
    private lateinit var historyAdapter: HistoryAdapter

    override fun isActive() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        historyAdapter = HistoryAdapter(this)
        binding = FragmentHistoryBinding.inflate(inflater, container, false).also {
            it.tagHistoryRecyclerview.let { recyclerView ->
                recyclerView.adapter = historyAdapter
                recyclerView.layoutManager = LinearLayoutManager(context)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setView(this)
        viewModel.allTags.observe(this) { tags ->
            historyAdapter.submitList(tags)
        }
    }

    override fun showError(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDeleteTagClick(tag: Tag) {
        viewModel.deleteTag(tag)
    }
}