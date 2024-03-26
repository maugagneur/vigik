package com.kidor.vigik.data.telephony

import android.content.ContentResolver
import android.provider.CallLog.Calls
import android.provider.ContactsContract
import android.provider.Telephony
import android.telephony.SmsManager
import timber.log.Timber

/**
 * Implementation of [TelephonyRepository].
 */
class TelephonyRepositoryImp(
    private val contentResolver: ContentResolver,
    private val smsManager: SmsManager
) : TelephonyRepository {

    override suspend fun getAllContact(): List<Contact> {
        val result = mutableListOf<Contact>()
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.CommonDataKinds.Phone.TYPE
        )

        contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, null, null, null)?.let { contacts ->
            while (contacts.moveToNext()) {
                try {
                    val contactId: Long = contacts.getLong(
                        contacts.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                    )
                    val type: Int = contacts.getInt(
                        contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)
                    )
                    result.add(Contact(id = contactId, type = type))
                } catch (exception: IllegalArgumentException) {
                    Timber.e(exception, "Error when query for all contacts")
                }
            }
            contacts.close()
        }

        return result
    }

    override suspend fun getAllMobileContact(): List<Contact> =
        getAllContact().filter { contact -> contact.type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE }

    @Suppress("NestedBlockDepth")
    override suspend fun getSmsTotalNumber(): Int {
        var result = 0
        val projection = arrayOf(
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY
        )

        contentResolver.query(Telephony.Sms.CONTENT_URI, projection, null, null, null)?.let { messages ->
            while (messages.moveToNext()) {
                try {
                    val address: String? = messages.getString(messages.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val body: String? = messages.getString(messages.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    // We consider a SMS as valid if it has an address and a body
                    if (address != null && body != null) {
                        result++
                    }
                } catch (exception: IllegalArgumentException) {
                    Timber.e(exception, "Error when query for all SMS")
                }
            }
            messages.close()
        }

        return result
    }

    override suspend fun sendSms(phoneNumber: String, message: String) {
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    @Suppress("NestedBlockDepth")
    override suspend fun getAllPhoneCalls(): List<PhoneCall> {
        val result = mutableListOf<PhoneCall>()
        val projection = arrayOf(
            Calls.NUMBER,
            Calls.TYPE
        )

        contentResolver.query(Calls.CONTENT_URI, projection, null, null, null)?.let { phoneCalls ->
            while (phoneCalls.moveToNext()) {
                try {
                    val number: String? = phoneCalls.getString(phoneCalls.getColumnIndexOrThrow(Calls.NUMBER))
                    val type: Int = phoneCalls.getInt(phoneCalls.getColumnIndexOrThrow(Calls.TYPE))
                    if (number != null) {
                        val status = when (type) {
                            Calls.INCOMING_TYPE -> PhoneCallStatus.RECEIVED
                            Calls.OUTGOING_TYPE -> PhoneCallStatus.EMITTED
                            Calls.MISSED_TYPE -> PhoneCallStatus.MISSED
                            Calls.REJECTED_TYPE -> PhoneCallStatus.REJECTED
                            else -> {
                                // We ignore other types of call like VOICEMAIL and BLOCKED
                                continue
                            }
                        }
                        result.add(PhoneCall(phoneNumber = number, status = status))
                    }
                } catch (exception: IllegalArgumentException) {
                    Timber.e(exception, "Error when query for all phone calls")
                }
            }
            phoneCalls.close()
        }

        return result
    }
}
