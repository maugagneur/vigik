package com.kidor.vigik.data.telephony

import android.content.ContentResolver
import android.provider.ContactsContract
import timber.log.Timber

/**
 * Implementation of [ContactRepository].
 */
class ContactRepositoryImp(
    private val contentResolver: ContentResolver
) : ContactRepository {

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
}
