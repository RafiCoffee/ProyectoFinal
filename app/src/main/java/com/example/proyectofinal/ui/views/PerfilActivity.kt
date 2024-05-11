package com.example.proyectofinal.ui.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.proyectofinal.R
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PerfilActivity : AppCompatActivity(), UsuarioCallback {
    private lateinit var usuarioLogueado: Usuario

    private lateinit var imagenPerfil: ImageView
    private lateinit var atrasBt: ImageView
    private lateinit var cambiarImagenBt: Button

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        userService.setUsuarioCallback(this)
        userService.obtenerDatosUsuarioActual{ usuario ->
            onUsuarioObtenido(usuario)
        }

        generalService.setTema(this)

        atrasBt.setOnClickListener {
            val intentMainActivity = Intent(this, MainActivity::class.java)
            try {
                startActivity(intentMainActivity)
            }catch (e : ActivityNotFoundException){
                Toast.makeText(this, "Error al acceder a la pantalla", Toast.LENGTH_SHORT).show()
            }
        }

        cambiarImagenBt.setOnClickListener {
            generalService.abrirGaleria(this)
        }
    }

    fun asociarElementos(){
        imagenPerfil = findViewById(R.id.fotoPerfil)
        atrasBt = findViewById(R.id.atrasBt)
        cambiarImagenBt = findViewById(R.id.cambiarImagenBt)
    }

    fun iniciarEventos(){
        imagenPerfil.setImageResource(usuarioLogueado.foto.hashCode())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Imagen Perfil
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            // Obtener la URI de la imagen seleccionada
            val imageUri = data.data

            // Asignar la imagen seleccionada a la ImageView en tu actividad
            imagenPerfil.setImageURI(imageUri)

            /*if(imageUri != null){
                generalService.subirImagenFirebaseStorage(imageUri)
            }*/
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
            generalService.cerrarSesion(this)
        }
    }
}