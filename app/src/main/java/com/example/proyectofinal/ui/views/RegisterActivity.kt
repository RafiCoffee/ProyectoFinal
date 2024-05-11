package com.example.proyectofinal.ui.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

    private var errorActual: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        if(generalService.comprobarUsuarioLogueado(this)){
            registerIntents(true)
        }

        asociarElementos()
        iniciarEventos()

        generalService.setTema(this)

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
            val foto = null

            if (comprobarRegistro(nombre, email, clave)){
                registrarUsuario(nombre, email, clave, foto)
            }else{
                errorTxt.text = errorActual
            }
        }

        //Pantalla Inicio Sesión
        iniciarSesionTxtBt.setOnClickListener {
            registerIntents(false)
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
    private fun registrarUsuario(nombre: String, email: String, clave: String, foto: String?){
        val newUser = Usuario(nombre, email, clave, foto)

        if(userService.registrarUsuario(newUser)){
            userService.iniciarSesion(generalService.getAuth(), email, clave)
                .addOnSuccessListener { iniciado ->
                    // Operación de inicio de sesión exitosa
                    val user = iniciado.user
                    registerIntents(true)
                }
                .addOnFailureListener { e ->
                    // Error durante el inicio de sesión
                    val errorCode = (e as FirebaseAuthException).errorCode
                    // Maneja el error
                }
        }else{
            errorActual = "Error desconocido al crear el usuario"
            errorTxt.text = errorActual
        }
    }
    private fun registerIntents(toHome: Boolean){
        val intentActivity = if(toHome){
            Intent(this, MainActivity::class.java)
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