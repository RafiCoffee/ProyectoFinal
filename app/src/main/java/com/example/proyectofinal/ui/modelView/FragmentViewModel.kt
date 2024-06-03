package com.example.proyectofinal.ui.modelView

import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.interfaces.TareasListener
import com.example.proyectofinal.ui.views.fragments.Eventos
import com.example.proyectofinal.ui.views.fragments.Notas
import com.example.proyectofinal.ui.views.fragments.Tareas
import com.example.proyectofinal.ui.views.fragments.TareasDiarias

class FragmentViewModel : ViewModel() {
    var listener: TareasListener? = null
    var viewCycleOwnerTareas: LifecycleOwner? = null

    val fragmentMap = mutableMapOf<String, Fragment>()
    val recyclerViewList = mutableListOf<RecyclerView?>()

    fun addFragment(tag: String, fragment: Fragment) {
        fragmentMap[tag] = fragment
    }

    fun getFragment(tag: String): Fragment? {
        return fragmentMap[tag]
    }

    fun removeFragment(tag: String) {
        fragmentMap.remove(tag)
    }
}