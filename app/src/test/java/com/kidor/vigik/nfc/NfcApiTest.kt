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
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [NfcApi].
 */
class NfcApiTest {

    @InjectMockKs
    lateinit var nfcApi: NfcApi

    @MockK
    private lateinit var nfcAdapter: NfcAdapter
    @MockK
    private lateinit var systemWrapper: SystemWrapper
    @MockK
    private lateinit var listener: NfcApiListener
    @MockK
    private lateinit var intent: Intent
    @MockK
    private lateinit var tag: Tag
    @MockK
    private lateinit var ndefMessage: NdefMessage
    @MockK
    private lateinit var ndefRecord: NdefRecord

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun checkNfcWhenEnable() {
        logTestName()

        // When
        every { nfcAdapter.isEnabled } returns true

        // Run
        val result = nfcApi.isNfcEnable()

        // Verify
        assertTrue(result)
    }

    @Test
    fun checkNfcWhenDisable() {
        logTestName()

        // When
        every { nfcAdapter.isEnabled } returns false

        // Run
        val result = nfcApi.isNfcEnable()

        // Verify
        assertFalse(result)
    }

    @Test
    fun doNotNotifyWhenReceiveWrongIntent() {
        logTestName()

        // When
        every { intent.action } returns "WRONG_ACTION"

        // Run
        nfcApi.register(listener)
        nfcApi.onNfcIntentReceived(intent)

        // Verify
        verify(inverse = true) { listener.onNfcTagRead(any()) }
    }

    @Test
    fun notNotifyWhenReceiveEmptyIntent() {
        logTestName()

        val now = System.currentTimeMillis()

        // When
        every { systemWrapper.currentTimeMillis() } returns now
        every { intent.action } returns NfcAdapter.ACTION_TAG_DISCOVERED
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns null
        every { intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) } returns null
        every { intent.getByteArrayExtra(NfcAdapter.EXTRA_ID) } returns null

        // Run
        nfcApi.register(listener)
        nfcApi.onNfcIntentReceived(intent)

        // Verify
        verify { listener.onNfcTagRead(com.kidor.vigik.nfc.model.Tag(now, null, null, "No NDEF messages", null)) }
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
        every { systemWrapper.currentTimeMillis() } returns now
        every { tag.id } returns tagUid
        every { tag.toString() } returns tagDescription
        every { intent.action } returns NfcAdapter.ACTION_TAG_DISCOVERED
        every { intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) } returns tag
        every { intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) } returns arrayOf(ndefMessage)
        every { intent.getByteArrayExtra(NfcAdapter.EXTRA_ID) } returns tagId
        every { ndefMessage.records } returns arrayOf(ndefRecord)
        every { ndefRecord.toString() } returns tagData

        // Run
        nfcApi.register(listener)
        nfcApi.onNfcIntentReceived(intent)

        // Verify
        verify { listener.onNfcTagRead(com.kidor.vigik.nfc.model.Tag(now, tagUid, tagDescription, tagData, tagId)) }
    }
}
