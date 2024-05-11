package com.example.proyectofinal.ui.views

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

    class GeofenceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("--- GEOFENCE ---", "Recibido Geofence")

            Toast.makeText(context, "Has entrado al Broadcast ", Toast.LENGTH_LONG).show()

            Log.i("--- GEOFENCE ---", intent.toString())
            Log.i("--- GEOFENCE ---", intent.extras.toString())
            val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) } ?: return
            Log.i("--- GEOFENCE ---", geofencingEvent.toString())

            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e("--- GEOFENCE ERROR ---", errorMessage)
                return
            }

            // Get the transition type.
            val geofenceTransition = geofencingEvent?.geofenceTransition
            Log.i("--- GEOFENCE ---", geofenceTransition.toString())

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                val triggeringGeofences = geofencingEvent.triggeringGeofences

                /* Get the transition details as a String.
                val geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
                )*/

                // Send notification and log the transition details.
                //sendNotification(geofenceTransitionDetails)
                Log.i("--- GEOFENCE ---", geofenceTransition.toString())
            } else {
                // Log the error.
                Log.e("--- GEOFENCE ERROR ---", "Error")
            }
        }
    }