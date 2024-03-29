package com.kidor.vigik.data.telephony

import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony
import android.telephony.SmsManager
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val CONTACT_ID_COLUMN_INDEX = 100
private const val CONTACT_TYPE_COLUMN_INDEX = 101
private const val SMS_ADDRESS_COLUMN_INDEX = 200
private const val SMS_BODY_COLUMN_INDEX = 201
private const val SMS_DATE_SENT_COLUMN_INDEX = 202
private const val CALL_NUMBER_COLUMN_INDEX = 300
private const val CALL_TYPE_COLUMN_INDEX = 301

/**
 * Unit tests for [TelephonyRepositoryImp].
 */
class TelephonyRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var telephonyRepository: TelephonyRepository

    @MockK
    private lateinit var contentResolver: ContentResolver
    @MockK
    private lateinit var smsManager: SmsManager
    @MockK
    private lateinit var cursor: Cursor

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        telephonyRepository = TelephonyRepositoryImp(
            contentResolver = contentResolver,
            smsManager = smsManager
        )
        every { cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID) } returns CONTACT_ID_COLUMN_INDEX
        every { cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE) } returns CONTACT_TYPE_COLUMN_INDEX
        every { cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS) } returns SMS_ADDRESS_COLUMN_INDEX
        every { cursor.getColumnIndexOrThrow(Telephony.Sms.BODY) } returns SMS_BODY_COLUMN_INDEX
        every { cursor.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT) } returns SMS_DATE_SENT_COLUMN_INDEX
        every { cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER) } returns CALL_NUMBER_COLUMN_INDEX
        every { cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE) } returns CALL_TYPE_COLUMN_INDEX
    }

    @Test
    fun `test getting all contacts`() {
        logTestName()

        val fakeData = listOf(
            Contact(1, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(1337, ContactsContract.CommonDataKinds.Phone.TYPE_HOME),
            Contact(42, ContactsContract.CommonDataKinds.Phone.TYPE_CAR),
            Contact(72, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(987654321, ContactsContract.CommonDataKinds.Phone.TYPE_WORK),
            Contact(0xDEADBEEF, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(4444, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(30121988, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        )

        var cursorIndex = 0
        every { contentResolver.query(ContactsContract.Data.CONTENT_URI, any(), null, null, null) } returns cursor
        every { cursor.moveToNext() } returns true andThenAnswer {
            cursorIndex += 1
            cursorIndex < fakeData.size
        }
        every { cursor.getLong(CONTACT_ID_COLUMN_INDEX) } answers { fakeData[cursorIndex].id }
        every { cursor.getInt(CONTACT_TYPE_COLUMN_INDEX) } answers { fakeData[cursorIndex].type }

        runTest {
            val contacts = telephonyRepository.getAllContact()

            // Check that the cursor was freed up after use
            verify { cursor.close() }

            // Check data returned
            assertEquals(fakeData.size, contacts.size, "Number of contacts")
            fakeData.forEach { contact ->
                assertTrue(contacts.contains(contact), "$contact found")
            }
        }
    }

    @Test
    fun `test getting all mobile contacts`() {
        logTestName()

        val fakeData = listOf(
            Contact(1, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(1337, ContactsContract.CommonDataKinds.Phone.TYPE_HOME),
            Contact(42, ContactsContract.CommonDataKinds.Phone.TYPE_CAR),
            Contact(72, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(987654321, ContactsContract.CommonDataKinds.Phone.TYPE_WORK),
            Contact(0xDEADBEEF, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(4444, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(30121988, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        )

        var cursorIndex = 0
        every { contentResolver.query(ContactsContract.Data.CONTENT_URI, any(), null, null, null) } returns cursor
        every { cursor.moveToNext() } returns true andThenAnswer {
            cursorIndex += 1
            cursorIndex < fakeData.size
        }
        every { cursor.getLong(CONTACT_ID_COLUMN_INDEX) } answers { fakeData[cursorIndex].id }
        every { cursor.getInt(CONTACT_TYPE_COLUMN_INDEX) } answers { fakeData[cursorIndex].type }

        runTest {
            val contacts = telephonyRepository.getAllMobileContact()

            // Check that the cursor was freed up after use
            verify { cursor.close() }

            // Check data returned
            assertEquals(fakeData.filter { it.type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE }.size, contacts.size, "Number of contacts")
            fakeData
                .filter { it.type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE }
                .forEach { contact ->
                    assertTrue(contacts.contains(contact), "$contact found")
                }
        }
    }

    @Test
    fun `test getting all SMS`() {
        logTestName()

        val fakeData = listOf(
            Triple(null, "Test message", 0L),
            Triple("Test address", null, 1L),
            Triple("Test address", "Test message", 1L),
            Triple("Test address", "Test message", 0L),
            Triple("Test address", "Test message", 0L),
            Triple(null, "Test message", 1L),
            Triple("Test address", "Test message", 1L),
            Triple("Test address", "Test message", 1L)
        )

        var cursorIndex = 0
        every { contentResolver.query(Telephony.Sms.CONTENT_URI, any(), null, null, null) } returns cursor
        every { cursor.moveToNext() } returns true andThenAnswer {
            cursorIndex += 1
            cursorIndex < fakeData.size
        }
        every { cursor.getString(SMS_ADDRESS_COLUMN_INDEX) } answers { fakeData[cursorIndex].first }
        every { cursor.getString(SMS_BODY_COLUMN_INDEX) } answers { fakeData[cursorIndex].second }
        every { cursor.getLong(SMS_DATE_SENT_COLUMN_INDEX) } answers { fakeData[cursorIndex].third }

        runTest {
            val smsList = telephonyRepository.getAllSms()

            // Check that the cursor was freed up after use
            verify { cursor.close() }

            // Check data returned
            assertEquals(fakeData.filter { it.first != null && it.second != null }.size, smsList.size, "Number of SMS")
            fakeData
                .filter { it.first != null && it.second != null }
                .forEach { message ->
                    val type = if (message.third > 0) SmsType.RECEIVED else SmsType.SENT
                    val sms = Sms(message.second!!, type)
                    assertTrue(smsList.contains(sms), "$sms found")
                }
        }
    }

    @Test
    fun `test getting all phone calls`() {
        logTestName()

        val fakeData = listOf(
            Pair("0123456789", CallLog.Calls.MISSED_TYPE),
            Pair("0123456789", CallLog.Calls.INCOMING_TYPE),
            Pair(null, CallLog.Calls.INCOMING_TYPE),
            Pair("0123456789", CallLog.Calls.OUTGOING_TYPE),
            Pair("0123456789", CallLog.Calls.REJECTED_TYPE),
            Pair(null, CallLog.Calls.OUTGOING_TYPE),
            Pair("0123456789", CallLog.Calls.OUTGOING_TYPE),
            Pair("0123456789", CallLog.Calls.OUTGOING_TYPE),
            Pair("0123456789", CallLog.Calls.INCOMING_TYPE),
            Pair("0123456789", CallLog.Calls.INCOMING_TYPE),
            Pair("0123456789", CallLog.Calls.OUTGOING_TYPE),
            Pair("0123456789", CallLog.Calls.INCOMING_TYPE),
            Pair("0123456789", CallLog.Calls.REJECTED_TYPE),
        )

        var cursorIndex = 0
        every { contentResolver.query(ContactsContract.Data.CONTENT_URI, any(), null, null, null) } returns cursor
        every { cursor.moveToNext() } returns true andThenAnswer {
            cursorIndex += 1
            cursorIndex < fakeData.size
        }
        every { cursor.getString(CALL_NUMBER_COLUMN_INDEX) } answers { fakeData[cursorIndex].first }
        every { cursor.getInt(CALL_TYPE_COLUMN_INDEX) } answers { fakeData[cursorIndex].second }

        runTest {
            val phoneCalls = telephonyRepository.getAllPhoneCalls()

            // Check that the cursor was freed up after use
            verify { cursor.close() }

            // Check data returned
            assertEquals(fakeData.filter { it.first != null }.size, phoneCalls.size, "Number of phone calls")
            fakeData
                .filter { it.first != null }
                .forEach {
                    val status = when (it.second) {
                        CallLog.Calls.INCOMING_TYPE -> PhoneCallStatus.RECEIVED
                        CallLog.Calls.OUTGOING_TYPE -> PhoneCallStatus.EMITTED
                        CallLog.Calls.MISSED_TYPE -> PhoneCallStatus.MISSED
                        CallLog.Calls.REJECTED_TYPE -> PhoneCallStatus.REJECTED
                        else -> throw IllegalArgumentException("Unexpected value")
                    }
                    val phoneCall = PhoneCall(it.first!!, status)
                    assertTrue(phoneCalls.contains(phoneCall), "$phoneCall found")
                }
        }
    }

    @Test
    fun `test send SMS`() {
        logTestName()

        val phoneNumber = "+33601337042"
        val smsContent = "Test message"

        runTest {
            telephonyRepository.sendSms(phoneNumber = phoneNumber, message = smsContent)

            verify { smsManager.sendTextMessage(phoneNumber, null, smsContent, null, null) }
        }
    }
}
