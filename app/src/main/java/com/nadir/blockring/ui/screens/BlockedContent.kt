package com.nadir.blockring.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.nadir.blockring.data.blocked.BlockedNumbersManager
import com.nadir.blockring.data.contacts.ContactsManager

@Composable
fun BlockedContent() {

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val blockedManager = remember { BlockedNumbersManager(context) }
    val contactsManager = remember {
        ContactsManager(context)
    }

    LaunchedEffect(Unit) {

        blockedManager.removeIfExistsInContacts(
            contactsManager
        )

    }

    val blockedNumbers = blockedManager.getBlocked().toList()

    if (blockedNumbers.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚫",
                    fontSize = 56.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Нет заблокированных номеров"
                )
            }
        }

        return
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

        items(blockedNumbers) { number ->

            var showMenu by remember { mutableStateOf(false) }

            Text(
                text = "🚫 $number",
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            showMenu = true
                        }
                    )
                    .padding(12.dp)
            )

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

        }

    }

}