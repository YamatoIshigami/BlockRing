package com.nadir.blockring.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nadir.blockring.data.contacts.ContactsManager
import com.nadir.blockring.data.emergency.EmergencyManager
import com.nadir.blockring.data.ussd.UssdManager
import com.nadir.blockring.model.Contact
import com.nadir.blockring.ui.components.ContactSection
import com.nadir.blockring.ui.components.EmergencySection
import com.nadir.blockring.ui.components.UsedSection
import com.nadir.blockring.ui.permissions.hasContactsPermission

@Composable
fun ContactsContent() {
    val context = LocalContext.current

    var contacts by remember {
        mutableStateOf(
            if (hasContactsPermission(context)) {
                ContactsManager(context).getContacts()
            } else {
                emptyList<Contact>()
            }
        )
    }

    val emergencyNumbers = remember { EmergencyManager(context).getEmergencyNumbers() }
    val ussdCodes = remember { UssdManager(context).getUssdCodes() }

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

        item {
            EmergencySection(emergencyNumbers)
        }

        item {
            UsedSection(ussdCodes)
        }

        item {
            ContactSection(
                contacts = contacts,
                onHideContact = { contact ->
                    contacts = contacts.filter {
                        it.phoneNumber != contact.phoneNumber
                    }
                }
            )
        }
    }
}