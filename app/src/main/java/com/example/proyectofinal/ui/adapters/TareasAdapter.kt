package com.example.proyectofinal.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectofinal.ui.views.fragments.Eventos
import com.example.proyectofinal.ui.views.fragments.Notas
import com.example.proyectofinal.ui.views.fragments.Tareas
import com.example.proyectofinal.ui.views.fragments.TareasDiarias


class TareasAdapter(private var parentFragment: Tareas) : FragmentStateAdapter(parentFragment) {

    private val fragmentMap = HashMap<Int, Fragment>()

    override fun getItemCount(): Int {
        // Retorna la cantidad total de fragmentos que tendrás en el ViewPager
        return 3 // Reemplaza con la cantidad de fragmentos que tengas
    }

    override fun createFragment(position: Int): Fragment {
        /* Retorna el fragmento correspondiente a la posición indicada
        val tareasNotas = Notas()
        tareasNotas.setListener(parentFragment)
        val tareasDiariasFragment = TareasDiarias()
        tareasDiariasFragment.setListener(parentFragment)
        val tareasEventos = Eventos()
        tareasEventos.setListener(parentFragment)
        return when (position) {
            0 -> tareasNotas
            1 -> tareasDiariasFragment
            2 -> tareasEventos
            else -> throw IllegalArgumentException("Posición inválida")
        }*/
        return getFragment(position)
    }

    fun getFragment(position: Int): Fragment {
        return fragmentMap[position] ?: when (position) {
            0 -> Notas().apply { fragmentMap[position] = this }
            1 -> TareasDiarias().apply { fragmentMap[position] = this }
            2 -> Eventos().apply { fragmentMap[position] = this }
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}