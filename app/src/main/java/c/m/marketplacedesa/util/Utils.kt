package c.m.marketplacedesa.util

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import c.m.marketplacedesa.R
import c.m.marketplacedesa.ui.user.main.MainActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.text.SimpleDateFormat
import java.util.*

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

@SuppressLint("SimpleDateFormat")
fun simpleDateFormat(originalDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd")
    val outputFormat = SimpleDateFormat("EEEE, dd MMM yyy")
    val date = inputFormat.parse(originalDate)
    return outputFormat.format(date as Date)
}

fun notificationSetup(
    context: Context?,
    title: String?,
    content: String?,
    channelId: Int?,
    notificationChannel: String?
) {
    // Declaration Notification
    val intentApp = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intentApp,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val notificationBuilder = NotificationCompat.Builder(
        context as Context,
        notificationChannel as String
    )
        .setDefaults(Notification.DEFAULT_ALL)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(content)
        )
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            notificationChannel,
            notificationChannel,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setShowBadge(true)
            canShowBadge()
            enableLights(true)
            lightColor = Color.BLUE
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            lockscreenVisibility = 1
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    with(NotificationManagerCompat.from(context)) {
        notify(channelId as Int, notificationBuilder.build())
    }
}

@Suppress("DEPRECATION", "UNUSED_VARIABLE")
fun displayLocationSettingRequest(context: Context) {
    val mGoogleApiClient = GoogleApiClient.Builder(context)
        .addApi(LocationServices.API)
        .build()
    mGoogleApiClient.connect()

    val locationRequest = LocationRequest.create()
    locationRequest.apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10000
        fastestInterval = 10000 / 2
    }

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
    builder.setAlwaysShow(true)

    val result = LocationServices.getSettingsClient(context)
        .checkLocationSettings(builder.build())
    result.addOnCompleteListener(object : OnCompleteListener<LocationSettingsResponse> {
        override fun onComplete(task: Task<LocationSettingsResponse>) {
            try {
                val response = task.getResult(ApiException::class.java)
            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = ex as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            context as Activity,
                            Constants.REQUEST_PERMISSION_CODE
                        )
                    } catch (e: IntentSender.SendIntentException) {

                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }

        }
    })
}
