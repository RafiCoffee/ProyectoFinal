package com.example.proyectofinal.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.models.Notificacion
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.data.services.NotificationService
import com.example.proyectofinal.data.services.UserService

@RequiresApi(Build.VERSION_CODES.Q)
class NotificationRepository(private var notificationService: NotificationService) {
    suspend fun getAll(): List<Notificacion>{
        return notificationService.getAllDb()
    }

    fun eliminarNotificacion(notiId: String){
        return notificationService.eliminarNotificacionDb(notiId, null)
    }
}