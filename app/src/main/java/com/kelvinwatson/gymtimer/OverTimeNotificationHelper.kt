package com.kelvinwatson.gymtimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class OverTimeNotificationHelper(private val context: Context) {
    init {
        createNotificationChannel()
    }

    private var notificationBuilder: NotificationCompat.Builder? = null

    fun onOverTime(secondsOverTime: Int = 0) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (notificationBuilder == null) {
            notificationBuilder = NotificationCompat.Builder(
                context,
                CHANNEL_ID_OVER_TIME
            )
            notificationBuilder?.let { builder ->
                builder.setContentTitle("Times up!")
                builder.setContentText("$secondsOverTime over time")
                builder.priority = NotificationCompat.PRIORITY_DEFAULT
                // Set the intent that will fire when the user taps the notification
                builder.setContentIntent(pendingIntent)
                // automatically removes the notification when the user taps it
                builder.setAutoCancel(true)
                // notificationId is a unique int for each notification that you must define
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
            }
        } else {
            notificationBuilder?.let { builder ->
                builder.setContentText("$secondsOverTime over time")
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    /**
     * Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is
     * new and not in the support library
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.over_time_channel_id)
            val descriptionText = context.getString(R.string.over_time_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_OVER_TIME, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID_OVER_TIME = "times_up_channel_id"
        const val NOTIFICATION_ID = 123
    }
}