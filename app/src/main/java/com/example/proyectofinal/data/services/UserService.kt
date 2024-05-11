package com.example.proyectofinal.data.services

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.proyectofinal.R
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Usuario
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.checkerframework.checker.units.qual.K
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class UserService @Inject constructor() {
    private var usuarioCallback: UsuarioCallback? = null
    fun setUsuarioCallback(callback: UsuarioCallback) {
        usuarioCallback = callback
    }
    fun registrarUsuario(newUser: Usuario): Boolean{
        var isRegistroCorrecto = true
        val auth = FirebaseAuth.getInstance()
        //val storageRef = FirebaseStorage.getInstance().reference
        val usersRef = FirebaseDatabase.getInstance().getReference().child("usuario")
        val userId = usersRef.push().key // Genera un ID único para el nuevo usuario
        /*val imageName = "logo_provisional.png"
        val imagesRef = storageRef.child("images")
        val imageRef = imagesRef.child(imageName)

        Descargar la imagen desde Firebase Storage
        val ONE_MEGABYTE: Long = 1024 * 1024
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            // Convertir los bytes descargados en un bitmap
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            // Convertir el bitmap en una cadena base64
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val imageBytes: ByteArray = baos.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }.addOnFailureListener {
            isRegistroCorrecto = false
        }*/

        val userData = hashMapOf(
            "nombre" to newUser.nombre,
            "email" to newUser.email,
            "clave" to newUser.clave,
            "foto" to ""
            // Agrega otros datos adicionales según sea necesario
        )

        userId?.let {
            auth.createUserWithEmailAndPassword(newUser.email, newUser.clave)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        // Registro exitoso en Firebase Authentication
                        val firebaseUser = auth.currentUser
                        usersRef.child(firebaseUser?.uid ?: it).setValue(userData)
                    } else {
                        isRegistroCorrecto = false
                    }
                }
                .addOnFailureListener { e ->
                    isRegistroCorrecto = false
                }
        }

        return isRegistroCorrecto
    }

    fun iniciarSesion(auth: FirebaseAuth, email: String, clave: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, clave)
    }
    fun obtenerDatosUsuarioActual(callback: (Usuario?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

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