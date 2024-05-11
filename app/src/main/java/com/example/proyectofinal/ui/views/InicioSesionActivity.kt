package com.example.proyectofinal.ui.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InicioSesionActivity: AppCompatActivity() {
    private lateinit var emailTxt: EditText
    private lateinit var claveTxt: EditText
    private lateinit var errorTxt: TextView
    private lateinit var iniciarSesionBt: Button
    private lateinit var registerTxtBt: TextView

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService

    private var errorActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        generalService.setTema(this)
        asociarElementos()
        iniciarEventos()
    }
    private fun asociarElementos(){
        emailTxt = findViewById(R.id.emailEditTxt)
        claveTxt = findViewById(R.id.claveEditTxt)
        errorTxt = findViewById(R.id.infoErrorReg)
        iniciarSesionBt = findViewById(R.id.finRegBt)
        registerTxtBt = findViewById(R.id.registerTxtBt)

        errorTxt.text = errorActual
    }
    private fun iniciarEventos(){
        //Boton de registro
        iniciarSesionBt.setOnClickListener{
            val email = emailTxt.text.toString()
            val clave = claveTxt.text.toString()

            comprobarInicioSesion(email, clave)
        }

        //Pantalla Inicio Sesión
        registerTxtBt.setOnClickListener {
            inicioSesionIntents(false)
        }
    }
    private fun comprobarInicioSesion(email: String, clave: String){
        val auth = generalService.getAuth()
        userService.iniciarSesion(auth, email, clave)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    inicioSesionIntents(true)
                }else{
                    errorActual = "Error al iniciar sesión"
                    errorTxt.text = errorActual
                }
            }
            .addOnFailureListener {
                errorActual = "Credenciales no válidas"
                errorTxt.text = errorActual
            }
    }

    private fun inicioSesionIntents(toHome: Boolean){
        val intentActivity = if(toHome){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, RegisterActivity::class.java)
        }

        try {
            startActivity(intentActivity)
        }catch (e : ActivityNotFoundException){
            Toast.makeText(this, "Error al acceder a la pantalla", Toast.LENGTH_SHORT).show()
        }
    }
}