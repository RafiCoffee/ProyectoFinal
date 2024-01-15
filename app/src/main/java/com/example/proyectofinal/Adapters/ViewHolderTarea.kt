package com.example.proyectofinal.Adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.TareaLayoutBinding
import com.example.proyectofinal.Modelos.Tarea

class ViewHolderTarea (view: View) : RecyclerView.ViewHolder (view) {
    private var binding: TareaLayoutBinding

    init {
        binding = TareaLayoutBinding.bind(view)
    }

    fun renderize(tarea: Tarea) {
        binding.tituloTarea.text = tarea.tarea
        binding.descripcionTarea.text = tarea.descripcion
        if(tarea.horas == null || tarea.minutos == null){
            binding.hora.text == ""
        }else{
            val horas = tarea.horas.toString().padStart(2, '0')
            val minutos = tarea.minutos.toString().padStart(2, '0')
            binding.hora.text = "$horas:$minutos"
        }
    }
}