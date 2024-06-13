package com.example.proyectofinal.ui.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinal.R
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.databinding.ActivityPerfilBinding
import com.example.proyectofinal.ui.modelView.UsuarioViewModel
import com.example.proyectofinal.ui.modelView.UsuarioViewModelFactory
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class PerfilActivity : AppCompatActivity(), UsuarioCallback {
    private lateinit var usuarioLogueado: Usuario
    private lateinit var recyclerView: RecyclerView

    private lateinit var imagenPerfil: ImageView
    private lateinit var nombreUsuario: TextView
    private lateinit var atrasBt: ImageView
    private lateinit var cambiarImagenBt: Button
    private lateinit var loadBar: ProgressBar

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService
    @Inject lateinit var firebaseService: FirebaseService

    private lateinit var perfilBinding: ActivityPerfilBinding

    private var fotoActualizada = false

    private val userViewModel: UsuarioViewModel by viewModels{
        UsuarioViewModelFactory(userService)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        perfilBinding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(perfilBinding.root)

        usuarioLogueado = intent.getParcelableExtra("usuario")!!

        usuarioLogueado.let {
            userViewModel.setLoggedUser(it)
        }

        onUsuarioObtenido(usuarioLogueado)
    }

    fun asociarElementos(){
        imagenPerfil = findViewById(R.id.fotoPerfil)
        nombreUsuario = findViewById(R.id.nombreUsuario)
        nombreUsuario.text = usuarioLogueado.nombre
        atrasBt = findViewById(R.id.atrasBt)
        cambiarImagenBt = findViewById(R.id.cambiarImagenBt)

        loadBar = perfilBinding.loadBar
    }

    fun iniciarEventos(){
        atrasBt.setOnClickListener {
            handleBackPress()
        }

        cambiarImagenBt.setOnClickListener {
            generalService.abrirGaleria(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        handleBackPress()
    }

    private fun handleBackPress() {
        val resultIntent = Intent().apply {
            putExtra("fotoActualizada", fotoActualizada)
            putExtra("usuario", usuarioLogueado)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Imagen Perfil
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            // Obtener la URI de la imagen seleccionada
            val imageUri = data.data

            if(imageUri != null){
                lifecycleScope.launch {
                    userViewModel.setLoaded(false)
                    firebaseService.borrarImagen(usuarioLogueado.foto!!)
                    firebaseService.subirImagen(this@PerfilActivity, imageUri, usuarioLogueado){
                        if(it){
                            imagenPerfil.setImageURI(imageUri)
                        }
                        fotoActualizada = it
                        userViewModel.setLoaded(true)
                        userViewModel.loadUsuariosInit()
                    }
                }
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

    fun registerLiveData(){
        userViewModel.userLoaded.observe(this){
            if(it){
                loadBar.visibility = LinearLayout.GONE
            }else{
                loadBar.visibility = LinearLayout.VISIBLE
            }
        }

        userViewModel.loggedUser.observe(this){
            usuarioLogueado = it!!
        }
    }

    override fun onUsuarioObtenido(usuario: Usuario?) {

        if(usuario != null){
            usuarioLogueado = usuario

            asociarElementos()
            iniciarBarraSuperior()
            registerLiveData()
            userViewModel.setLoaded(false)
            userViewModel.setLoggedUser(usuario)
            firebaseService.cambiarImagenUsuario(this, imagenPerfil, usuarioLogueado.foto!!){
                if(it){
                    iniciarEventos()
                }
                userViewModel.setLoaded(true)
            }
        }else{
            //Salir
            generalService.setUsuarioLogueado(this, false)
            firebaseService.cerrarSesion(this)
        }
    }
}