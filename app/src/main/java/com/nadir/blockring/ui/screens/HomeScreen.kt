package com.nadir.blockring.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.foundation.layout.navigationBarsPadding
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.nadir.blockring.ui.permissions.hasContactsPermission
import com.nadir.blockring.data.protection.ProtectionManager

@Composable
fun HomeScreen() {

    val context = LocalContext.current

    val protectionManager = remember {
        ProtectionManager(context)
    }

    var protectionEnabled by remember {
        mutableStateOf(protectionManager.isEnabled())
    }

    var hasPermission by remember {
        mutableStateOf(hasContactsPermission(context))
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasPermission = granted

        }

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "BlockRing",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))


        HorizontalDivider()

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(
            selectedTabIndex = selectedTab
        ) {

            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text("Контакты")
                }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text("Заблокировано")
                }
            )

            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text("Скрытые")
                }
            )

        }

        Box(
            modifier = Modifier.weight(1f)
        ) {

            if (!hasPermission) {

                Text(
                    text = "Для работы приложения необходимо разрешение на доступ к контактам.",
                    modifier = Modifier.align(Alignment.Center)
                )

            } else {

                when (selectedTab) {

                    0 -> ContactsContent()

                    1 -> BlockedContent()

                    2 -> HiddenContent()

                }

            }

        }

        Button(
            onClick = {

                if (!hasPermission) {

                    permissionLauncher.launch(
                        Manifest.permission.READ_CONTACTS
                    )

                    return@Button
                }

                protectionEnabled = !protectionEnabled

                protectionManager.setEnabled(
                    protectionEnabled
                )

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {

            Text(
                if (protectionEnabled)
                    "🟢 Защита включена"
                else
                    "🔴 Защита выключена"
            )

        }

    }

}