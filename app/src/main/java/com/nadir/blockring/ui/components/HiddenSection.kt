package com.nadir.blockring.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nadir.blockring.model.Contact

@Composable
fun HiddenSection(
    contacts: List<Contact>
) {

    Text("🙈 Скрытые контакты")

    Spacer(modifier = Modifier.height(12.dp))

    for (contact in contacts) {

        Text("👤 ${contact.name}")
        Text(contact.phoneNumber)

        Spacer(modifier = Modifier.height(12.dp))
    }
}