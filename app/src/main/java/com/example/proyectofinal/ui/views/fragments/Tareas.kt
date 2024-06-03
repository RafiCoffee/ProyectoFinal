package com.example.proyectofinal.ui.views.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.proyectofinal.ui.adapters.TareasAdapter
import com.example.proyectofinal.R
import com.example.proyectofinal.data.interfaces.TareasListener
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.data.repositories.TareasRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.databinding.FragmentTareasBinding
import com.example.proyectofinal.ui.modelView.FragmentViewModel
import com.example.proyectofinal.ui.modelView.TareasViewModel
import com.example.proyectofinal.ui.modelView.TareasViewModelFactory
import com.example.proyectofinal.ui.views.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
@Suppress("DEPRECATION")
@AndroidEntryPoint
class Tareas : Fragment(), TareasListener/*, OnMapReadyCallback*/ {
    //private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //private lateinit var geofencingClient: GeofencingClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var annadirElementoBt: ImageView
    private var tipoTarea = 1
    private lateinit var tareasBinding: FragmentTareasBinding

    private lateinit var tareaEliminada: Tarea
    private var objetosList = mutableListOf<String>()
    private var fechaActual = "${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}/${Calendar.getInstance().get(Calendar.MONTH) + 1}/${Calendar.getInstance().get(Calendar.YEAR)}"
    private var latitud = 0.0
    private var longitud = 0.0

    //private lateinit var ubi: TextView
    //private lateinit var ubiBt: Button
    private lateinit var casaLogo: ImageView
    private lateinit var tareasLayout: TabLayout
    private lateinit var contenedorTareas: ViewPager2
    private lateinit var fecha: ConstraintLayout
    private lateinit var fechaElegidaTxt: TextView
    private lateinit var loadBar: ProgressBar

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var tareasService: TareasService
    @Inject lateinit var firebaseService: FirebaseService

    private var fragmentosIniciados = ArrayList<Boolean>()

    private val viewModel: TareasViewModel by viewModels {
        val repositorio = TareasRepository(tareasService)
        val pantallaCrearTarea = (requireActivity() as MainActivity).contenedorCrearTarea
        TareasViewModelFactory(firebaseService.getUserId(), repositorio, tareasService, pantallaCrearTarea)
    }

    /*private val PERMISOS_DE_UBICACION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        TODO("VERSION.SDK_INT < Q")
    }

    private companion object {
        private const val CODIGO_DE_PERMISO = 42
        private const val GEOFENCE_RADIUS_IN_METERS = 2.0  // Radio en metros
        private const val GEOFENCE_ID = "MiGeofence"
    }*/

    private lateinit var fragmentViewModel: FragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentosIniciados.add(false)
        fragmentosIniciados.add(false)
        fragmentosIniciados.add(false)

        fragmentViewModel = ViewModelProvider(requireActivity()).get(FragmentViewModel::class.java)
        fragmentViewModel.recyclerViewList.add(null)
        fragmentViewModel.recyclerViewList.add(null)
        fragmentViewModel.recyclerViewList.add(null)
        fragmentViewModel.listener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        tareasBinding = FragmentTareasBinding.inflate(inflater, container, false)

        fragmentViewModel.viewCycleOwnerTareas = viewLifecycleOwner

        return tareasBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ubi = view.findViewById(R.id.coordenadas)
        //ubiBt = view.findViewById(R.id.ubicacionBt)

        /*
        // Solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                view.context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("--- GEOFENCE ---", "Permisos si")
            //obtenerUbicacion()
        } else {
            Log.i("--- GEOFENCE ---", "Permisos no")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                CODIGO_DE_PERMISO
            )
            Log.i("--- GEOFENCE ---", "Después de solicitar permisos")
        }


        ubiBt.setOnClickListener {
            //mostrarMapa()
            //simularTransicionDeGeofence()
        }*/

