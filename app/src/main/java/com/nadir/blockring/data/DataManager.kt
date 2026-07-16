package com.nadir.blockring.data

import android.content.Context
import com.nadir.blockring.data.contacts.ContactsManager
import com.nadir.blockring.data.emergency.EmergencyManager
import com.nadir.blockring.data.ussd.UssdManager

class DataManager(
    context: Context
) {

    val contacts = ContactsManager(context)

    val emergency = EmergencyManager(context)

    val ussd = UssdManager(context)

}