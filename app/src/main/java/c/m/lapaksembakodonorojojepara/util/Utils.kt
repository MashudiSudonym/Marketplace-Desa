@file:Suppress("DEPRECATION")

package c.m.lapaksembakodonorojojepara.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import c.m.lapaksembakodonorojojepara.R
import c.m.lapaksembakodonorojojepara.ui.notification.NotificationActivity
import c.m.lapaksembakodonorojojepara.ui.user.userprofile.UserProfileActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun notificationSetup(
    context: Context?,
    title: String?,
    content: String?,
    channelId: Int?,
    notificationChannel: String?
) {
    // Declaration Notification
    val intentApp = Intent(context, UserProfileActivity::class.java).apply {
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
        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            notificationChannel,
            notificationChannel,
            NotificationManager.IMPORTANCE_HIGH
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

fun updateOrderStatusNotificationSetup(
    context: Context?,
    title: String?,
    content: String?,
    channelId: Int?,
    notificationChannel: String?
) {
    // Declaration Notification
    val intentApp = Intent(context, NotificationActivity::class.java).apply {
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
        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            notificationChannel,
            notificationChannel,
            NotificationManager.IMPORTANCE_HIGH
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
    result.addOnCompleteListener { task ->
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
                    Log.e("Error", "$e")
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }
}
