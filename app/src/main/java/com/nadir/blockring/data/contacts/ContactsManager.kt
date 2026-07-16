package com.nadir.blockring.data.contacts

import android.content.Context
import android.provider.ContactsContract
import com.nadir.blockring.data.hidden.HiddenContactsManager
import com.nadir.blockring.model.Contact

class ContactsManager(
    private val context: Context
) {

    private val systemNumbers = setOf(
        "101",
        "102",
        "103",
        "104",
        "1050",
        "112"
    )

    private fun getAllContacts(): List<Contact> {

        val contacts = mutableListOf<Contact>()
        val addedNumbers = mutableSetOf<String>()

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {

            val idIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )

            val nameIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )

            val phoneIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (it.moveToNext()) {

                val rawPhone = it.getString(phoneIndex) ?: continue

                val normalizedPhone = rawPhone
                    .replace(" ", "")
                    .replace("-", "")
                    .replace("(", "")
                    .replace(")", "")

                val contact = Contact(
                    id = it.getLong(idIndex),
                    name = it.getString(nameIndex) ?: "",
                    phoneNumber = normalizedPhone
                )

                if (
                    contact.phoneNumber !in systemNumbers &&
                    addedNumbers.add(contact.phoneNumber)
                ) {
                    contacts.add(contact)
                }
            }
        }

        return contacts
    }

    fun getUssdContacts(): List<Contact> {
        return getContacts().filter {
            it.phoneNumber.startsWith("*") &&
                    it.phoneNumber.endsWith("#")
        }
    }

fun getContacts(): List<Contact> {

    val hiddenManager = HiddenContactsManager(context)
    val hiddenContacts = hiddenManager.getHidden()

    return getAllContacts().filter {
        it.phoneNumber !in hiddenContacts
    }
}
    fun getHiddenContacts(): List<Contact> {

        val hiddenManager = HiddenContactsManager(context)

        return hiddenManager.getHiddenContacts(
            getAllContacts()
        )

    }

    fun isContact(phoneNumber: String): Boolean {
        return getContacts().any {
            it.phoneNumber == phoneNumber
        }
    }

}
