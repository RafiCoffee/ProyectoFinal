package com.example.proyectofinal.data.services

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.models.Notificacion
import com.example.proyectofinal.data.models.Tarea
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RequiresApi(Build.VERSION_CODES.Q)
class NotificationService @Inject constructor(private var firebaseService: FirebaseService) {

    suspend fun getAllDb(): List<Notificacion> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseService.getUserId()
        val notificacionesRecuperadas: MutableList<Notificacion> = mutableListOf()
        val notificationRef = firebaseService.getDatabase().getReference("notificacion")
        val consulta = notificationRef.orderByChild("idUsuario").equalTo(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (notificationSnapshot in dataSnapshot.children) {
                    val notificacion = notificationSnapshot.getValue(Notificacion::class.java)

                    if(notificacion != null) {
                        notificacion.id = notificationSnapshot.key.toString()
                        notificacionesRecuperadas.add(notificacion)
                    }
                }
                continuation.resume(notificacionesRecuperadas)
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

    fun insertNotificacionDb(tarea: Tarea?, amigo: Amigo?, otherUserId: String?){
        val userId = firebaseService.getUserId()
        val notificacionesRef = firebaseService.getDatabase().getReference().child("notificacion")
        val notificacionId = notificacionesRef.push().key

        lateinit var notificacion: Notificacion
        if(tarea != null){
            val tareaId = tarea.id
            notificacion = Notificacion(notificacionId!!, 1, userId, tareaId, false)
        }else if(amigo != null && otherUserId != null){
            val amigoId = amigo.id
            notificacion = Notificacion(notificacionId!!, 2, otherUserId, amigoId, true)
        }

        val notificacionData = mapOf(
            "tipoNotificacion" to notificacion.tipoNotificacion,
            "idUsuario" to notificacion.idUsuario,
            "idInfo" to notificacion.idInfo,
            "seMuestra" to notificacion.seMuestra
        )

        notificacionesRef.child(notificacionId!!).setValue(notificacionData)
            .addOnCompleteListener { createNotificacionTask ->
                if(createNotificacionTask.isSuccessful){
                    //Creado
                }
            }
    }

    suspend fun comprobarNotificacionesTarea(): List<Notificacion> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseService.getUserId()
        val notificacionesRecuperadas: MutableList<Notificacion> = mutableListOf()
        val notificationRef = firebaseService.getDatabase().getReference("notificacion")
        val consulta = notificationRef.orderByChild("idUsuario").equalTo(userId)

        val listener = object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (notificationSnapshot in dataSnapshot.children) {
                    val notificacion = notificationSnapshot.getValue(Notificacion::class.java)

                    if (notificacion != null && notificacion.tipoNotificacion == 1) {
                        notificacion.id = notificationSnapshot.key.toString()
                        notificacionesRecuperadas.add(notificacion)
                    }
                }

                val currentDate = Date()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy-HH:mm")
                val tareasRef = firebaseService.getDatabase().getReference("tareas")

                // Lista de tareas pendientes
                val notificacionesEliminadas = mutableListOf<Notificacion>()
                val tareasPendientes = notificacionesRecuperadas.mapNotNull { notificacion ->
                    tareasRef.child(notificacion.idInfo).get().continueWith { task ->
                        val tarea = task.result.getValue(Tarea::class.java)
                        if (tarea != null) {
                            if(!tarea.completada){
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
                                calendar.add(Calendar.DAY_OF_YEAR, -1)
                                val fechaTareaMenos1Dia = calendar.time
                                calendar.time = fechaTarea
                                calendar.add(Calendar.DAY_OF_YEAR, 1)
                                val fechaTareaMas1Dia = calendar.time
                                calendar.time = currentDate
                                calendar.add(Calendar.DAY_OF_YEAR, -1)
                                val currentDateMenos1Dia = calendar.time

                                if(fechaTareaMas1Dia.after(currentDateMenos1Dia)){
                                    notificacion.seMuestra = true
                                }else if(fechaTarea.before(currentDate)){
                                    notificacionesEliminadas.add(notificacion)
                                    notificationRef.child(notificacion.id).removeValue()
                                        .addOnSuccessListener {
                                            //Eliminado
                                        }
                                }
                            }else{
                                notificacionesRecuperadas.remove(notificacion)
                                notificationRef.child(notificacion.id).removeValue()
                                    .addOnSuccessListener {
                                        //Eliminado
                                    }
                            }
                        }else{
                            notificacionesRecuperadas.remove(notificacion)
                            notificationRef.child(notificacion.id).removeValue()
                                .addOnSuccessListener {
                                    //Eliminado
                                }
                        }
                        notificacion
                    }
                }

                // Esperar a que todas las tareas se completen
                Tasks.whenAllComplete(tareasPendientes).addOnCompleteListener {
                    notificacionesRecuperadas.removeAll(notificacionesEliminadas)
                    // Actualizar las notificaciones en la base de datos
                    for (notificacion in notificacionesRecuperadas) {
                        notificationRef.child(notificacion.id).setValue(notificacion)
                    }
                    continuation.resume(notificacionesRecuperadas)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
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

    fun eliminarNotificacionDb(idNotificacion: String?, tareaId: String?){
        val notificacionRef = firebaseService.getDatabase().getReference().child("notificacion")

        if(idNotificacion != null){
            notificacionRef.child(idNotificacion).removeValue()
                .addOnSuccessListener {
                    //Eliminado
                }
        } else if (tareaId != null) {
            val consulta = notificacionRef.orderByChild("idInfo").equalTo(tareaId)
            consulta.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (notificacionSnapshot in dataSnapshot.children) {
                        notificacionSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                // Eliminado exitosamente por ID de tarea
                                println("Notificación eliminada exitosamente por ID de tarea.")
                            }
                            .addOnFailureListener {
                                println("Error al eliminar la notificación: ${it.message}")
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error en la consulta: ${error.message}")
                }
            })
        } else {
            println("Debe proporcionar idNotificacion o tareaId.")
        }
    }
}