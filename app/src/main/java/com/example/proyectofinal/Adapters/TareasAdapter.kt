package com.example.proyectofinal.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectofinal.Fragmentos.Eventos
import com.example.proyectofinal.Fragmentos.Notas
import com.example.proyectofinal.Fragmentos.TareasDiarias


class TareasAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        // Retorna la cantidad total de fragmentos que tendrás en el ViewPager
        return 3 // Reemplaza con la cantidad de fragmentos que tengas
    }

    override fun createFragment(position: Int): Fragment {
        // Retorna el fragmento correspondiente a la posición indicada
        return when (position) {
            0 -> Notas()
            1 -> TareasDiarias()
            2 -> Eventos()
            else -> throw IllegalArgumentException("Posición inválida")
        }
    }
}