package com.example.proyectofinal.ui.adapters.viewHolders

import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.TareaLayoutBinding
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.databinding.MediaTareaLayoutBinding

class ViewHolderTareaMedia (view: View) : RecyclerView.ViewHolder (view) {
    private var binding: MediaTareaLayoutBinding = MediaTareaLayoutBinding.bind(view)

    fun renderize(tarea: Tarea) {
        binding.tituloTarea.text = tarea.nombreTarea
        binding.horaTarea.text = tarea.hora.ifEmpty { "Todo el día" }
        binding.fechaTarea.text = tarea.fecha

        if(tarea.completada){
            binding.tareaCompletada.isChecked = true
            binding.visualCompletado.visibility = LinearLayout.VISIBLE
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remover el listener para evitar múltiples llamadas
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val anchoNombreHora = binding.tituloTarea.width + binding.horaTarea.width
                setLineaTachaWidth(anchoNombreHora)
            }
        })
    }

    private fun setLineaTachaWidth(width: Int) {
        val constraintLayout = binding.visualCompletado

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintSet.constrainWidth(binding.lineaTacha.id, width)
        constraintSet.applyTo(constraintLayout)
    }
}