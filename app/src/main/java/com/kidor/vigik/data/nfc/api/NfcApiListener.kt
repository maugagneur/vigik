package com.kidor.vigik.data.nfc.api

import com.kidor.vigik.data.nfc.model.Tag

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
