package com.example.proyectofinal.data.dataSource.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.proyectofinal.data.dataSource.dataBase.entities.Tarea

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas")
    fun getAll(): List<Tarea>

    @Query("SELECT * FROM tareas WHERE id = :id")
    fun getById(id: Int): Tarea?

    @Insert
    fun insert(tarea: Tarea)

    @Update
    fun update(tarea: Tarea)

    @Delete
    fun delete(tarea: Tarea)
}
