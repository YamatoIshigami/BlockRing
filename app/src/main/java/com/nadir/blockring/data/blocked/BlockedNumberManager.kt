package com.nadir.blockring.data.blocked

import android.content.Context
import com.nadir.blockring.data.contacts.ContactsManager

class BlockedNumbersManager(
    private val context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "blocked_numbers",
            Context.MODE_PRIVATE
        )

    fun block(number: String) {

        val blocked = getBlocked().toMutableSet()

        blocked.add(number)

        prefs.edit()
            .putStringSet("numbers", blocked)
            .apply()
    }

    fun unblock(number: String) {

        val blocked = getBlocked().toMutableSet()

        blocked.remove(number)

        prefs.edit()
            .putStringSet("numbers", blocked)
            .apply()
    }

    fun getBlocked(): Set<String> {

        return prefs.getStringSet(
            "numbers",
            emptySet()
        ) ?: emptySet()

    }

    fun removeIfExistsInContacts(
        contactsManager: ContactsManager
    ) {

        val blocked = getBlocked().toMutableSet()

        blocked.removeAll { number ->
            contactsManager.isContact(number)
        }

        prefs.edit()
            .putStringSet("numbers", blocked)
            .apply()

    }

    fun isBlocked(number: String): Boolean {

        return number in getBlocked()

    }

}