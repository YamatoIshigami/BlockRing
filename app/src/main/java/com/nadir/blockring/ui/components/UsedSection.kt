package com.nadir.blockring.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nadir.blockring.model.UssdCode

@Composable
fun UsedSection(
    codes: List<UssdCode>
) {

    Text("📱 USSD-коды")

    Spacer(modifier = Modifier.height(12.dp))

    for (code in codes) {

        Text("⭐ ${code.name}")
        Text(code.code)

        Spacer(modifier = Modifier.height(12.dp))
    }
}