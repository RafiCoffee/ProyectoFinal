package com.example.proyectofinal.ui.views.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.proyectofinal.ui.adapters.TareasAdapter
import com.example.proyectofinal.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Calendar


class Tareas : Fragment()/*, OnMapReadyCallback*/ {
    //private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //private lateinit var geofencingClient: GeofencingClient
    private lateinit var objetosClaveAdapter: ArrayAdapter<String>

    private var objetosList = ArrayList<String>()
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

    private lateinit var fragmentoNotas: Notas

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tareas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ubi = view.findViewById(R.id.coordenadas)
        //ubiBt = view.findViewById(R.id.ubicacionBt)
        casaLogo = view.findViewById(R.id.casaLogo)
        tareasLayout = view.findViewById(R.id.eleccionTareas)
        contenedorTareas = view.findViewById(R.id.contenedorTareas)
        fecha = view.findViewById(R.id.eleccionFecha)
        fechaElegidaTxt = view.findViewById(R.id.fechaActual)
        fechaElegidaTxt.text = fechaActual

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

        casaLogo.setOnClickListener {
            mostrarObjetosClave()
        }

        fecha.setOnClickListener {
            mostrarEleccionFecha()
        }

        val fragments: MutableList<Fragment> = ArrayList()
        fragments.add(Notas())
        fragments.add(TareasDiarias())
        fragments.add(Eventos())

        val adapter = TareasAdapter(requireActivity())
        contenedorTareas.setAdapter(adapter)

        TabLayoutMediator(
            tareasLayout, contenedorTareas
        ) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.setText("Notas")
                1 -> tab.setText("Tareas Diarias")
                2 -> tab.setText("Eventos")
            }}.attach()
    }

    private fun mostrarObjetosClave(){
        val objetosListAux = ArrayList(objetosList)
        val contexto = requireContext()

        val builder = AlertDialog.Builder(contexto)
        builder.setTitle("Introducir Objetos Clave")

        val contenedorPadre = LinearLayout(contexto)

        val parametrosContenedoresHijos =  LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        parametrosContenedoresHijos.weight = 1f

        val contenedorIzq = LinearLayout(contexto)
        contenedorIzq.orientation = LinearLayout.VERTICAL
        contenedorIzq.gravity = Gravity.CENTER
        contenedorIzq.layoutParams = parametrosContenedoresHijos
        contenedorIzq.setPadding(10,5,10,5)

        val contenedorDer = LinearLayout(contexto)
        contenedorDer.orientation = LinearLayout.VERTICAL
        contenedorDer.gravity = Gravity.CENTER
        contenedorDer.layoutParams = parametrosContenedoresHijos
        contenedorDer.setPadding(10,5,10,5)

        val objetoClaveTxt = TextView(contexto)
        objetoClaveTxt.text = "Objeto:"
        contenedorIzq.addView(objetoClaveTxt)
        val objetoClave = EditText(contexto)
        objetoClave.hint = "Introduce un objeto"
        contenedorIzq.addView(objetoClave)

        val introducirBt = Button(contexto)
        introducirBt.text = "Introducir"
        contenedorIzq.addView(introducirBt)

        val eliminarBt = Button(contexto)
        eliminarBt.text = "Eliminar"
        contenedorIzq.addView(eliminarBt)

        val contenedorLista = LinearLayout(contexto)
        contenedorLista.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 400)
        val listaObjetos = ListView(contexto)
        listaObjetos.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        objetosClaveAdapter = ArrayAdapter(contexto, R.layout.list_objetos_clave, objetosListAux)
        listaObjetos.adapter = objetosClaveAdapter
        contenedorLista.addView(listaObjetos)
        contenedorDer.addView(contenedorLista)

        contenedorPadre.addView(contenedorIzq)
        contenedorPadre.addView(contenedorDer)

        builder.setView(contenedorPadre)

        listaObjetos.setOnItemClickListener { _, _, position, _ ->
            objetoClave.setText(objetosListAux[position])
        }

        introducirBt.setOnClickListener {
            objetosListAux.add(objetoClave.text.toString())
            objetosClaveAdapter.notifyDataSetChanged()
            objetoClave.text.clear()
        }

        eliminarBt.setOnClickListener {
            objetosListAux.remove(objetoClave.text.toString())
            objetosClaveAdapter.notifyDataSetChanged()
            objetoClave.text.clear()
        }

        builder.setPositiveButton("Aceptar"){ _, _ ->
            objetosList = objetosListAux
        }

        builder.setNegativeButton("Cancelar"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun mostrarEleccionFecha(){
        var fechaSeleccionada = ""
        val contexto = requireContext()

        val builder = AlertDialog.Builder(contexto)

        val contenedorPadre = LinearLayout(contexto)

        val calendario = DatePicker(contexto)
        calendario.spinnersShown = true
        calendario.calendarViewShown = false
        val fechaActual = Calendar.getInstance()
        calendario.minDate = fechaActual.timeInMillis
        calendario.init(fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            val realMonth = month + 1
            fechaSeleccionada = "$day/$realMonth/$year"
        }
        contenedorPadre.addView(calendario)

        builder.setView(contenedorPadre)

        builder.setPositiveButton("Aceptar"){ _, _ ->
            fechaElegidaTxt.text = fechaSeleccionada
        }

        builder.setNegativeButton("Cancelar"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()
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