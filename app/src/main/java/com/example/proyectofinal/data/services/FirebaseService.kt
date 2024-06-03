package com.example.proyectofinal.data.services

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.ui.views.InicioSesionActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject

class FirebaseService @Inject constructor() {

    fun getAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    fun getUserId(): String{
        return getAuth().currentUser!!.uid
    }
    fun getStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
    fun getDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    fun cerrarSesion(context: Context){
        val auth = getAuth()
        auth.signOut()

        val intentInicioSesion = Intent(context, InicioSesionActivity::class.java)
        try {
            context.startActivity(intentInicioSesion)
        }catch (e : ActivityNotFoundException){
            Toast.makeText(context, "Error al acceder a la pantalla", Toast.LENGTH_SHORT).show()
        }
    }

    fun subirImagen(context: Context, imagen: Uri, currentUser: Usuario){
        val stream = context.contentResolver.openInputStream(imagen)
        val imageName = "${UUID.randomUUID()}.jpg"
        val imagesRef = getStorage().reference.child("images/${imageName}")

        if(stream != null){
            val uploadTask = imagesRef.putStream(stream)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->
                val currentUserId = getAuth().currentUser?.uid
                val referenciaUsuarios = getDatabase().getReference("usuario")
                lateinit var referenciaUsuario: DatabaseReference
                if (currentUserId != null){
                    referenciaUsuario = referenciaUsuarios.child(currentUserId)
                }

                val actualizacionUsuario = mapOf(
                    "nombre" to currentUser.nombre,
                    "email" to currentUser.email,
                    "clave" to currentUser.clave,
                    "foto" to imageName,
                    "idAmigo" to currentUser.idAmigo,
                    "Amigos" to currentUser.Amigos
                )

                referenciaUsuario.updateChildren(actualizacionUsuario)
                    .addOnSuccessListener {
                        // La actualización fue exitosa
                        println("Usuario actualizado exitosamente")
                    }
                    .addOnFailureListener { error ->
                        // Ocurrió un error al actualizar el usuario
                        println("Error al actualizar usuario: $error")
                    }
            }
        }
    }

    fun borrarImagen(imageName: String){
        val imageRef = getStorage().getReference().child("images/${imageName}")

        if(!imageName.equals("profile_default_negro.png")){
            imageRef.delete().addOnSuccessListener {
                // El archivo ha sido eliminado exitosamente
            }.addOnFailureListener { exception ->
                // Ha ocurrido un error al intentar eliminar el archivo
            }
        }
    }

    fun cambiarImagenUsuario(context: Context, imagenUsuario: ImageView, foto: String){
        val imageName = foto
        val url = getStorage().getReferenceFromUrl("gs://zapetask-api.appspot.com/images/$imageName")
        val contexto: Any = try {
            context as Activity
        } catch (e: ClassCastException) {

        }

        url.downloadUrl.addOnSuccessListener {
            if(contexto is Activity){
                if(!contexto.isDestroyed){
                    Glide.with(context)
                        .load(it).into(imagenUsuario)
                }
            }else{
                Glide.with(context)
                    .load(it).into(imagenUsuario)
            }
        }.addOnFailureListener {
            //Manejar Error
        }
    }
}