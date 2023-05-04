package com.example.fetchcontacts__

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.ContactsContract

//class Contact(
//    val id: Long,
//    val name: String,
//    val phoneNumber: String,
//    val type: ContactType
//)
//
//enum class ContactType {
//    PERSONAL,
//    BUSINESS
//}

class ContactsDataSource(private val contentResolver: ContentResolver) {
    @SuppressLint("Range")
    fun getAllContacts(): List<Contact> {
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            val contacts = mutableListOf<Contact>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber =
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                if (hasPhoneNumber > 0) {
                    val phoneNumberCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id.toString()),
                        null
                    )
                    phoneNumberCursor?.let { phoneCursor ->
                        while (phoneCursor.moveToNext()) {
                            val phoneNumber = phoneCursor.getString(
                                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            val contactType =
                                if (id % 2 == 0L) ContactType.PERSONAL else ContactType.BUSINESS
                            contacts.add(Contact(id="",name, phoneNumber, contactType))
                        }
                        phoneCursor.close()
                    }
                }
            }
            cursor.close()
            return@let contacts
        }
        return emptyList()
    }
}