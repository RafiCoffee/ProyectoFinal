package com.example.proyectofinal.data.dataSource.dataBase.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificacion")
data class Notificacion(
    @PrimaryKey(autoGenerate = false) val id: String,
    val tipoNotificacion: Int,
    val idUsuario: String,
    val idInfo: String,
    val seMuestra: Boolean
)
