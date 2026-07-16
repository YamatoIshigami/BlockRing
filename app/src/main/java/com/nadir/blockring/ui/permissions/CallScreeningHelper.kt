package com.nadir.blockring.ui.permissions

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun openCallScreeningSettings(context: Context) {

    context.startActivity(
        Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
    )

}