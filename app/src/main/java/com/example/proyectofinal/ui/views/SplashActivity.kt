package com.example.proyectofinal.ui.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinal.R
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.NotificationService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.modelView.UsuarioViewModel
import com.example.proyectofinal.ui.modelView.UsuarioViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class SplashActivity : AppCompatActivity(), UsuarioCallback {
    private lateinit var logoSplash: ImageView
    private lateinit var progressBarSplash: ProgressBar
    private lateinit var textoSplash: TextView

    var usuario: Usuario? = null
    private var dataLoaded = false

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService
    @Inject lateinit var notificationService: NotificationService
    @Inject lateinit var tareasService: TareasService

    private val viewModel: UsuarioViewModel by viewModels {
        UsuarioViewModelFactory(userService)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        asociarElementos()
        actualizarProgressBar()
        animacionTexto()
        comenzarAnimacion()
        viewModel.setLoaded(false)
        userService.setUsuarioCallback(this)

        val email = intent.getStringExtra("email")
        val clave = intent.getStringExtra("clave")
        val nuevoUsuario: Usuario? = intent.getParcelableExtra("usuario")

        if(email != null && clave != null){
            comprobarInicioSesion(email, clave)
        }else if(nuevoUsuario != null){
            registrarUsuario(nuevoUsuario)
        }else{
            viewModel.loadUsuariosInit()
        }

        viewModel.userLoaded.observe(this) { isLoaded ->
            if (isLoaded) {
                if(usuario != null){
                    lifecycleScope.launch {
                        comprobarTareasNotificacionesYActualizar()
                    }
                }else{
                    errorAlAcceder(false, "Error al recuperar los datos del usuario")
                }
            }
        }

        viewModel.loggedUser.observe(this){
            usuario = it
        }
    }

    private fun asociarElementos(){
        logoSplash = findViewById(R.id.logo_splash_screen)
        progressBarSplash = findViewById(R.id.progress_splash_screen)
        textoSplash = findViewById(R.id.texto_splash)
    }

    private fun animacionTexto(){
        lifecycleScope.launch {
            while (true){
                textoSplash.text = "Recordando tareas."
                delay(200)
                textoSplash.text = "Recordando tareas.."
                delay(500)
                textoSplash.text = "Recordando tareas..."
                delay(800)
            }
        }
    }

    private fun comenzarAnimacion(){
        val rotateAnim1 = ObjectAnimator.ofFloat(logoSplash, "rotation", -20f)
        rotateAnim1.duration = 250
        val rotateAnim2 = ObjectAnimator.ofFloat(logoSplash, "rotation", 390f)
        rotateAnim2.duration = 500
        val rotateAnim3 = ObjectAnimator.ofFloat(logoSplash, "rotation", 360f)
        rotateAnim3.duration = 350

        var repeatCount = 0

        rotateAnim1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!dataLoaded || repeatCount < 4){
                    repeatCount++
                    rotateAnim2.start()
                }
            }
        })
        rotateAnim2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!dataLoaded || repeatCount < 5){
                    repeatCount++
                    rotateAnim3.start()
                }
            }
        })
        rotateAnim3.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!dataLoaded || repeatCount < 6){
                    repeatCount++
                    rotateAnim1.start()
                }
            }
        })

        rotateAnim1.start()
    }

    private fun actualizarProgressBar() {
        lifecycleScope.launch {
            while (progressBarSplash.progress < 70 && !dataLoaded) {
                if(progressBarSplash.progress < 70){
                    progressBarSplash.progress += 2
                }
                delay(100)
            }
            if (dataLoaded) {
                acelerarProgressBar()
            }
        }
    }

    private fun acelerarProgressBar() {
        lifecycleScope.launch {
            while (progressBarSplash.progress < 100) {
                progressBarSplash.progress += 5
                delay(50)
            }
            goToMainActivity()
        }
    }

    private fun comprobarInicioSesion(email: String, clave: String){
        var errorActual: String
        userService.iniciarSesion(email, clave)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    viewModel.loadUsuariosInit()
                }else{
                    errorActual = "Error al iniciar sesión"
                    errorAlAcceder(false, errorActual)
                }
            }
            .addOnFailureListener {
                errorActual = "Credenciales no válidas"
                errorAlAcceder(false, errorActual)
            }
    }

    private fun registrarUsuario(newUser: Usuario){
        var errorActual: String
        userService.registrarUsuario(newUser) { registroExitoso ->
            if (registroExitoso) {
                comprobarInicioSesion(newUser.email, newUser.clave)
            } else {
                errorActual = "Error desconocido al crear el usuario"
                errorAlAcceder(true, errorActual)
            }
        }
    }

    private suspend fun comprobarTareasNotificacionesYActualizar() {
        try {
            tareasService.comprobarTareas()
            notificationService.comprobarNotificacionesTarea()

            dataLoaded = true
            if (progressBarSplash.progress < 100){
                acelerarProgressBar()
            }
        } catch (e: Exception) {
            println("Error al comprobar las notificaciones: ${e.message}")
            goToMainActivity() // Puedes decidir si quieres ir a la MainActivity en caso de error
        }
    }

    private fun goToMainActivity() {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("usuario", usuario)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun errorAlAcceder(toRegister: Boolean, error: String?){
        val errorIntent: Intent

        generalService.setUsuarioLogueado(this, false)

        if(toRegister){
            errorIntent = Intent(this, RegisterActivity::class.java).apply {
                putExtra("error", error)
            }
        }else{
            errorIntent = Intent(this, InicioSesionActivity::class.java).apply {
                putExtra("error", error)
            }
        }

        startActivity(errorIntent)
        finish()
    }

    override fun onUsuarioObtenido(usuario: Usuario?) {
        TODO("Not yet implemented")
    }
}