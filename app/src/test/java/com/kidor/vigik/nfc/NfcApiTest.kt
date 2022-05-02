package com.kidor.vigik.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.nfc.api.NfcApiListener
import com.kidor.vigik.utils.SystemWrapper
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any

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
    lateinit var systemWrapper: SystemWrapper
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
        verify(listener, never()).onNfcTagRead(any())
    }

    @Test
    fun notNotifyWhenReceiveEmptyIntent() {
        logTestName()

        val now = System.currentTimeMillis()

        // When
        `when`(systemWrapper.currentTimeMillis()).thenReturn(now)
        `when`(intent.action).thenReturn(NfcAdapter.ACTION_TAG_DISCOVERED)

        // Run
        nfcApi.register(listener)
        nfcApi.onNfcIntentReceived(intent)

        // Verify
        verify(listener).onNfcTagRead(com.kidor.vigik.nfc.model.Tag(now, null, null, "No NDEF messages", null))
    }

    @Test
    fun notifyWhenReceiveIntentWithData() {
        logTestName()

        val now = System.currentTimeMillis()
        val tagUid = byteArrayOf(0x42, 0x13, 0x37, 0xFF.toByte())
        val tagDescription = "TAG: Tech [NfcA, MifareClassic, NdefFormatable]"
        val tagData = "Fake NDEF description"
        val tagId = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())

        // When
        `when`(systemWrapper.currentTimeMillis()).thenReturn(now)
        `when`(tag.id).thenReturn(tagUid)
        `when`(tag.toString()).thenReturn(tagDescription)
        `when`(intent.action).thenReturn(NfcAdapter.ACTION_TAG_DISCOVERED)
        `when`(intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)).thenReturn(tag)
        `when`(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)).thenReturn(arrayOf(ndefMessage))
        `when`(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)).thenReturn(tagId)
        `when`(ndefMessage.records).thenReturn(arrayOf(ndefRecord))
        `when`(ndefRecord.toString()).thenReturn(tagData)

        // Run
        nfcApi.register(listener)
        nfcApi.onNfcIntentReceived(intent)

        // Verify
        verify(listener).onNfcTagRead(com.kidor.vigik.nfc.model.Tag(now, tagUid, tagDescription, tagData, tagId))
    }
}
