package com.nadir.blockring.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nadir.blockring.model.EmergencyService

@Composable
fun EmergencySection(
    numbers: List<EmergencyService>
) {

    for (number in numbers) {

        Text("🚨 ${number.name}")

        Text(number.phoneNumber)

        Spacer(modifier = Modifier.height(12.dp))
    }
}