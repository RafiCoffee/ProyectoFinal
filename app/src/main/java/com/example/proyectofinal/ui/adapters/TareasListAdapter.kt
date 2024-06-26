package com.example.proyectofinal.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.R

class TareasListAdapter(val miContexto: Context, val datos: List<Tarea>) : ArrayAdapter<Tarea>(miContexto, 0, datos) {

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(miContexto).inflate(R.layout.list_tareas, parent, false)
        }

        val currentItem = getItem(position)

        val textViewTitulo = itemView!!.findViewById<TextView>(R.id.textTarea)
        val textViewHora = itemView.findViewById<TextView>(R.id.textHora)

        textViewTitulo.text = currentItem?.nombreTarea
        if(currentItem?.hora?.isNotEmpty()!!) {
            textViewHora.text = currentItem.hora
        }else{
            textViewHora.text = ""
        }

        return itemView
    }
}
