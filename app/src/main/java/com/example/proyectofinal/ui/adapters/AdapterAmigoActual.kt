package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.adapters.viewHolders.ViewHolderAmigo
import com.example.proyectofinal.ui.adapters.viewHolders.ViewHolderAmigoActual

class AdapterAmigoActual(private var listaAmigos: MutableList<Amigo>,
                         private var firebaseService: FirebaseService) : RecyclerView.Adapter<ViewHolderAmigoActual>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAmigoActual {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutItemAmigoActual = R.layout.actual_friend_layout
        return ViewHolderAmigoActual(
            layoutInflater.inflate(layoutItemAmigoActual, parent, false), firebaseService)
    }

    override fun onBindViewHolder(holder: ViewHolderAmigoActual, position: Int) {
        holder.renderize(listaAmigos[position])
    }

    override fun getItemCount(): Int = listaAmigos.size
}