package com.example.proyectofinal

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.proyectofinal.Fragmentos.Notas
import com.example.proyectofinal.Fragmentos.Tareas
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity: AppCompatActivity() {
    private lateinit var sP: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchTheme: Switch
    private lateinit var switchThemeLogo: ImageView
    private lateinit var fotoPerfil: ImageView

    private lateinit var fragmentoPadre: Fragment
    private lateinit var fragmentoNotas: Fragment

    private val CODIGO_DE_PERMISO = 42
    @RequiresApi(Build.VERSION_CODES.Q)
    private val PERMISOS_DE_UBICACION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        fragmentoPadre = Tareas()
        fragmentoNotas = Notas()

        sP = getSharedPreferences("ShPr", MODE_PRIVATE)
        switchTheme = findViewById(R.id.switchTema)
        switchThemeLogo = findViewById(R.id.switchTemaLogo)
        fotoPerfil = findViewById(R.id.fotoUsuarioImg)

        iniciarNav()
        iniciarBarraLateral()

        setTema()

        fotoPerfil.setOnClickListener {
            cambioFotoPerfil()
        }

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
    }

    private fun iniciarNav(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val bottomNav = findViewById<BottomNavigationView>(R.id.barraInferior)

        navHostFragment.let {
            // El fragmento no es nulo, realiza tus operaciones aquí
            navController = it.navController

            bottomNav.setupWithNavController(navController)
            comprobarTema()
            cambiarTema()
        }?: run{}
    }

    private fun iniciarBarraLateral(){
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

                //R.id.opcion_desplegablePrincipal -> toggleSubMenuVisibility()
            }

            // Cerrar el menú lateral
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    /*private fun toggleSubMenuVisibility() {
        val menu = navView.menu
        val submenu = menu.findItem(R.id.opcion_desplegable).subMenu

        // Cambia la visibilidad del submenú
        if (submenu != null) {
            submenu?.let {
                for(i in 0 until it.size()){
                    val hijo = it.getItem(i)
                    hijo.isVisible = !hijo.isVisible
                }
            }
        }
    }*/

    private fun comprobarTema(){
        val tema = sP.getInt("Tema",1)
        if(tema == 1){
            switchTheme.isChecked = false
            switchThemeLogo.setImageResource(R.drawable.tema_oscuro_off_logo)
        }else{
            switchTheme.isChecked = true
            switchThemeLogo.setImageResource(R.drawable.tema_oscuro_on_logo)
        }
    }

    private fun setTema(){
        val tema = sP.getInt("Tema", 1)
        if(tema == 0){
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }else{
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }
    }

    private fun cambiarTema(){
        val sPEdit: SharedPreferences.Editor = sP.edit()
        switchTheme.setOnClickListener {
            if(switchTheme.isChecked){
                sPEdit.putInt("Tema", 0)
                switchThemeLogo.setImageResource(R.drawable.tema_oscuro_off_logo)
            }else{
                sPEdit.putInt("Tema", 1)
                switchThemeLogo.setImageResource(R.drawable.tema_oscuro_on_logo)
            }
            sPEdit.commit()
            setTema()
        }
    }

    private fun cambioFotoPerfil(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar Foto De Perfil")

        val contenedorPadre = LinearLayout(this)
        contenedorPadre.orientation = LinearLayout.VERTICAL
        contenedorPadre.gravity = Gravity.CENTER

        val cardViewRedondo = CardView(this)
        cardViewRedondo.radius = 1000f
        cardViewRedondo.cardElevation = 10f
        cardViewRedondo.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val fotoPerfil = ImageView(this)
        fotoPerfil.setImageResource(R.drawable.foto_perfil)
        fotoPerfil.layoutParams = LinearLayout.LayoutParams(500, 500)
        fotoPerfil.scaleType = ImageView.ScaleType.CENTER_CROP
        cardViewRedondo.addView(fotoPerfil)
        contenedorPadre.addView(cardViewRedondo)

        builder.setView(contenedorPadre)

        builder.show()
    }

    // Manejar el evento de retroceso para cerrar el menú lateral si está abierto
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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

}