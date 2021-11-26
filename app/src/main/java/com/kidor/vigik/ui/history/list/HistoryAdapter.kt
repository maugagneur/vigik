package com.kidor.vigik.ui.history.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.kidor.vigik.nfc.model.Tag

class HistoryAdapter(
    private val onDeleteTagClickListener: OnDeleteTagClickListener
) : ListAdapter<Tag, HistoryViewHolder>(TagComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onDeleteTagClickListener)
    }
}