package com.nadir.blockring.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nadir.blockring.data.protection.ProtectionManager
import com.nadir.blockring.ui.permissions.hasContactsPermission

@OptIn(ExperimentalMaterial3Api::class)
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

    val tabs = listOf(
        "👥" to "Контакты",
        "🚫" to "Заблокировано",
        "🙈" to "Скрытые"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🛡️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BlockRing",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            ProtectionToggle(
                enabled = protectionEnabled,
                hasPermission = hasPermission,
                onToggle = {
                    if (!hasPermission) {
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    } else {
                        protectionEnabled = !protectionEnabled
                        protectionManager.setEnabled(protectionEnabled)
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, (emoji, label) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = "$emoji $label",
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            HorizontalDivider()

            Box(
                modifier = Modifier.weight(1f)
            ) {

                if (!hasPermission) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔒", fontSize = 48.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Для работы приложения необходимо разрешение на доступ к контактам",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                } else {

                    when (selectedTab) {

                        0 -> ContactsContent()

                        1 -> BlockedContent()

                        2 -> HiddenContent()

                    }

                }

            }

        }
    }
}

@Composable
private fun ProtectionToggle(
    enabled: Boolean,
    hasPermission: Boolean,
    onToggle: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (enabled)
            MaterialTheme.colorScheme.tertiaryContainer
        else
            MaterialTheme.colorScheme.errorContainer,
        animationSpec = tween(300),
        label = "protectionColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (enabled)
            MaterialTheme.colorScheme.onTertiaryContainer
        else
            MaterialTheme.colorScheme.onErrorContainer,
        animationSpec = tween(300),
        label = "protectionContentColor"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onToggle() },
        color = containerColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (!hasPermission) "🔒" else if (enabled) "🟢" else "🔴",
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        !hasPermission -> "Нужно разрешение"
                        enabled -> "Защита включена"
                        else -> "Защита выключена"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = when {
                        !hasPermission -> "Нажмите, чтобы предоставить доступ"
                        enabled -> "Незнакомые звонки блокируются"
                        else -> "Нажмите, чтобы включить блокировку"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
        }
    }
}