        asociarElementos(view)
        iniciarEventos()
        registerLiveData()
    }

    private fun asociarElementos(view: View){
        casaLogo = view.findViewById(R.id.casaLogo)
        tareasLayout = view.findViewById(R.id.eleccionTareas)
        contenedorTareas = view.findViewById(R.id.contenedorTareas)
        fecha = view.findViewById(R.id.eleccionFecha)
        fechaElegidaTxt = view.findViewById(R.id.fechaActual)
        loadBar = tareasBinding.myProgressBar
        fechaElegidaTxt.text = fechaActual
    }

    private fun iniciarEventos(){
        casaLogo.setOnClickListener {
            generalService.mostrarObjetosClave(requireContext(), objetosList)
        }

        fecha.setOnClickListener {
            generalService.mostrarEleccionFecha(requireContext(), fechaElegidaTxt, viewModel)
        }

        val adapter = TareasAdapter(this)
        contenedorTareas.isUserInputEnabled = false
        contenedorTareas.setAdapter(adapter)

        TabLayoutMediator(
            tareasLayout, contenedorTareas
        ) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> {
                    tab.setText("Notas")
                    tab.setId(1)
                }
                1 -> {
                    tab.setText("Tareas Diarias")
                    tab.setId(2)
                }
                2 -> {
                    tab.setText("Eventos")
                    tab.setId(3)
                }
            }}.attach()

        contenedorTareas.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                tipoTarea = position + 1
                if(fragmentViewModel.recyclerViewList[position] != null){
                    recyclerView = fragmentViewModel.recyclerViewList[position]!!
                }
            }
        })
    }

    private fun registerLiveData(){
        viewModel.loading.observe(viewLifecycleOwner) {
            if(it){
                loadBar.visibility = LinearLayout.VISIBLE
            }else{
                loadBar.visibility = LinearLayout.GONE
            }
        }

        if (fragmentosIniciados[0]){
            viewModel.tareasNota.observe(viewLifecycleOwner) { listaTareas ->
                recyclerView = fragmentViewModel.recyclerViewList[0]!!
                tipoTarea = 1
                viewModel.setAdapter(recyclerView, tipoTarea)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        if (fragmentosIniciados[1]) {
            viewModel.tareasDiarias.observe(viewLifecycleOwner) { listaTareas ->
                recyclerView = fragmentViewModel.recyclerViewList[1]!!
                tipoTarea = 2
                viewModel.setAdapter(recyclerView, tipoTarea)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        if (fragmentosIniciados[2]) {
            viewModel.tareasEvento.observe(viewLifecycleOwner) { listaTareas ->
                recyclerView = fragmentViewModel.recyclerViewList[2]!!
                tipoTarea = 3
                viewModel.setAdapter(recyclerView, tipoTarea)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }
    private fun iniciarRecyclerView(recyclerViewActual: RecyclerView) {
        recyclerView = recyclerViewActual
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val ItemTouchHelper = ItemTouchHelper(simpleCallback)
        ItemTouchHelper.attachToRecyclerView(recyclerView)
        //myProgressBar = videojuegosBinding.barraCarga
    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val startPosition = viewHolder.adapterPosition
            val endPosition = target.adapterPosition

            //Collections.swap(control.getListTareas(), startPosition, endPosition)
            recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition

            when(direction){
                ItemTouchHelper.LEFT -> {
                    tareaEliminada = viewModel.getTarea(pos, tipoTarea)!!
                    var isTareaEliminada = true

                    val mensajeEliminacion = Snackbar.make(recyclerView, "${tareaEliminada.nombreTarea} ha sido eliminada", Snackbar.LENGTH_LONG).setAction("Deshacer", View.OnClickListener {
                        isTareaEliminada = false
                        viewModel.loadTareas(fechaElegidaTxt.text.toString())
                    })

                    mensajeEliminacion.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if(isTareaEliminada){
                                viewModel.eliminarTareaRepo(pos, tipoTarea)
                            }
                        }
                    })

                    mensajeEliminacion.show()

                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun cargaTarea(idFragmento: Int) {
        lateinit var recyclerViewActual: RecyclerView
        lateinit var fragmentoActual: Fragment
        when (idFragmento) {
            1 -> {
                fragmentoActual = tareasBinding.root.findViewById<ConstraintLayout?>(R.id.fragmentoNotas).findFragment<Notas>()
                recyclerViewActual = fragmentoActual.tareasBinding.myRecyclerView
                annadirElementoBt = fragmentoActual.tareasBinding.annadirElementoBt

                fragmentViewModel.recyclerViewList[0] = recyclerViewActual
                fragmentosIniciados[0] = true
            }

            2 -> {
                fragmentoActual = tareasBinding.root.findViewById<ConstraintLayout?>(R.id.fragmentoTareasDiarias).findFragment<TareasDiarias>()
                recyclerViewActual = fragmentoActual.tareasBinding.myRecyclerView
                annadirElementoBt = fragmentoActual.tareasBinding.annadirElementoBt

                fragmentViewModel.recyclerViewList[1] = recyclerViewActual
                fragmentosIniciados[1] = true
            }

            3 -> {
                fragmentoActual = tareasBinding.root.findViewById<ConstraintLayout?>(R.id.fragmentoEventos).findFragment<Eventos>()
                recyclerViewActual = fragmentoActual.tareasBinding.myRecyclerView
                annadirElementoBt = fragmentoActual.tareasBinding.annadirElementoBt

                fragmentViewModel.recyclerViewList[2] = recyclerViewActual
                fragmentosIniciados[2] = true
            }
        }

        tipoTarea = idFragmento
        iniciarRecyclerView(recyclerViewActual)

        viewModel.setAdapter(recyclerView, tipoTarea)
        viewModel.setAddButton(annadirElementoBt, tipoTarea, fechaElegidaTxt.text.toString())

        var lifeCycle: LifecycleOwner
        if(fragmentViewModel.viewCycleOwnerTareas != null){
            lifeCycle = fragmentViewModel.viewCycleOwnerTareas!!
        }else{
            lifeCycle = viewLifecycleOwner
        }

        if (viewModel.tareasNota.isInitialized) {
            viewModel.tareasNota.observe(lifeCycle) { listaTareas ->
                viewModel.setAdapter(recyclerView, tipoTarea)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        if (viewModel.tareasDiarias.isInitialized) {
            viewModel.tareasDiarias.observe(lifeCycle) { listaTareas ->
                viewModel.setAdapter(recyclerView, tipoTarea)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        if (viewModel.tareasEvento.isInitialized) {
            viewModel.tareasEvento.observe(lifeCycle) { listaTareas ->
                viewModel.setAdapter(recyclerView, tipoTarea)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    /*
    override fun onMapReady(googleMap: GoogleMap) {
        // Configura el mapa según tus necesidades
        val location = LatLng(latitud, longitud) // Latitud y longitud de ejemplo
        googleMap.addMarker(MarkerOptions().position(location).title("Marker en Ejemplo"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

    private fun mostrarMapa() {
        // Verificar si el fragmento del mapa ya está presente
        if (childFragmentManager.findFragmentById(R.id.mapContainer) == null) {
            // Si no está presente, agregar dinámicamente el fragmento del mapa
            val mapFragment = SupportMapFragment()

            childFragmentManager.beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .addToBackStack(null)
                .commit()

            mapFragment.getMapAsync(this)
        }
    }


    // Obtener la ubicación
    private fun obtenerUbicacion() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.context as Activity)
        if (ActivityCompat.checkSelfPermission(
                view.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                view.context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                    ubi.text = "$latitud --- $longitud"
                    Log.i("Location", "Location no es null")
                }

                configurarGeofence()
            }
            .addOnFailureListener { _ ->
                // No se pudo obtener la ubicación
            }
    }


    //Configurar geofence
    private fun configurarGeofence() {
        geofencingClient = LocationServices.getGeofencingClient(requireContext())

        Log.i("--- GEOFENCE ---", latitud.toString() + " - " + longitud.toString())

        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(latitud, longitud, GEOFENCE_RADIUS_IN_METERS.toFloat())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofenceRequest = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofence(geofence)
        }.build()


        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        val geofencePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)

        // Solicitar permisos de ubicación (necesario para el geofence)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    Log.i("--- GEOFENCE ---", "Geofence agregado")
                    Log.i("--- GEOFENCE ---", geofence.toString())
                    Toast.makeText(requireContext(), "Geofence Agregada: " + geofence.toString(), Toast.LENGTH_LONG).show()
                    // Geofence agregado con éxito
                }
                addOnFailureListener { e ->
                    Log.i("--- GEOFENCE ---", "Geofence no agregado", e)
                    Log.i("--- GEOFENCE ---", geofence.toString())
                    // Error al agregar el geofence
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), CODIGO_DE_PERMISO)
        }
    }*/
}