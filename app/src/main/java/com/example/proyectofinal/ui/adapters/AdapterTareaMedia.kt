package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.interfaces.ItemTouchHelperAdapter
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.ui.adapters.viewHolders.ViewHolderTarea
import com.example.proyectofinal.ui.adapters.viewHolders.ViewHolderTareaMedia
import java.util.Collections

class AdapterTareaMedia(
    private var listaTareas: MutableList<Tarea>) : RecyclerView.Adapter<ViewHolderTareaMedia>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTareaMedia {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutItemTarea = R.layout.media_tarea_layout
        return ViewHolderTareaMedia(
            layoutInflater.inflate(layoutItemTarea, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderTareaMedia, position: Int) {
        holder.renderize(listaTareas[position])
    }

    override fun getItemCount(): Int = listaTareas.size
}