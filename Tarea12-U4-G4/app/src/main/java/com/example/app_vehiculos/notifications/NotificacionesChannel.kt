package com.example.app_vehiculos.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificacionesChannel {
    const val LOCAL_CHANNEL_ID = "vehiculo_local"
    const val REMOTO_CHANNEL_ID = "vehiculo_remoto"

    fun crearCanales(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localChannel = NotificationChannel(
                LOCAL_CHANNEL_ID,
                "Operaciones Locales",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Notificaciones de acciones locales" }

            val remotoChannel = NotificationChannel(
                REMOTO_CHANNEL_ID,
                "Sincronización Remota",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Notificaciones de sincronización remota" }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(localChannel)
            manager.createNotificationChannel(remotoChannel)
        }
    }
}