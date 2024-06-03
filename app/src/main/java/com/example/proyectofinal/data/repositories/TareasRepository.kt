package com.example.proyectofinal.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.data.services.TareasService

@RequiresApi(Build.VERSION_CODES.Q)
class TareasRepository(private var tareasService: TareasService) {
    suspend fun getAll(userId: String): List<Tarea>{
        return tareasService.getAllTareas(userId)
    }

    suspend fun getByTipoTarea(userId: String, tipoTarea: Int): List<Tarea>{
        return tareasService.getAllTareasByTipoTarea(userId, tipoTarea)
    }

    suspend fun getAllTareasByFriend(friendId: String): List<Tarea>{
        return getAll(friendId)
    }

    suspend fun insertarTarea(nuevaTarea: Tarea, userId: String){
        tareasService.saveTareaDb(nuevaTarea, userId, true)
    }

    suspend fun editarTarea(tarea: Tarea, userId: String){
        tareasService.saveTareaDb(tarea, userId, false)
    }

    suspend fun eliminarTarea(tareaEliminar: Tarea){
        tareasService.eliminarTareaDb(tareaEliminar.id)
    }
}