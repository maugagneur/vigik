package com.kidor.vigik.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for [NfcApi].
 */
@RunWith(MockitoJUnitRunner::class)
class NfcApiTest {

    @InjectMocks
    lateinit var nfcApi: NfcApi

    @Mock
    lateinit var nfcAdapter: NfcAdapter
    @Mock
    lateinit var listener: NfcApiListener
    @Mock
    lateinit var intent: Intent
    @Mock
    lateinit var tag: Tag
    @Mock
    lateinit var ndefMessage: NdefMessage
    @Mock
    lateinit var ndefRecord: NdefRecord

    @Test
    fun checkNfcWhenEnable() {
        logTestName()

        // When
        `when`(nfcAdapter.isEnabled).thenReturn(true)

        // Run
        val result = nfcApi.isNfcEnable()

        // Verify
        assertTrue(result)
    }

    @Test
    fun checkNfcWhenDisable() {
        logTestName()

        // When
        `when`(nfcAdapter.isEnabled).thenReturn(false)

        // Run
        val result = nfcApi.isNfcEnable()

        // Verify
        assertFalse(result)
    }

    @Test
    fun doNotNotifyWhenReceiveWrongIntent() {
        logTestName()

        // When
        `when`(intent.action).thenReturn("WRONG_ACTION")

        // Run
        nfcApi.register(listener)
        nfcApi.onNfcIntentReceived(intent)

        // Verify
        verify(listener, never()).onNfcTagRead(anyString())
    }

    @Test
    fun notNotifyWhenReceiveEmptyIntent() {
        logTestName()

        // When
        `when`(intent.action).thenReturn(NfcAdapter.ACTION_TAG_DISCOVERED)

        // Run
        nfcApi.register(listener)
        nfcApi.onNfcIntentReceived(intent)

        // Verify
        verify(listener).onNfcTagRead(
            "Tag UID -> N/A\n" +
                    "Tag tech list -> N/A\n" +
                    "Tag data -> No NDEF messages\n" +
                    "Tag ID-> N/A"
        )
    }

    @Test
    fun notNotifyWhenReceiveIntentWithData() {
        logTestName()

        // When
        `when`(tag.id).thenReturn(byteArrayOf(0x42, 0x13, 0x37, 0xFF.toByte()))
        `when`(tag.toString()).thenReturn("TAG: Tech [NfcA, MifareClassic, NdefFormatable]")
        `when`(intent.action).thenReturn(NfcAdapter.ACTION_TAG_DISCOVERED)
        `when`(intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)).thenReturn(tag)
        `when`(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)).thenReturn(arrayOf(ndefMessage))
        `when`(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)).thenReturn(byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte()))
        `when`(ndefMessage.records).thenReturn(arrayOf(ndefRecord))
        `when`(ndefRecord.toString()).thenReturn("Fake NDEF description")

        // Run
        nfcApi.register(listener)
        nfcApi.onNfcIntentReceived(intent)

        // Verify
        verify(listener).onNfcTagRead(
            "Tag UID -> 42 13 37 FF\n" +
                    "Tag tech list -> TAG: Tech [NfcA, MifareClassic, NdefFormatable]\n" +
                    "Tag data -> Fake NDEF description\n" +
                    "Tag ID-> DE AD BE EF"
        )
    }
}