package com.nadir.blockring.data.hidden

import android.content.Context

class HiddenContactsManager(
    private val context: Context
) {

    private val prefs =
        context.getSharedPreferences("hidden_contacts", Context.MODE_PRIVATE)

    fun hide(number: String) {
        val hidden = getHidden().toMutableSet()
        hidden.add(number)

        prefs.edit()
            .putStringSet("numbers", hidden)
            .apply()
    }

    fun unhide(number: String) {
        val hidden = getHidden().toMutableSet()
        hidden.remove(number)

        prefs.edit()
            .putStringSet("numbers", hidden)
            .apply()
    }

    fun getHidden(): Set<String> {
        return prefs.getStringSet("numbers", emptySet()) ?: emptySet()
    }

    fun isHidden(number: String): Boolean {
        return number in getHidden()
    }


fun getHiddenContacts(
    allContacts: List<com.nadir.blockring.model.Contact>
): List<com.nadir.blockring.model.Contact> {

    val hiddenNumbers = getHidden()

    return allContacts.filter {
        it.phoneNumber in hiddenNumbers
    }
}

}