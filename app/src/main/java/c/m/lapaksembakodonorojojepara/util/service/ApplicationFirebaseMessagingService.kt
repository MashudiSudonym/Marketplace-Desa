package c.m.lapaksembakodonorojojepara.util.service

import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import c.m.lapaksembakodonorojojepara.R
import c.m.lapaksembakodonorojojepara.util.Constants
import c.m.lapaksembakodonorojojepara.util.notificationSetup
import c.m.lapaksembakodonorojojepara.util.updateOrderStatusNotificationSetup
import c.m.lapaksembakodonorojojepara.util.worker.MyWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ApplicationFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(Constants.APP_FIREBASE_MESSAGING_SERVICE, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(
                Constants.APP_FIREBASE_MESSAGING_SERVICE,
                "Message data payload: " + remoteMessage.data
            )

            if (true) {
                scheduleJob()
            } else {
                handleNow()
            }
        }

        remoteMessage.notification?.let {
            Log.d(Constants.APP_FIREBASE_MESSAGING_SERVICE, "Message Notification Body: ${it.body}")

            if (it.title == "You Have a New Order" || it.title == "Anda Memiliki Pesanan Baru") {
                notificationSetup(
                    this,
                    it.title,
                    it.body,
                    6969,
                    getString(R.string.notification_channel)
                )
            } else {
                updateOrderStatusNotificationSetup(
                    this,
                    it.title,
                    it.body,
                    8080,
                    getString(R.string.notification_channel)
                )
            }
        }
    }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    private fun handleNow() {
        Log.d(
            Constants.APP_FIREBASE_MESSAGING_SERVICE,
            getString(R.string.short_lived_task_is_done)
        )
    }
}
