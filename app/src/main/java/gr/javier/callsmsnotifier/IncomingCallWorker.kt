package gr.javier.callsmsnotifier

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class IncomingCallWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private val TAG = "IncomingCallWorker"

    override fun doWork(): Result {
        val number = this.inputData.getString("number")
        // Do the work here--in this case, upload the images.
        Log.d(TAG, "Incoming CALL form ${number}")

        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }
}