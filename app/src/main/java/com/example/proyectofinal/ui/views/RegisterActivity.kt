package com.example.proyectofinal.ui.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var usernameTxt: EditText
    private lateinit var emailTxt: EditText
    private lateinit var claveTxt: EditText
    private lateinit var confirmarClaveTxt: EditText
    private lateinit var errorTxt: TextView
    private lateinit var registerBt: Button
    private lateinit var iniciarSesionTxtBt: TextView

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService
    @Inject lateinit var firebaseService: FirebaseService

    private var errorActual: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        if(generalService.comprobarUsuarioLogueado(this)){
            registerIntents(true, null)
        }

        asociarElementos()
        iniciarEventos()

        //generalService.setTema(this)
    }
    private fun asociarElementos(){
        usernameTxt = findViewById(R.id.usernameEditTxt)
        emailTxt = findViewById(R.id.emailEditTxt)
        claveTxt = findViewById(R.id.claveEditTxt)
        confirmarClaveTxt = findViewById(R.id.confirmClaveEditTxt)
        errorTxt = findViewById(R.id.infoErrorReg)
        registerBt = findViewById(R.id.finRegBt)
        iniciarSesionTxtBt = findViewById(R.id.inicioSesionTxtBt)

        errorTxt.text = errorActual
    }
    private fun iniciarEventos(){
        //Boton de registro
        registerBt.setOnClickListener{
            val nombre = usernameTxt.text.toString()
            val email = emailTxt.text.toString()
            val clave = claveTxt.text.toString()

            if (comprobarRegistro(nombre, email, clave)){
                val nuevoUsuario = Usuario(userService.generateUserId()!!, nombre, email, clave, null, userService.generateFriendId(), emptyList())
                registerIntents(true, nuevoUsuario)
            }else{
                errorTxt.text = errorActual
            }
        }

        //Pantalla Inicio Sesión
        iniciarSesionTxtBt.setOnClickListener {
            registerIntents(false, null)
        }
    }
    private fun comprobarRegistro(nombre: String, email: String, clave: String): Boolean{
        var isRegistroCorrecto = true
        val confirmClave = confirmarClaveTxt.text.toString()

        if(nombre.isNotBlank() || nombre.isNotEmpty() || email.isNotBlank() || email.isNotEmpty() ||
            clave.isNotBlank() || clave.isNotEmpty() || confirmClave.isNotBlank() || confirmClave.isNotEmpty()){
            if(generalService.isValidEmail(email)){
                if(clave != confirmClave){
                    isRegistroCorrecto = false
                    errorActual = "Las contraseñas no coinciden"
                }
            }else{
                isRegistroCorrecto = false
                errorActual = "El email no es válido"
            }
        }else{
            isRegistroCorrecto = false
            errorActual = "Existe algún campo vacío"
        }

        return isRegistroCorrecto
    }
    private fun registerIntents(toHome: Boolean, usuario: Usuario?){
        val intentActivity = if(toHome){
            Intent(this, SplashActivity::class.java).apply {
                putExtra("usuario", usuario)
            }
        }else{
            Intent(this, InicioSesionActivity::class.java)
        }

        try {
            startActivity(intentActivity)
        }catch (e : ActivityNotFoundException){
            Toast.makeText(this, "Error al acceder a la pantalla", Toast.LENGTH_SHORT).show()
        }
    }
}