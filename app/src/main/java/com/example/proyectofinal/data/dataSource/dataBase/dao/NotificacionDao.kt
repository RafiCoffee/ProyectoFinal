package com.example.proyectofinal.data.dataSource.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.proyectofinal.data.dataSource.dataBase.entities.Notificacion
import com.example.proyectofinal.data.dataSource.dataBase.entities.Tarea

@Dao
interface NotificacionDao {
    @Query("SELECT * FROM notificacion")
    fun getAll(): List<Notificacion>

    @Query("SELECT * FROM notificacion WHERE id = :id")
    fun getById(id: Int): Notificacion?

    @Insert
    fun insert(notificacion: Notificacion)

    @Update
    fun update(notificacion: Notificacion)

    @Delete
    fun delete(notificacion: Notificacion)
}
