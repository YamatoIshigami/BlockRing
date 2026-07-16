package com.nadir.blockring.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.nadir.blockring.data.hidden.HiddenContactsManager
import com.nadir.blockring.model.Contact

@Composable
fun ContactSection(
    contacts: List<Contact>,
    onHideContact: (Contact) -> Unit = {},
    onUnhideContact: (Contact) -> Unit = {},
    isHiddenScreen: Boolean = false
) {
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showHideDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val hiddenManager = remember { HiddenContactsManager(context) }

    for (contact in contacts) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                        },
                        onLongClick = {
                            selectedContact = contact
                            showMenu = true
                        }
                    )
                    .padding(12.dp)
            ) {
                Text("👤 ${contact.name}")
                Text(contact.phoneNumber)
            }

            DropdownMenu(
                expanded = showMenu && selectedContact == contact,
                onDismissRequest = {
                    showMenu = false
                }
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
        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showHideDialog && selectedContact != null) {
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
                        "Вы действительно хотите РАСКРЫТЬ этот контакт в список?"
                    else
                        "Вы действительно хотите СКРЫТЬ этот контакт из списка?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isHiddenScreen) {
                            hiddenManager.unhide(selectedContact!!.phoneNumber)
                            onUnhideContact(selectedContact!!)
                        } else {
                            hiddenManager.hide(selectedContact!!.phoneNumber)
                            onHideContact(selectedContact!!)
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