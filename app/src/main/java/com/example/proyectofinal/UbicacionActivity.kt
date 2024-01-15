package com.example.proyectofinal

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class UbicacionActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient
    //private lateinit var ubi: TextView
    private lateinit var ubiBt: Button

    private var latitud = 0.0
    private var longitud = 0.0

    private companion object {
        private const val CODIGO_DE_PERMISO = 42
        private const val GEOFENCE_RADIUS_IN_METERS = 2.0  // Radio en metros
        private const val GEOFENCE_ID = "MiGeofence"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        //ubi = findViewById(R.id.coordenadas)
        ubiBt = findViewById(R.id.ubicacionBt)

        // Solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion()
            configurarGeofence()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), CODIGO_DE_PERMISO)
        }

        ubiBt.setOnClickListener {
            mostrarMapa()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Configura el mapa según tus necesidades
        val location = LatLng(latitud, longitud) // Latitud y longitud de ejemplo
        googleMap.addMarker(MarkerOptions().position(location).title("Marker en Ejemplo"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

    private fun mostrarMapa() {
        // Verificar si el fragmento del mapa ya está presente
        if (supportFragmentManager.findFragmentById(R.id.mapContainer) == null) {
            // Si no está presente, agregar dinámicamente el fragmento del mapa
            val mapFragment = SupportMapFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .addToBackStack(null)
                .commit()

            mapFragment.getMapAsync(this)
        }
    }

    // Obtener la ubicación
    private fun obtenerUbicacion() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
                    //ubi.text = "$latitud --- $longitud"
                    Log.i("Location", "Location no es null")
                }
            }
            .addOnFailureListener { _ ->
                // No se pudo obtener la ubicación
            }
    }

    // Configurar geofence
    private fun configurarGeofence() {
        geofencingClient = LocationServices.getGeofencingClient(this)

        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(latitud, longitud, GEOFENCE_RADIUS_IN_METERS.toFloat())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Solicitar permisos de ubicación (necesario para el geofence)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent)?.run {
                addOnSuccessListener {
                    // Geofence agregado con éxito
                }
                addOnFailureListener {
                    // Error al agregar el geofence
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), CODIGO_DE_PERMISO)
        }
    }
}