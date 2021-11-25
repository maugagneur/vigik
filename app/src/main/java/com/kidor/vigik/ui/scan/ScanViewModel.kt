package com.kidor.vigik.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.nfc.api.NfcApiListener
import com.kidor.vigik.nfc.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val nfcApi: NfcApi,
    private val tagRepository: TagRepository
) : ViewModel(), NfcApiListener, ScanContract.ScanViewModel {

    private lateinit var view: ScanContract.ScanView
    private var tag: Tag? = null

    override fun setView(view: ScanContract.ScanView) {
        this.view = view
    }

    override fun onStart() {
        nfcApi.register(this)
    }

    override fun onStop() {
        nfcApi.unregister(this)
    }

    override fun onNfcTagRead(tag: Tag) {
        // Memorized last read tag
        this.tag = tag

        // Update view
        if (view.isActive()) {
            view.displayScanResult(tag.toString())
            if (tag.uid == null) {
                view.hideSaveButton()
            } else {
                view.showSaveButton()
            }
        }
    }

    override fun saveTag() {
        tag.let { tag ->
            if (tag == null) {
                Timber.w("Trying to save invalid tag into database")
            } else {
                insertTagInTheDatabase(tag)
            }
        }
    }

    private fun insertTagInTheDatabase(tag: Tag) {
        viewModelScope.launch {
            if (tagRepository.insert(tag) > 0) {
                Timber.i("Tag saved into database with success")
                if (view.isActive()) {
                    view.promptSaveSuccess()
                }
            } else {
                Timber.e("Fail to save tag into database")
                if (view.isActive()) {
                    view.promptSaveFail()
                }
            }
        }
    }
}