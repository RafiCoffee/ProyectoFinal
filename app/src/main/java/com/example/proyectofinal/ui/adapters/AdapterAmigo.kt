package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.adapters.viewHolders.ViewHolderAmigo

class AdapterAmigo(private var listaAmigos: MutableList<Amigo>,
                   private var firebaseService: FirebaseService,
                   private var userService: UserService) : RecyclerView.Adapter<ViewHolderAmigo>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAmigo {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutItemAmigo = R.layout.friend_layout
        return ViewHolderAmigo(
            layoutInflater.inflate(layoutItemAmigo, parent, false), firebaseService, userService)
    }

    override fun onBindViewHolder(holder: ViewHolderAmigo, position: Int) {
        holder.renderize(listaAmigos[position])
    }

    override fun getItemCount(): Int = listaAmigos.size
}