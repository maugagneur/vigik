package com.kidor.vigik.nfc.api

interface NfcApiListener {
    fun onNfcTagRead(tagData: TagData)
}