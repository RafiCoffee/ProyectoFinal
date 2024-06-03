package com.example.proyectofinal.data.dataSource.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinal.data.dataSource.dataBase.dao.TareaDao
import com.example.proyectofinal.data.dataSource.dataBase.entities.Tarea

@Database(entities = [Tarea::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tareaDao(): TareaDao
}
