package com.kidor.vigik.ui.history.list

import com.kidor.vigik.nfc.model.Tag

interface OnDeleteTagClickListener {
    fun onDeleteTagClick(tag: Tag)
}