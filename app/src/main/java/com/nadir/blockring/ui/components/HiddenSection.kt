package com.nadir.blockring.ui.components

import androidx.compose.foundation.lazy.LazyListScope
import com.nadir.blockring.model.Contact

fun LazyListScope.hiddenSection(
    contacts: List<Contact>,
    onUnhideContact: (Contact) -> Unit = {}
) {
    item {
        SectionHeader(emoji = "🙈", title = "Скрытые контакты", count = contacts.size)
    }

    contactSection(
        contacts = contacts,
        isHiddenScreen = true,
        onUnhideContact = onUnhideContact
    )
}