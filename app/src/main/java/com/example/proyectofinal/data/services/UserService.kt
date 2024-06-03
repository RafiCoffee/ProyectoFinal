package com.example.proyectofinal.data.services

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.models.Usuario
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RequiresApi(Build.VERSION_CODES.Q)
class UserService @Inject constructor(private var firebaseService: FirebaseService, private var notificationService: NotificationService) {
    private var usuarioCallback: UsuarioCallback? = null
    private var friendUserObject: Amigo? = null

    fun setFriendUserObject(usuario: Usuario){
        friendUserObject = Amigo(firebaseService.getUserId(), usuario.nombre, usuario.foto, usuario.idAmigo, 0)
    }
    fun setUsuarioCallback(callback: UsuarioCallback) {
        usuarioCallback = callback
    }
    fun registrarUsuario(newUser: Usuario, callback: (Boolean) -> Unit) {
        val auth = firebaseService.getAuth()
        val usersRef = firebaseService.getDatabase().getReference().child("usuario")

        GlobalScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(newUser.email, newUser.clave)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser?.uid ?: ""
                        val userFriendId = generateFriendId()
                        val fotoPorDefecto = "profile_default_negro.png"

                        val userData = mapOf(
                            "nombre" to newUser.nombre,
                            "email" to newUser.email,
                            "clave" to newUser.clave,
                            "foto" to fotoPorDefecto,
                            "idAmigo" to userFriendId,
                            "Amigos" to mutableListOf<Amigo>()
                        )
                        usersRef.child(userId).setValue(userData)
                            .addOnCompleteListener { registrationTask ->
                                if (registrationTask.isSuccessful) {
                                    callback(true) // Llamamos al callback con true si el registro fue exitoso
                                } else {
                                    callback(false) // Llamamos al callback con false si ocurrió un error en el registro
                                }
                            }.addOnFailureListener { e ->
                                Log.e("PRUEBA", e.message.toString())
                            }.addOnSuccessListener {
                                Log.e("PRUEBA", "Success")
                            }.addOnCanceledListener {
                                Log.e("PRUEBA", "Cancelado")
                            }
                    } else {
                        callback(false)
                    }
                }
        }
    }

    suspend fun getAllFriends(): List<Amigo> = suspendCancellableCoroutine { continuation ->
        val amigosRecuperados: MutableList<Amigo> = mutableListOf()
        val usersRef = firebaseService.getDatabase().getReference("usuario")

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (friendSnapshot in dataSnapshot.children) {
                    val usuario = friendSnapshot.getValue(Usuario::class.java)

                    if (!friendSnapshot.key.equals(firebaseService.getUserId())) {
                        if(usuario != null) {
                            val amigo = Amigo(
                                friendSnapshot.key.toString(),
                                usuario.nombre,
                                usuario.foto,
                                usuario.idAmigo,
                                0
                            )
                            amigosRecuperados.add(amigo)
                        }
                    }
                }
                continuation.resume(amigosRecuperados)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        }

        usersRef.addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            usersRef.removeEventListener(listener)
        }
    }

    suspend fun getAllActualFriends(): List<Amigo> = suspendCancellableCoroutine { continuation ->
        val amigosRecuperados: MutableList<Amigo> = mutableListOf()
        val currentUser = firebaseService.getAuth().currentUser
        val usersRef = firebaseService.getDatabase().getReference("usuario/${currentUser?.uid}/Amigos")

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (friendSnapshot in dataSnapshot.children) {
                    val amigo = friendSnapshot.getValue(Amigo::class.java)

                    if(amigo != null){
                        amigosRecuperados.add(amigo)
                    }

                }
                continuation.resume(amigosRecuperados)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        }

        usersRef.addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            usersRef.removeEventListener(listener)
        }
    }

    fun addFriendToUser(nuevoAmigo: Amigo){
        nuevoAmigo.estadoSolicitud = 1
        val currentUser = firebaseService.getAuth().currentUser
        val userRef = firebaseService.getDatabase().getReference().child("usuario/${currentUser?.uid}")

        userRef.child("Amigos").get().addOnSuccessListener { dataSnapshot ->
            val amigosList = dataSnapshot.getValue(object : GenericTypeIndicator<MutableList<Amigo>>() {}) ?: mutableListOf()
            amigosList.add(nuevoAmigo)

            userRef.child("Amigos").setValue(amigosList).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addFriendToOtherUser(nuevoAmigo.id)
                    println("Amigo agregado exitosamente.")
                } else {
                    println("Error al agregar amigo: ${task.exception?.message}")
                }
            }
        }.addOnFailureListener {
            println("Error al obtener lista de amigos: ${it.message}")
        }
    }

    fun addFriendToOtherUser(otherUserId: String){
        val usuarioActualAmigo = friendUserObject
        usuarioActualAmigo!!.estadoSolicitud = 2
        val userRef = firebaseService.getDatabase().getReference().child("usuario/${otherUserId}")

        userRef.child("Amigos").get().addOnSuccessListener { dataSnapshot ->
            val amigosList = dataSnapshot.getValue(object : GenericTypeIndicator<MutableList<Amigo>>() {}) ?: mutableListOf()
            amigosList.add(usuarioActualAmigo)

            userRef.child("Amigos").setValue(amigosList).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    notificationService.insertNotificacionDb(null, usuarioActualAmigo, otherUserId)
                    println("Amigo agregado exitosamente.")
                } else {
                    println("Error al agregar amigo: ${task.exception?.message}")
                }
            }
        }.addOnFailureListener {
            println("Error al obtener lista de amigos: ${it.message}")
        }
    }

    fun acceptFriend(idAmigo: String){
        val currentUser = firebaseService.getAuth().currentUser
        val userRef = firebaseService.getDatabase().getReference().child("usuario/${currentUser?.uid}")

        userRef.child("Amigos").get().addOnSuccessListener { dataSnapshot ->
            val amigosList = dataSnapshot.getValue(object : GenericTypeIndicator<MutableList<Amigo>>() {}) ?: mutableListOf()

            var amigoEncontrado = false
            for (amigo in amigosList) {
                if (amigo.id == idAmigo) {
                    amigo.estadoSolicitud = 3
                    amigoEncontrado = true
                    break
                }
            }

            if (amigoEncontrado) {
                userRef.child("Amigos").setValue(amigosList).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        acceptFriendOtherUser(idAmigo)
                        println("Estado de amigo actualizado exitosamente.")
                    } else {
                        println("Error al actualizar estado del amigo: ${task.exception?.message}")
                    }
                }
            } else {
                println("Amigo no encontrado en la lista.")
            }
        }.addOnFailureListener {
            println("Error al obtener lista de amigos: ${it.message}")
        }
    }

    fun acceptFriendOtherUser(idAmigo: String){
        val currentUser = firebaseService.getAuth().currentUser
        val userRef = firebaseService.getDatabase().getReference().child("usuario/${idAmigo}")
        val actualUserId = currentUser?.uid

        userRef.child("Amigos").get().addOnSuccessListener { dataSnapshot ->
            val amigosList = dataSnapshot.getValue(object : GenericTypeIndicator<MutableList<Amigo>>() {}) ?: mutableListOf()

            var amigoEncontrado = false
            for (amigo in amigosList) {
                if (amigo.id == actualUserId) {
                    amigo.estadoSolicitud = 3
                    amigoEncontrado = true
                    break
                }
            }

            if (amigoEncontrado) {
                userRef.child("Amigos").setValue(amigosList).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Estado de amigo actualizado exitosamente.")
                    } else {
                        println("Error al actualizar estado del amigo: ${task.exception?.message}")
                    }
                }
            } else {
                println("Amigo no encontrado en la lista.")
            }
        }.addOnFailureListener {
            println("Error al obtener lista de amigos: ${it.message}")
        }
    }

    fun rejectFriend(idAmigo: String){

    }

    fun getUserById(userId: String, callback: (Usuario?) -> Unit){
        val usersRef = FirebaseDatabase.getInstance().getReference("usuario").child(userId)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                callback(usuario)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de lectura de la base de datos
                callback(null)
            }
        })
    }

    fun generateUserId(): String? {
        val usuariosRef = firebaseService.getDatabase().getReference().child("usuario")
        return usuariosRef.push().key
    }

    fun generateFriendId(): String {
        val friendRef = firebaseService.getDatabase().getReference().child("usuario").child("amigos")
        val friendId = "#FR${friendRef.push().key}"
        return friendId
    }

    fun iniciarSesion(email: String, clave: String): Task<AuthResult> {
        return firebaseService.getAuth().signInWithEmailAndPassword(email, clave)
    }
    fun obtenerDatosUsuarioActual(callback: (Usuario?) -> Unit) {
        val currentUser = firebaseService.getAuth().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val usersRef = FirebaseDatabase.getInstance().getReference("usuario").child(userId)

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usuario = snapshot.getValue(Usuario::class.java)
                    callback(usuario)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores de lectura de la base de datos
                    callback(null)
                }
            })
        } else {
            // El usuario no ha iniciado sesión
            callback(null)
        }
    }

}