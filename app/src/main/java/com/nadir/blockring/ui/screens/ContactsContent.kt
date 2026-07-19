package com.nadir.blockring.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nadir.blockring.data.contacts.ContactsManager
import com.nadir.blockring.data.emergency.EmergencyManager
import com.nadir.blockring.data.hidden.HiddenContactsManager
import com.nadir.blockring.data.ussd.UssdManager
import com.nadir.blockring.model.Contact
import com.nadir.blockring.model.EmergencyService
import com.nadir.blockring.model.UssdCode
import com.nadir.blockring.ui.components.EmergencySection
import com.nadir.blockring.ui.components.SectionHeader
import com.nadir.blockring.ui.components.UsedSection
import com.nadir.blockring.ui.components.contactSection
import com.nadir.blockring.ui.permissions.hasContactsPermission

@Composable
fun ContactsContent() {
    val context = LocalContext.current
    val contactsManager = remember { ContactsManager(context) }
    val hiddenManager = remember { HiddenContactsManager(context) }
    var contacts by remember { mutableStateOf<List<Contact>?>(null) }
    var emergencyNumbers by remember { mutableStateOf<List<EmergencyService>>(emptyList()) }
    var ussdCodes by remember { mutableStateOf<List<UssdCode>>(emptyList()) }

    LaunchedEffect(Unit) {
        emergencyNumbers = EmergencyManager(context).getEmergencyNumbers()
        ussdCodes = UssdManager(context).getUssdCodes()
    }

    LaunchedEffect(Unit) {
        if (!hasContactsPermission(context)) {
            contacts = emptyList()
            return@LaunchedEffect
        }

        val hiddenNumbers = hiddenManager.getHidden()
        contactsManager.loadContactsProgressively().collect { snapshot ->
            contacts = snapshot.filter { it.phoneNumber !in hiddenNumbers }
        }
    }

    val currentContacts = contacts

    if (currentContacts == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

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
            SectionHeader(emoji = "👥", title = "Контакты", count = currentContacts.size)
        }

        contactSection(
            contacts = currentContacts,
            onHideContact = { contact ->
                contacts = currentContacts.filter {
                    it.phoneNumber != contact.phoneNumber
                }
            }
        )
    }
}