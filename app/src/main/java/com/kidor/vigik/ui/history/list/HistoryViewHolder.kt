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
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
    private val dateTextView: TextView = itemView.findViewById(R.id.date)
    private val uidTextView: TextView = itemView.findViewById(R.id.uid)
    private val deleteImageView: ImageView = itemView.findViewById(R.id.delete)

    fun bind(tag: Tag, onDeleteTagClickListener: OnDeleteTagClickListener) {
        dateTextView.text = dateFormat.format(Date(tag.timestamp))
        uidTextView.text = tag.uid?.toHex()
        deleteImageView.setOnClickListener { onDeleteTagClickListener.onDeleteTagClick(tag) }
    }

    companion object {
        fun create(parent: ViewGroup): HistoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_history_item, parent, false)
            return HistoryViewHolder(view)
        }
    }
}