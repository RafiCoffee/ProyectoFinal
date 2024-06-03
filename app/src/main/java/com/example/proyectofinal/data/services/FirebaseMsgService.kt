package com.example.proyectofinal.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.proyectofinal.R
import com.example.proyectofinal.ui.views.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMsgService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Manejar la recepción de la notificación aquí
        if (remoteMessage.notification != null) {
            // Obtener el título y el cuerpo de la notificación
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body

            // Mostrar la notificación al usuario
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        // Construir y mostrar la notificación
        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}