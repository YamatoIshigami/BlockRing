package com.nadir.blockring.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nadir.blockring.data.contacts.ContactsManager
import com.nadir.blockring.data.hidden.HiddenContactsManager
import com.nadir.blockring.ui.components.ContactSection
import com.nadir.blockring.ui.permissions.hasContactsPermission
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun HiddenContent() {

    val context = LocalContext.current

    val hiddenManager = HiddenContactsManager(context)

    var hiddenContacts by remember {
        mutableStateOf(
            if (hasContactsPermission(context)) {
                ContactsManager(context).getHiddenContacts()
            } else {
                emptyList()
            }
        )
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

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