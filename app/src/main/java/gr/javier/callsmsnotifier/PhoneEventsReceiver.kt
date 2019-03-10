package gr.javier.callsmsnotifier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import androidx.work.*
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

class PhoneEventsReceiver : BroadcastReceiver() {

    private val TAG = "PhoneEventsReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d(TAG, "${intent.action}")

        when(intent.action) {
            "android.provider.Telephony.SMS_RECEIVED" -> sms(intent)
            "android.intent.action.PHONE_STATE" -> {
                when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                    TelephonyManager.EXTRA_STATE_RINGING -> incomingCall(intent)
                }

            }
        }
    }
}

private fun connected(): Constraints {
    return Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
}

private fun <T:WorkRequest.Builder<*,*>> T.enqueue () {
    this.build().let { WorkManager.getInstance().enqueue(it) }
}

private fun sms(intent: Intent) {
    val pdus = ByteArrayOutputStream().let {
        ObjectOutputStream(it).writeObject(intent.getSerializableExtra("pdus"))
        Base64.encodeToString(it.toByteArray(), Base64.DEFAULT)
    }


    connected()
        .let {
            OneTimeWorkRequestBuilder<SmsWorker>().setConstraints(it)
        }
        .setInputData(workDataOf("pdus" to pdus , "format" to intent.getStringExtra("format")))
        .enqueue()
}

private fun incomingCall(intent: Intent) {
    connected()
        .let {
            OneTimeWorkRequestBuilder<IncomingCallWorker>().setConstraints(it)
        }
        .setInputData(workDataOf("number" to intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)))
        .enqueue()
}