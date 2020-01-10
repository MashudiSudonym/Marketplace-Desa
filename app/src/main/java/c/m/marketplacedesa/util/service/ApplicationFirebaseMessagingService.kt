package c.m.marketplacedesa.util.service

import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import c.m.marketplacedesa.R
import c.m.marketplacedesa.util.Constants
import c.m.marketplacedesa.util.newOrderNotificationSetup
import c.m.marketplacedesa.util.updateOrderStatusNotificationSetup
import c.m.marketplacedesa.util.worker.MyWorker
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

            if (it.title == getString(R.string.you_have_a_new_order)) {
                newOrderNotificationSetup(
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
