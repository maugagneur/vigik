package com.kidor.vigik.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kidor.vigik.databinding.FragmentHistoryBinding
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.ui.base.BaseFragment
import com.kidor.vigik.ui.history.list.HistoryAdapter
import com.kidor.vigik.ui.history.list.TagHistoryListener
import dagger.hilt.android.AndroidEntryPoint

/**
 * View that display all tags saved in the database.
 */
@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewAction, HistoryViewState, Nothing, HistoryViewModel>(),
    TagHistoryListener {

    override val viewModel by viewModels<HistoryViewModel>()

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

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

    override fun stateRender(viewState: HistoryViewState) {
        when (viewState) {
            HistoryViewState.Initializing -> {
                binding.tagHistoryRecyclerview.visibility = View.GONE
                binding.noDataTextview.visibility = View.GONE
                historyAdapter.submitList(emptyList())
            }
            is HistoryViewState.DisplayTags -> {
                binding.tagHistoryRecyclerview.visibility = View.VISIBLE
                binding.noDataTextview.visibility = View.GONE
                historyAdapter.submitList(viewState.tags)
            }
            HistoryViewState.NoTag -> {
                binding.tagHistoryRecyclerview.visibility = View.GONE
                binding.noDataTextview.visibility = View.VISIBLE
                historyAdapter.submitList(emptyList())
            }
        }
    }

    override fun onDeleteTagClick(tag: Tag) {
        viewModel.handleAction(HistoryViewAction.DeleteTag(tag))
    }
}
