package com.nadir.blockring.data.contacts

import android.content.Context
import android.provider.ContactsContract
import com.nadir.blockring.data.hidden.HiddenContactsManager
import com.nadir.blockring.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ContactsManager(
    private val context: Context
) {
    companion object {
        @Volatile
        private var cachedContacts: List<Contact>? = null

        fun clearCache() {
            cachedContacts = null
        }
    }

    private val systemNumbers = setOf(
        "101",
        "102",
        "103",
        "104",
        "1050",
        "112"
    )

    private fun queryContactsPage(limit: Int, offset: Int): List<Contact> {

        val page = mutableListOf<Contact>()

        val sortOrder =
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
                    " ASC LIMIT $limit OFFSET $offset"

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            sortOrder
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

                page.add(
                    Contact(
                        id = it.getLong(idIndex),
                        name = it.getString(nameIndex) ?: "",
                        phoneNumber = normalizedPhone
                    )
                )
            }
        }

        return page
    }

    private fun queryContactsFromProvider(): List<Contact> {

        val contacts = mutableListOf<Contact>()
        val addedNumbers = mutableSetOf<String>()

        var offset = 0
        val pageSize = 200

        while (true) {
            val page = queryContactsPage(pageSize, offset)
            if (page.isEmpty()) break

            for (contact in page) {
                if (
                    contact.phoneNumber !in systemNumbers &&
                    addedNumbers.add(contact.phoneNumber)
                ) {
                    contacts.add(contact)
                }
            }

            if (page.size < pageSize) break
            offset += pageSize
        }

        return contacts
    }

    suspend fun loadAllContacts(forceRefresh: Boolean = false): List<Contact> {
        cachedContacts?.let { if (!forceRefresh) return it }

        return withContext(Dispatchers.IO) {
            queryContactsFromProvider().also { cachedContacts = it }
        }
    }

    fun loadContactsProgressively(pageSize: Int = 80): Flow<List<Contact>> = flow {
        cachedContacts?.let {
            emit(it)
            return@flow
        }

        val accumulated = mutableListOf<Contact>()
        val addedNumbers = mutableSetOf<String>()
        var offset = 0

        while (true) {
            val page = queryContactsPage(pageSize, offset)
            if (page.isEmpty()) break

            for (contact in page) {
                if (
                    contact.phoneNumber !in systemNumbers &&
                    addedNumbers.add(contact.phoneNumber)
                ) {
                    accumulated.add(contact)
                }
            }

            emit(accumulated.toList())

            if (page.size < pageSize) break
            offset += pageSize
        }

        cachedContacts = accumulated.toList()
    }.flowOn(Dispatchers.IO)

    suspend fun getContacts(): List<Contact> {

        val hiddenManager = HiddenContactsManager(context)
        val hiddenNumbers = hiddenManager.getHidden()

        return loadAllContacts().filter {
            it.phoneNumber !in hiddenNumbers
        }
    }

    suspend fun getHiddenContacts(): List<Contact> {

        val hiddenManager = HiddenContactsManager(context)

        return hiddenManager.getHiddenContacts(
            loadAllContacts()
        )
    }

    suspend fun getUssdContacts(): List<Contact> {
        return getContacts().filter {
            it.phoneNumber.startsWith("*") &&
                    it.phoneNumber.endsWith("#")
        }
    }

    fun isContact(phoneNumber: String): Boolean {

        val hiddenManager = HiddenContactsManager(context)
        val hiddenNumbers = hiddenManager.getHidden()

        val contacts = cachedContacts ?: queryContactsFromProvider().also {
            cachedContacts = it
        }

        return contacts.any {
            it.phoneNumber == phoneNumber && it.phoneNumber !in hiddenNumbers
        }
    }

}