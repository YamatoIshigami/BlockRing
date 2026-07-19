package com.nadir.blockring.data.ussd

import android.content.Context
import android.telephony.TelephonyManager
import com.nadir.blockring.data.contacts.ContactsManager
import com.nadir.blockring.model.UssdCode

class UssdManager(
    private val context: Context
) {

    suspend fun getUssdCodes(): List<UssdCode> {

        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE)
                    as TelephonyManager

        val country = telephonyManager.simCountryIso.lowercase()

        val builtInCodes = when (country) {
            "uz" -> listOf(
                UssdCode("Проверить баланс", "*100#"),
                UssdCode("📱 Узнать свой номер", "*148#"),
                UssdCode("💳 Остаток интернета", "*102#")
            )
            else -> emptyList()
        }

        val simCodes = ContactsManager(context)
            .getUssdContacts()
            .map {
                UssdCode(
                    name = it.name,
                    code = it.phoneNumber
                )
            }

        return (builtInCodes + simCodes)
            .distinctBy {
                it.code
                    .replace(" ", "")
                    .replace("-", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace(";", "")
                    .replace(",", "")
                    .trim()
            }
    }

}