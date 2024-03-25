package com.kidor.vigik.data.telephony

import android.content.ContentResolver
import android.provider.ContactsContract
import android.provider.Telephony
import timber.log.Timber

/**
 * Implementation of [TelephonyRepository].
 */
class TelephonyRepositoryImp(
    private val contentResolver: ContentResolver
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
                    val contactId: Long = contacts.getLong(contacts.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val type: Int = contacts.getInt(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE))
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
}
