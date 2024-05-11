package com.example.proyectofinal.ui.views

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity(), UsuarioCallback {
    private lateinit var usuarioLogueado: Usuario

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchTheme: Switch
    private lateinit var switchThemeLogo: ImageView
    private lateinit var fotoPerfil: ImageView
    private lateinit var cerrarSesionBt: Button
    private lateinit var usernameTxt: TextView

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService

    /*private val CODIGO_DE_PERMISO = 42
    @RequiresApi(Build.VERSION_CODES.Q)
    private val PERMISOS_DE_UBICACION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )*/
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        userService.setUsuarioCallback(this)
        userService.obtenerDatosUsuarioActual{ usuario ->
            onUsuarioObtenido(usuario)
        }

        generalService.setTema(this)
        generalService.setUsuarioLogueado(this, true)

        /*
        // Solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permisos ya otorgados, proceder con la lógica relacionada con la ubicación
            //configurarGeofence()
        } else {
            // Solicitar permisos
            ActivityCompat.requestPermissions(
                this as Activity,
                PERMISOS_DE_UBICACION,
                CODIGO_DE_PERMISO
            )
        }*/

        //Solicitar permisos de galeria

    }
    fun asociarElementos(){
        switchTheme = findViewById(R.id.switchTema)
        switchThemeLogo = findViewById(R.id.switchTemaLogo)
        fotoPerfil = findViewById(R.id.fotoUsuarioImg)
        cerrarSesionBt = findViewById(R.id.cerrarSesionBt)
        usernameTxt = findViewById(R.id.usernameTxt)
    }
    fun iniciarEventos(){
        //fotoPerfil.setImageResource(usuarioLogueado.foto.hashCode())
        usernameTxt.text = usuarioLogueado.nombre

        cerrarSesionBt.setOnClickListener {
            generalService.cerrarSesion(this)
        }
    }

    private fun iniciarNav(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val bottomNav = findViewById<BottomNavigationView>(R.id.barraInferior)

        navHostFragment.let {
            // El fragmento no es nulo, realiza tus operaciones aquí
            navController = it.navController

            bottomNav.setupWithNavController(navController)

            comprobarSwitchTema()
            cambiarTema()
        }?: run{}
    }

    private fun iniciarBarraSuperiorYLateral(){
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.barraLateralOpciones)

        val toolbar = findViewById<Toolbar>(R.id.barraSuperior)
        setSupportActionBar(toolbar)

        // Configurar el icono de la barra de acción para abrir el menú lateral
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configurar la acción cuando se selecciona un elemento del menú
        navView.setNavigationItemSelectedListener { opcion ->
            // Acciones según el elemento seleccionado
            // Por ejemplo, puedes cambiar de fragmento o actividad aquí

            when (opcion.itemId){
                R.id.menu_option1 -> {
                    val intentPerfilActivity = Intent(this, PerfilActivity::class.java)
                    try {
                        startActivity(intentPerfilActivity)
                    }catch (e : ActivityNotFoundException){
                        Toast.makeText(this, "Error al acceder a la pantalla", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Cerrar el menú lateral
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // Manejar el evento de retroceso para cerrar el menú lateral si está abierto
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    fun comprobarSwitchTema() {
        var tema = generalService.comprobarTema(this)

        if(tema == 1){
            switchTheme.isChecked = false
            switchThemeLogo.setImageResource(R.drawable.tema_oscuro_off_logo)
        }else{
            switchTheme.isChecked = true
            switchThemeLogo.setImageResource(R.drawable.tema_oscuro_on_logo)
        }
    }

    fun cambiarTema(){
        val sPEdit: SharedPreferences.Editor = getSharedPreferences("ShPr", MODE_PRIVATE).edit()
        switchTheme.setOnClickListener {
            if(switchTheme.isChecked){
                sPEdit.putInt("Tema", 0)
                switchThemeLogo.setImageResource(R.drawable.tema_oscuro_off_logo)
            }else{
                sPEdit.putInt("Tema", 1)
                switchThemeLogo.setImageResource(R.drawable.tema_oscuro_on_logo)
            }
            sPEdit.commit()
            generalService.setTema(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_basic, menu)
        return true
    }

    override fun onUsuarioObtenido(usuario: Usuario?) {

        if(usuario != null){
            usuarioLogueado = usuario

            asociarElementos()
            iniciarNav()
            iniciarBarraSuperiorYLateral()
            iniciarEventos()
        }else{
            //Salir
            generalService.cerrarSesion(this)
        }
    }

}