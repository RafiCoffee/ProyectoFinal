package com.example.proyectofinal.data.dataSource.dataBase.dao

import android.service.autofill.UserData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectofinal.data.dataSource.dataBase.entities.Tarea
import com.example.proyectofinal.data.dataSource.dataBase.entities.Usuario


@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario")
    fun getAll(): List<Usuario>

    @Query("SELECT * FROM usuario WHERE id = :id")
    fun getById(id: Int): Usuario?

    @Insert
    fun insert(usuario: Usuario)

    @Update
    fun update(usuario: Usuario)

    @Delete
    fun delete(usuario: Usuario)
}
