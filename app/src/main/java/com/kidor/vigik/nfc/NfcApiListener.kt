package com.kidor.vigik.nfc

interface NfcApiListener {
    fun onNfcTagRead(tagInfo: String)
}