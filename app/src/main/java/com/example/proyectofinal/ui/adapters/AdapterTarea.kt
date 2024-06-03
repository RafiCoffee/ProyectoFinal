package com.example.proyectofinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.interfaces.ItemTouchHelperAdapter
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.ui.adapters.viewHolders.ViewHolderTarea
import java.util.Collections

class AdapterTarea(
    private var listaTareas: MutableList<Tarea>,
    private var completeTaskOnClick: (Int, Boolean, Int) -> Unit,
    private var editOnClick: (Int) -> Unit) : RecyclerView.Adapter<ViewHolderTarea>(),
    ItemTouchHelperAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTarea {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutItemTarea = R.layout.tarea_layout
        return ViewHolderTarea(
            layoutInflater.inflate(layoutItemTarea, parent, false),
            completeTaskOnClick, editOnClick)
    }

    override fun onBindViewHolder(holder: ViewHolderTarea, position: Int) {
        holder.renderize(listaTareas[position])
        holder.setOnClickListener(position, listaTareas[position].tipoTarea)
    }

    override fun getItemCount(): Int = listaTareas.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        // Realiza el cambio en tu lista de datos
        Collections.swap(listaTareas, fromPosition, toPosition)

        // Notifica al adaptador sobre el cambio
        notifyItemMoved(fromPosition, toPosition)
    }
}