package com.kidor.vigik.nfc.api

import com.kidor.vigik.nfc.model.Tag

interface NfcApiListener {
    fun onNfcTagRead(tag: Tag)
}