package com.nadir.blockring.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.nadir.blockring.data.blocked.BlockedNumbersManager
import com.nadir.blockring.data.contacts.ContactsManager
import com.nadir.blockring.data.protection.ProtectionManager

class BlockRingCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {

        Log.e("BlockRing", "========== SERVICE STARTED ==========")

        val number = callDetails.handle?.schemeSpecificPart ?: "UNKNOWN"
        val contactsManager = ContactsManager(applicationContext)

        val protectionManager = ProtectionManager(applicationContext)

        if (!protectionManager.isEnabled()) {

            Log.e("BlockRing", "PROTECTION DISABLED")

            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .build()
            )

            return
        }

        val isContact = contactsManager.isContact(number)

        Log.e("BlockRing", "NUMBER = $number")
        Log.e("BlockRing", "IS CONTACT = $isContact")

        if (isContact) {

            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .build()
            )

        } else {
            val blockedManager = BlockedNumbersManager(applicationContext)
            blockedManager.block(number)

            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
            )

        }
    }

}