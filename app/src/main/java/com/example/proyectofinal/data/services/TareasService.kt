package com.example.proyectofinal.data.services

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.data.models.Notificacion
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.data.models.Usuario
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RequiresApi(Build.VERSION_CODES.Q)
class TareasService @Inject constructor(private var firebaseService: FirebaseService, private var notificationService: NotificationService) {
    suspend fun getAllTareas(userId: String): List<Tarea> = suspendCancellableCoroutine { continuation ->
        val tareasRecuperadas: MutableList<Tarea> = mutableListOf()
        val db = firebaseService.getDatabase()
        val tareasRef = db.getReference("tareas")
        val consulta = tareasRef.orderByChild("idUsuario").equalTo(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (tareaSnapshot in dataSnapshot.children) {
                    val tarea = tareaSnapshot.getValue(Tarea::class.java)
                    if (tarea != null) {
                        tarea.id = tareaSnapshot.key.toString()
                        tareasRecuperadas.add(tarea)
                    }
                }
                continuation.resume(tareasRecuperadas)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        }

        consulta.addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            consulta.removeEventListener(listener)
        }
    }

    suspend fun getAllTareasByTipoTarea(userId: String, tipoTarea: Int): List<Tarea> = suspendCancellableCoroutine { continuation ->
        val tareasRecuperadas: MutableList<Tarea> = mutableListOf()
        val db = firebaseService.getDatabase()
        val tareasRef = db.getReference("tareas")
        val consulta = tareasRef.orderByChild("idUsuario").equalTo(userId).orderByChild("tipoTarea").equalTo(tipoTarea.toString())

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (tareaSnapshot in dataSnapshot.children) {
                    val tarea = tareaSnapshot.getValue(Tarea::class.java)
                    if (tarea != null) {
                        tareasRecuperadas.add(tarea)
                    }
                }
                continuation.resume(tareasRecuperadas)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        }

        consulta.addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            consulta.removeEventListener(listener)
        }
    }

    fun getTareaById(tareaId: String, callback: (Tarea?) -> Unit){
        val tareasRef = FirebaseDatabase.getInstance().getReference("tareas").child(tareaId)

        tareasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tarea = snapshot.getValue(Tarea::class.java)
                callback(tarea)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de lectura de la base de datos
                callback(null)
            }
        })
    }

    fun generateTareaId(): String? {
        val tareasRef = firebaseService.getDatabase().getReference().child("tareas")
        return tareasRef.push().key
    }

    fun saveTareaDb(tarea: Tarea, userId: String, isInsert: Boolean){
        val tareasRef = firebaseService.getDatabase().getReference().child("tareas")
        val tareaId = tarea.id

        val tareaData = mapOf(
            "idUsuario" to userId,
            "nombreTarea" to tarea.nombreTarea,
            "hora" to tarea.hora,
            "fecha" to tarea.fecha,
            "recordatorio" to tarea.recordatorio,
            "tipoTarea" to tarea.tipoTarea,
            "completada" to tarea.completada,
            "fechaCompletada" to tarea.fechaCompletada
        )

        if(isInsert){
            tareasRef.child(tareaId).setValue(tareaData)
                .addOnCompleteListener { createTareaTask ->
                    if(createTareaTask.isSuccessful){
                        notificationService.insertNotificacionDb(tarea, null, null)
                    }
                }
        }else{
            tareasRef.child(tareaId).updateChildren(tareaData)
                .addOnSuccessListener {
                    // La actualización fue exitosa
                    println("Tarea actualizada exitosamente")
                }
                .addOnFailureListener { error ->
                    // Ocurrió un error al actualizar la tarea
                    println("Error al actualizar la tarea: $error")
                }
        }
    }

    fun eliminarTareaDb(idTarea: String){
        val tareasRef = firebaseService.getDatabase().getReference().child("tareas")

        tareasRef.child(idTarea).removeValue()
            .addOnSuccessListener {
                notificationService.eliminarNotificacionDb(null, idTarea)
                //Eliminado
            }
    }

    suspend fun comprobarTareas(): Boolean = suspendCancellableCoroutine { continuation ->
        val userId = firebaseService.getUserId()
        val tareasRecuperadas: MutableList<Tarea> = mutableListOf()
        val tareasRef = firebaseService.getDatabase().getReference("tareas")
        val consulta = tareasRef.orderByChild("idUsuario").equalTo(userId)

        val listener = object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (tareaSnapshot in dataSnapshot.children) {
                    val tarea = tareaSnapshot.getValue(Tarea::class.java)

                    if (tarea != null) {
                        tarea.id = tareaSnapshot.key.toString()
                        tareasRecuperadas.add(tarea)
                    }
                }

                val currentDate = Date()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy-HH:mm")

                for (tarea in tareasRecuperadas){
                    val fechaTarea = if(tarea.hora.isEmpty()){
                        currentDate.hours = 0
                        currentDate.minutes = 0
                        currentDate.seconds = 0
                        dateFormat.parse(tarea.fecha)!!
                    }else{
                        dateTimeFormat.parse("${tarea.fecha}-${tarea.hora}")!!
                    }

                    val calendar = Calendar.getInstance()
                    calendar.time = fechaTarea
                    calendar.add(Calendar.DAY_OF_YEAR, 2)
                    val fechaTareaMas2Dias = calendar.time

                    if(currentDate.after(fechaTareaMas2Dias)){
                        tareasRef.child(tarea.id).removeValue()
                            .addOnSuccessListener {
                                //Tarea Eliminada Si La Fecha Actual Tiene 2 Días De Diferencia
                            }
                    }
                }
                continuation.resume(true)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        }

        consulta.addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            consulta.removeEventListener(listener)
        }
    }
}