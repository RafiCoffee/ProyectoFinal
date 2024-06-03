package com.example.proyectofinal.ui.views

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Notification
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.example.proyectofinal.R
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.modelView.FragmentViewModel
import com.example.proyectofinal.ui.modelView.UsuarioViewModel
import com.example.proyectofinal.ui.modelView.UsuarioViewModelFactory
import com.example.proyectofinal.ui.views.fragments.Tareas
import com.example.proyectofinal.ui.views.fragments.Zapet
import com.example.proyectofinal.ui.views.fragments.ZapetMedia
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class MainActivity: AppCompatActivity(), UsuarioCallback {
    lateinit var usuarioLogueado: Usuario

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchTheme: Switch
    private lateinit var switchThemeLogo: ImageView
    private lateinit var fotoPerfil: ImageView
    private lateinit var cerrarSesionBt: Button
    private lateinit var usernameTxt: TextView
    private lateinit var codigoAmigoTxt: TextView
    private lateinit var atrasCodigoAmigoBt: ImageView

    lateinit var contenedorCrearTarea: LinearLayout
    private lateinit var contenedorCodigoAmigo: ConstraintLayout
    lateinit var pantallaSemitransparente: LinearLayout

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService
    @Inject lateinit var firebaseService: FirebaseService

    private val viewModel: FragmentViewModel by viewModels()
    private val userViewModel: UsuarioViewModel by viewModels{
        UsuarioViewModelFactory(userService)
    }

    private var alturaPantalla = 0.0f
    private var anchoPantalla = 0.0f

    //PERMISOS
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

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
        esconderLayouts()
        askNotificationPermission()
        usuarioLogueado = intent.getParcelableExtra("usuario")!!

        usuarioLogueado.let {
            userViewModel.setLoggedUser(it)
        }

        onUsuarioObtenido(usuarioLogueado)

        //generalService.setTema(this)
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
        codigoAmigoTxt = findViewById(R.id.codigoAmigoTxt)
        atrasCodigoAmigoBt = findViewById(R.id.atrasCodigoAmigoBt)

        if(!this.isDestroyed){ firebaseService.cambiarImagenUsuario(this, fotoPerfil, usuarioLogueado.foto!!) }
        usernameTxt.text = usuarioLogueado.nombre
        codigoAmigoTxt.text = usuarioLogueado.idAmigo
    }
    fun iniciarEventos(){
        cerrarSesionBt.setOnClickListener {
            generalService.setUsuarioLogueado(this, false)
            firebaseService.cerrarSesion(this)
        }

        fotoPerfil.setOnClickListener {
            Toast.makeText(this, "Tareas: " + viewModel.viewCycleOwnerTareas, Toast.LENGTH_SHORT).show()
        }

        switchTheme.setOnCheckedChangeListener { _, _ -> cambiarTema() }

        codigoAmigoTxt.setOnClickListener {
            generalService.copiarPortapapeles(codigoAmigoTxt.text.toString(), this)
        }

        atrasCodigoAmigoBt.setOnClickListener {
            pantallaSemitransparente.visibility = LinearLayout.GONE
            val animator = ObjectAnimator.ofFloat(contenedorCodigoAmigo, "translationX", anchoPantalla)
            animator.duration = 750

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    contenedorCodigoAmigo.visibility = LinearLayout.GONE
                }
            })
            animator.start()
        }
    }

    private fun iniciarNav(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val bottomNav = findViewById<BottomNavigationView>(R.id.barraInferior)

        navHostFragment.let {
            // El fragmento no es nulo, realiza tus operaciones aquí
            navController = it.navController

            bottomNav.setupWithNavController(navController)
            bottomNav.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.pantallaZapet -> {
                        loadFragment(menuItem.itemId.toString(), Zapet())
                        Toast.makeText(this, "Zapet", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.pantallaTareas -> {
                        loadFragment(menuItem.itemId.toString(), Tareas())
                        true
                    }
                    R.id.pantallaZapetMedia -> {
                        loadFragment(menuItem.itemId.toString(), ZapetMedia())
                        Toast.makeText(this, "ZapetMedia", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            comprobarSwitchTema()
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
            when (opcion.itemId){
                R.id.perfilActivity -> {
                    val intentPerfilActivity = Intent(this, PerfilActivity::class.java).apply {
                        putExtra("usuario", usuarioLogueado)
                    }
                    mainIntents(intentPerfilActivity)
                }

                R.id.notificacionesActivity -> {
                    val intentInfoActivity = Intent(this, InfoActivity::class.java).apply {
                        putExtra("infoMode", 1)
                    }
                    mainIntents(intentInfoActivity)
                }

                R.id.amigosActivity -> {
                    val intentInfoActivity = Intent(this, InfoActivity::class.java).apply {
                        putExtra("infoMode", 2)
                    }
                    mainIntents(intentInfoActivity)
                }

                R.id.addAmigo -> {
                    val intentAddFriendActivity = Intent(this, AddAmigoActivity::class.java).apply {
                        putExtra("usuario", usuarioLogueado)
                    }
                    mainIntents(intentAddFriendActivity)
                }
            }

            /**
             * Info Activity:
             * Si recibe infoMode 1 muestra notificaciones
             * Si recibe infoMode 2 muestra amigos
             */

            // Cerrar el menú lateral
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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
        val nuevoTema = if(switchTheme.isChecked) 0 else 1

        if (nuevoTema != generalService.comprobarTema(this)) {
            sPEdit.putInt("Tema", nuevoTema)
            sPEdit.apply()

            generalService.setTema(this)
        }
    }
    private fun esconderLayouts(){
        alturaPantalla = generalService.getScreenHeight(this).toFloat()
        anchoPantalla = generalService.getScreenHeight(this).toFloat()

        pantallaSemitransparente = findViewById(R.id.pantallaSemiTransparente)
        pantallaSemitransparente.visibility = LinearLayout.GONE

        contenedorCrearTarea = findViewById(R.id.pantallaSaveTarea)
        contenedorCrearTarea.translationY = alturaPantalla
        contenedorCrearTarea.visibility = LinearLayout.GONE

        contenedorCodigoAmigo = findViewById(R.id.pantallaCodigoAmigo)
        contenedorCodigoAmigo.translationX = anchoPantalla
        contenedorCodigoAmigo.visibility = LinearLayout.GONE
    }

    private fun loadFragment(tag: String, fragment: Fragment) {
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach { frag ->
            transaction.hide(frag)
        }
        if (existingFragment == null) {
            transaction.add(R.id.fragmentContainerView, fragment, tag)
        } else {
            transaction.show(existingFragment)
        }
        transaction.commit()
    }

    private fun mainIntents(intent: Intent){
        try {
            startActivity(intent)
        }catch (e : ActivityNotFoundException){
            Toast.makeText(this, "Error al acceder a la pantalla", Toast.LENGTH_SHORT).show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId){
            R.id.idAmigo ->{
                pantallaSemitransparente.visibility = LinearLayout.VISIBLE
                contenedorCodigoAmigo.visibility = LinearLayout.VISIBLE
                contenedorCodigoAmigo.animate().translationX(0f).setDuration(750)
            }
        }
        return true
    }

    //RequestCode 10 -> Actualizar Foto De Perfil
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            firebaseService.cambiarImagenUsuario(this, fotoPerfil, usuarioLogueado.foto!!)
        }
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
            generalService.setUsuarioLogueado(this, false)
            firebaseService.cerrarSesion(this)
        }
    }

}