package com.kidor.vigik.nfc.api

import com.kidor.vigik.nfc.model.Tag

/**
 * Callback to be notify of events provides by [NfcApi].
 */
fun interface NfcApiListener {

    /**
     * Called when a NFC tag is read.
     *
     * @param tag The NFC tag read.
     */
    fun onNfcTagRead(tag: Tag)
}
