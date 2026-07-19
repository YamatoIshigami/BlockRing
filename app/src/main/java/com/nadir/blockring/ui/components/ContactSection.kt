package com.nadir.blockring.ui.components

import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import com.nadir.blockring.data.hidden.HiddenContactsManager
import com.nadir.blockring.model.Contact

/**
 * Lazily renders a list of contacts inside an existing LazyColumn.
 * IMPORTANT: call this directly inside a LazyColumn { ... } block —
 * do NOT wrap it in item { } or the whole list will be composed eagerly again.
 */
fun LazyListScope.contactSection(
    contacts: List<Contact>,
    onHideContact: (Contact) -> Unit = {},
    onUnhideContact: (Contact) -> Unit = {},
    isHiddenScreen: Boolean = false
) {
    items(
        items = contacts,
        key = { it.phoneNumber }
    ) { contact ->
        ContactRow(
            contact = contact,
            isHiddenScreen = isHiddenScreen,
            onHideContact = onHideContact,
            onUnhideContact = onUnhideContact
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun ContactRow(
    contact: Contact,
    isHiddenScreen: Boolean,
    onHideContact: (Contact) -> Unit,
    onUnhideContact: (Contact) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showHideDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val hiddenManager = remember { HiddenContactsManager(context) }

    Box {
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
                    emoji = if (isHiddenScreen) "🙈" else "👤",
                    containerColor = if (isHiddenScreen)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "⋮",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(8.dp)
                        .combinedClickable(
                            onClick = { showMenu = true },
                            onLongClick = {}
                        )
                )
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("📋 Копировать номер") },
                onClick = {
                    clipboardManager.setText(
                        AnnotatedString(contact.phoneNumber)
                    )

                    Toast.makeText(
                        context,
                        "📋 Номер скопирован",
                        Toast.LENGTH_SHORT
                    ).show()

                    showMenu = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        if (isHiddenScreen)
                            "👁️ Раскрыть"
                        else
                            "🙈 Скрыть"
                    )
                },
                onClick = {
                    showMenu = false
                    showHideDialog = true
                }
            )
        }
    }

    if (showHideDialog) {
        AlertDialog(
            onDismissRequest = {
                showHideDialog = false
            },
            title = {
                Text(
                    if (isHiddenScreen)
                        "Раскрыть контакт"
                    else
                        "Скрыть контакт"
                )
            },
            text = {
                Text(
                    if (isHiddenScreen)
                        "Вы действительно хотите раскрыть этот контакт в списке?"
                    else
                        "Вы действительно хотите скрыть этот контакт из списка?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isHiddenScreen) {
                            hiddenManager.unhide(contact.phoneNumber)
                            onUnhideContact(contact)
                        } else {
                            hiddenManager.hide(contact.phoneNumber)
                            onHideContact(contact)
                        }
                        showHideDialog = false
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showHideDialog = false
                    }
                ) {
                    Text("Нет")
                }
            }
        )
    }
}