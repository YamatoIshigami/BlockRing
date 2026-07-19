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
import com.nadir.blockring.model.Contact
import com.nadir.blockring.ui.components.EmptyState
import com.nadir.blockring.ui.components.SectionHeader
import com.nadir.blockring.ui.components.contactSection
import com.nadir.blockring.ui.permissions.hasContactsPermission

@Composable
fun HiddenContent() {

    val context = LocalContext.current
    val contactsManager = remember { ContactsManager(context) }

    var hiddenContacts by remember { mutableStateOf<List<Contact>?>(null) }

    LaunchedEffect(Unit) {
        hiddenContacts = if (hasContactsPermission(context)) {
            contactsManager.getHiddenContacts()
        } else {
            emptyList()
        }
    }

    val currentHidden = hiddenContacts

    if (currentHidden == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (currentHidden.isEmpty()) {
        EmptyState(
            emoji = "🙈",
            message = "Нет скрытых контактов",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

        item {
            SectionHeader(emoji = "🙈", title = "Скрытые контакты", count = currentHidden.size)
        }

        contactSection(
            contacts = currentHidden,
            isHiddenScreen = true,
            onUnhideContact = { unhiddenContact ->
                hiddenContacts = currentHidden.filter {
                    it.phoneNumber != unhiddenContact.phoneNumber
                }
            }
        )
    }
}