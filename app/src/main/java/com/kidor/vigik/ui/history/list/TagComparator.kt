package com.kidor.vigik.ui.history.list

import androidx.recyclerview.widget.DiffUtil
import com.kidor.vigik.nfc.model.Tag

class TagComparator : DiffUtil.ItemCallback<Tag>() {

    override fun areItemsTheSame(old: Tag, new: Tag): Boolean {
        return old === new
    }

    override fun areContentsTheSame(old: Tag, new: Tag): Boolean {
        return old == new
    }
}