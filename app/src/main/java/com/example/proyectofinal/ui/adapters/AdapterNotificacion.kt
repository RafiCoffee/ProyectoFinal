package com.example.proyectofinal.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Notificacion
import com.example.proyectofinal.data.repositories.NotificationRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.NotificationService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.adapters.viewHolders.ViewHolderNotificacion

@RequiresApi(Build.VERSION_CODES.Q)
class AdapterNotificacion(private var listaNotificacion: MutableList<Notificacion>,
                          private var firebaseService: FirebaseService,
                          private var userService: UserService,
                          private var tareasService: TareasService,
                          private var notificationRepository: NotificationRepository) : RecyclerView.Adapter<ViewHolderNotificacion>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderNotificacion {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutItemNotificacion = R.layout.notification_layout
        return ViewHolderNotificacion(
            layoutInflater.inflate(layoutItemNotificacion, parent, false), firebaseService, userService, tareasService, notificationRepository)
    }
    override fun onBindViewHolder(holder: ViewHolderNotificacion, position: Int) {
        holder.renderize(listaNotificacion[position])
    }

    override fun getItemCount(): Int = listaNotificacion.size
}