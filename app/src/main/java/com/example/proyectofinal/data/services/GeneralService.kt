package com.example.proyectofinal.data.services

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.ui.views.InicioSesionActivity
import com.example.proyectofinal.ui.views.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class GeneralService @Inject constructor() {
    private lateinit var sP: SharedPreferences
    fun cambioFotoPerfil(context : Context){

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Cambiar Foto De Perfil")

        val contenedorPadre = LinearLayout(context)
        contenedorPadre.orientation = LinearLayout.VERTICAL
        contenedorPadre.gravity = Gravity.CENTER

        val cardViewRedondo = CardView(context)
        cardViewRedondo.radius = 1000f
        cardViewRedondo.cardElevation = 10f
        cardViewRedondo.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val fotoPerfil = ImageView(context)
        fotoPerfil.setImageResource(R.drawable.foto_perfil)
        fotoPerfil.layoutParams = LinearLayout.LayoutParams(500, 500)
        fotoPerfil.scaleType = ImageView.ScaleType.CENTER_CROP
        cardViewRedondo.addView(fotoPerfil)
        contenedorPadre.addView(cardViewRedondo)

        builder.setView(contenedorPadre)

        builder.show()
    }

    fun abrirGaleria(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 1)
    }

    fun comprobarTema(context: Context) : Int{
        sP = context.getSharedPreferences("ShPr", MODE_PRIVATE)
        return sP.getInt("Tema",1)
    }

    fun setTema(context: Context){
        val tema = comprobarTema(context)

        if(tema == 0){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun comprobarUsuarioLogueado(context: Context) : Boolean{
        sP = context.getSharedPreferences("ShPr", MODE_PRIVATE)
        return sP.getBoolean("UsuarioLogueado",false)
    }
    fun setUsuarioLogueado(context: Context, isLogueado: Boolean){
        val sPEdit = context.getSharedPreferences("ShPr", MODE_PRIVATE).edit()
        sPEdit.putBoolean("UsuarioLogueado", isLogueado)
        sPEdit.commit()
    }

    fun isValidEmail(email: String): Boolean {
        val regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return regex.matches(email)
    }

    fun getAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
    fun getStorage(): FirebaseStorage{
        return FirebaseStorage.getInstance()
    }

    fun cerrarSesion(context: Context){
        setUsuarioLogueado(context, false)
        val auth = getAuth()
        auth.signOut()

        val intentInicioSesion = Intent(context, InicioSesionActivity::class.java)
        try {
            context.startActivity(intentInicioSesion)
        }catch (e : ActivityNotFoundException){
            Toast.makeText(context, "Error al acceder a la pantalla", Toast.LENGTH_SHORT).show()
        }
    }

    /*fun descargarImagen(){
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images")
        val imageRef = imagesRef.child("nombre_de_tu_imagen.jpg")

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            // Ahora puedes usar imageUrl para mostrar la imagen en tu aplicación
        }.addOnFailureListener {
            // Error al obtener la URL de descarga de la imagen
        }
    }

    fun subirImagenFirebaseStorage(imageUri: Uri) {
        val storageRef = getStorage().reference
        val imagesRef = storageRef.child("images/${imageUri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Obtener la URL de descarga de la imagen y guardarla en la base de datos
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                // Guardar la URL de descarga en Firebase Realtime Database
                subirImagenFirebaseRealDatabase(downloadUrl)
            }
        }.addOnFailureListener { exception ->
            // Manejar errores de carga de la imagen
        }
    }

    fun subirImagenFirebaseRealDatabase(imageUrl: String){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val databaseRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId)
            val userData = hashMapOf(
                "foto" to imageUrl
            )
            databaseRef.updateChildren(userData as Map<String, String>)
                .addOnSuccessListener {
                    // La URL de la imagen se ha guardado correctamente en la base de datos
                }
                .addOnFailureListener { exception ->
                    // Manejar errores al guardar la URL de la imagen
                }
        }
    }*/
}