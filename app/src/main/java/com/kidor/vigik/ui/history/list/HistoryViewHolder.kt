package com.kidor.vigik.ui.history.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kidor.vigik.R
import com.kidor.vigik.extensions.toHex
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.ui.usecases.FormatDateUseCase

/**
 * Base view to display tag's data.
 */
class HistoryViewHolder(itemView: View, private val formatDateUseCase: FormatDateUseCase) : RecyclerView.ViewHolder(itemView) {

    private val dateTextView: TextView = itemView.findViewById(R.id.date)
    private val uidTextView: TextView = itemView.findViewById(R.id.uid)
    private val deleteImageView: ImageView = itemView.findViewById(R.id.delete)

    /**
     * Binds a tag history view with its data.
     *
     * @param tag                Tag bound to this view.
     * @param tagHistoryListener View's listener.
     */
    fun bind(tag: Tag, tagHistoryListener: TagHistoryListener) {
        dateTextView.text = formatDateUseCase(tag.timestamp)
        uidTextView.text = tag.uid?.toHex()
        deleteImageView.setOnClickListener { tagHistoryListener.onDeleteTagClick(tag) }
    }

    companion object {

        /**
         * Creates an instance of [HistoryViewHolder].
         *
         * @param parent View to be the parent of the generated hierarchy
         */
        fun create(parent: ViewGroup): HistoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_history_item, parent, false)
            return HistoryViewHolder(view, FormatDateUseCase())
        }
    }
}
