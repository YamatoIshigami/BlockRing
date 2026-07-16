package com.nadir.blockring.data.emergency

import android.content.Context
import android.telephony.TelephonyManager
import com.nadir.blockring.model.EmergencyService

class EmergencyManager(
    private val context: Context
) {

    fun getEmergencyNumbers(): List<EmergencyService> {

        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE)
                    as TelephonyManager

        val country =
            telephonyManager.simCountryIso.lowercase()

        return when (country) {

            "uz" -> uzbekistanEmergencyNumbers

            else -> listOf()

        }
    }
}