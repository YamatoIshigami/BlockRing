package com.nadir.blockring.ui.screens

import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nadir.blockring.data.blocked.BlockedNumbersManager
import com.nadir.blockring.data.contacts.ContactsManager
import com.nadir.blockring.ui.components.EmojiAvatar
import com.nadir.blockring.ui.components.EmptyState
import com.nadir.blockring.ui.components.SectionHeader

@Composable
fun BlockedContent() {

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val blockedManager = remember { BlockedNumbersManager(context) }
    val contactsManager = remember { ContactsManager(context) }

    var blockedNumbers by remember { mutableStateOf<List<String>?>(null) }

    LaunchedEffect(Unit) {
        blockedManager.removeIfExistsInContacts(contactsManager)
        blockedNumbers = blockedManager.getBlocked().toList()
    }

    val currentBlocked = blockedNumbers

    if (currentBlocked == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (currentBlocked.isEmpty()) {
        EmptyState(
            emoji = "🚫",
            message = "Нет заблокированных номеров",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

        item {
            SectionHeader(emoji = "🚫", title = "Заблокированные номера", count = currentBlocked.size)
        }

        items(
            items = currentBlocked,
            key = { it }
        ) { number ->

            var showMenu by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showMenu = true }
                    ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmojiAvatar(
                        emoji = "🚫",
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = number,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = {
                    showMenu = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("📋 Копировать номер")
                    },
                    onClick = {

                        clipboard.setText(
                            AnnotatedString(number)
                        )

                        Toast.makeText(
                            context,
                            "📋 Номер скопирован",
                            Toast.LENGTH_SHORT
                        ).show()

                        showMenu = false

                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}