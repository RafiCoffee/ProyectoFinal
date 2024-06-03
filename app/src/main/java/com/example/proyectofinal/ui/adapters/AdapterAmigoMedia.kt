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
import com.example.proyectofinal.ui.adapters.viewHolders.ViewHolderAmigoMedia

class AdapterAmigoMedia(private var listaAmigos: MutableList<Amigo>,
                        private var firebaseService: FirebaseService) : RecyclerView.Adapter<ViewHolderAmigoMedia>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAmigoMedia {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutItemAmigoActual = R.layout.media_layout
        return ViewHolderAmigoMedia(
            layoutInflater.inflate(layoutItemAmigoActual, parent, false), firebaseService)
    }

    override fun onBindViewHolder(holder: ViewHolderAmigoMedia, position: Int) {
        holder.renderize(listaAmigos[position])
    }

    override fun getItemCount(): Int = listaAmigos.size
}