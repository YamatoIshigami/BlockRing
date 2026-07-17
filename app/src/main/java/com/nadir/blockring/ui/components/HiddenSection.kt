package com.nadir.blockring.ui.components

import androidx.compose.runtime.Composable
import com.nadir.blockring.model.Contact

@Composable
fun HiddenSection(
    contacts: List<Contact>
) {
    SectionHeader(emoji = "🙈", title = "Скрытые контакты", count = contacts.size)

    ContactSection(
        contacts = contacts,
        isHiddenScreen = true
    )
}
