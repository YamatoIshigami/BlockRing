package com.nadir.blockring.data.protection

import android.content.Context

class ProtectionManager(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "protection",
            Context.MODE_PRIVATE
        )

    fun isEnabled(): Boolean {
        return prefs.getBoolean("enabled", true)
    }

    fun setEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean("enabled", enabled)
            .apply()
    }

}