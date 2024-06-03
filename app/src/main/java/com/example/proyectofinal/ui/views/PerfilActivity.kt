package com.example.proyectofinal.ui.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.proyectofinal.R
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.modelView.UsuarioViewModel
import com.example.proyectofinal.ui.modelView.UsuarioViewModelFactory
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PerfilActivity : AppCompatActivity(), UsuarioCallback {
    private lateinit var usuarioLogueado: Usuario

    private lateinit var imagenPerfil: ImageView
    private lateinit var nombreUsuario: TextView
    private lateinit var atrasBt: ImageView
    private lateinit var cambiarImagenBt: Button

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService
    @Inject lateinit var firebaseService: FirebaseService

    private var fotoActualizada = false

    private val userViewModel: UsuarioViewModel by viewModels{
        UsuarioViewModelFactory(userService)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        usuarioLogueado = intent.getParcelableExtra("usuario")!!

        onUsuarioObtenido(usuarioLogueado)
    }

    fun asociarElementos(){
        imagenPerfil = findViewById(R.id.fotoPerfil)
        firebaseService.cambiarImagenUsuario(this, imagenPerfil, usuarioLogueado.foto!!)
        nombreUsuario = findViewById(R.id.nombreUsuario)
        nombreUsuario.text = usuarioLogueado.nombre
        atrasBt = findViewById(R.id.atrasBt)
        cambiarImagenBt = findViewById(R.id.cambiarImagenBt)
    }

    fun iniciarEventos(){
        atrasBt.setOnClickListener {
            if(fotoActualizada){
                setResult(10)
            }
                finish()
        }

        cambiarImagenBt.setOnClickListener {
            generalService.abrirGaleria(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Imagen Perfil
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            // Obtener la URI de la imagen seleccionada
            val imageUri = data.data

            if(imageUri != null){
                firebaseService.borrarImagen(usuarioLogueado.foto!!)
                firebaseService.subirImagen(this, imageUri, usuarioLogueado)
                imagenPerfil.setImageURI(imageUri)
                fotoActualizada = true
            }
        }
    }

    private fun iniciarBarraSuperior(){
        val toolbar = findViewById<Toolbar>(R.id.barraSuperior)
        toolbar.title = ""
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_basic, menu)
        return true
    }

    override fun onUsuarioObtenido(usuario: Usuario?) {

        if(usuario != null){
            usuarioLogueado = usuario

            asociarElementos()
            iniciarEventos()
            iniciarBarraSuperior()
        }else{
            //Salir
            generalService.setUsuarioLogueado(this, false)
            firebaseService.cerrarSesion(this)
        }
    }
}