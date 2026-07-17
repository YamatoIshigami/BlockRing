package com.nadir.blockring.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nadir.blockring.data.contacts.ContactsManager
import com.nadir.blockring.ui.components.ContactSection
import com.nadir.blockring.ui.components.EmptyState
import com.nadir.blockring.ui.components.SectionHeader
import com.nadir.blockring.ui.permissions.hasContactsPermission

@Composable
fun HiddenContent() {

    val context = LocalContext.current

    var hiddenContacts by remember {
        mutableStateOf(
            if (hasContactsPermission(context)) {
                ContactsManager(context).getHiddenContacts()
            } else {
                emptyList()
            }
        )
    }

    if (hiddenContacts.isEmpty()) {
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
            SectionHeader(emoji = "🙈", title = "Скрытые контакты", count = hiddenContacts.size)
        }

        items(hiddenContacts) { contact ->

            ContactSection(
                contacts = listOf(contact),
                isHiddenScreen = true,
                onUnhideContact = { unhiddenContact ->

                    hiddenContacts = hiddenContacts.filter {
                        it.phoneNumber != unhiddenContact.phoneNumber
                    }

                }
            )

        }

    }

}
