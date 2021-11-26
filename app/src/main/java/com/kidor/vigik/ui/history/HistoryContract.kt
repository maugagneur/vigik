package com.kidor.vigik.ui.history

import androidx.lifecycle.LiveData
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.ui.base.BaseView
import com.kidor.vigik.ui.base.BaseViewModel

interface HistoryContract {

    interface HistoryView : BaseView {
        fun showError(message: String)
    }

    interface HistoryViewModel : BaseViewModel<HistoryView> {
        val allTags: LiveData<List<Tag>>
        fun deleteTag(tag: Tag)
    }
}