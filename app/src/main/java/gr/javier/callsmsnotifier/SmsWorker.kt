package gr.javier.callsmsnotifier

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Base64
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.Serializable

class SmsWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private val TAG = "SmsWorker"

    override fun doWork(): Result {
        var pdus = inputData.getString("pdus")
            .let{ Base64.decode(it, Base64.DEFAULT) }
            .let{ ByteArrayInputStream(it) }
            .let{ ObjectInputStream(it) }
            .readObject()

        var messages = Intent()
            .putExtra("pdus", pdus as Serializable)
            .putExtra("format", inputData.getString("format"))
            .let { Telephony.Sms.Intents.getMessagesFromIntent(it) }

        Log.d(TAG, "Incoming SMS!!!! ${messages[0].displayMessageBody}")

        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }
}
