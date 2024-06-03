package com.example.proyectofinal.data.dataSource.dataBase

import android.service.autofill.UserData
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinal.data.dataSource.dataBase.dao.NotificacionDao
import com.example.proyectofinal.data.dataSource.dataBase.dao.TareaDao
import com.example.proyectofinal.data.dataSource.dataBase.dao.UsuarioDao
import com.example.proyectofinal.data.dataSource.dataBase.entities.Notificacion
import com.example.proyectofinal.data.dataSource.dataBase.entities.Tarea
import com.example.proyectofinal.data.dataSource.dataBase.entities.Usuario


@Database(entities = [Usuario::class, Tarea::class, Notificacion::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun tareaDao(): TareaDao
    abstract fun notificacionDao(): NotificacionDao
}
