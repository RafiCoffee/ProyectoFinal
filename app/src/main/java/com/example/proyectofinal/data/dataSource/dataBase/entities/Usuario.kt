package com.example.proyectofinal.data.dataSource.dataBase.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyectofinal.data.models.Amigo

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey(autoGenerate = false) val id: String,
    val nombre: String,
    val email: String,
    val clave: String,
    val foto: String,
    val idAmigo: String,
    val Amigos: List<Amigo>
)
