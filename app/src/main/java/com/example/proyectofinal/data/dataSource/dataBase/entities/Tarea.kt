package com.example.proyectofinal.data.dataSource.dataBase.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = false) val id: String,
    val nombreTarea: String,
    val hora: String,
    val fecha: String,
    val recordatorio: String,
    val tipoTarea: Int
)
