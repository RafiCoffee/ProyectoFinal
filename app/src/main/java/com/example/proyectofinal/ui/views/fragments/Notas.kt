package com.example.proyectofinal.ui.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.Controllers.TareasController
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentNotasBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class Notas : Fragment() {
    private lateinit var annadirElementoBt: ImageView

    private lateinit var control: TareasController
    lateinit var tareasBinding: FragmentNotasBinding
    private lateinit var myRecyclerView: RecyclerView

    private lateinit var notaEliminada: Tarea

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        tareasBinding = FragmentNotasBinding.inflate(inflater, container, false)

        return tareasBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //listaNotas = view.findViewById(R.id.listaTareas)
        annadirElementoBt = view.findViewById(R.id.annadirElementoBt)

        //notasAdapter = TareasListAdapter(requireContext(), notasList)
        //listaNotas.adapter = notasAdapter

        annadirElementoBt.setOnClickListener {
            //mostrarAnnadirTarea()
        }

        iniciar()
    }

    private fun iniciar() {
        iniciarRecyclerView()
        control = TareasController(requireContext(), tareasBinding)
        control.setAdapter()

        myRecyclerView = tareasBinding.myRecyclerView
        control.setRecyclerView(myRecyclerView)

        val addButton = tareasBinding.root.findViewById<ImageView>(R.id.annadirElementoBt)
        control.setAddButton(addButton)
    }

    private fun iniciarRecyclerView() {
        myRecyclerView = tareasBinding.myRecyclerView
        myRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val ItemTouchHelper = ItemTouchHelper(simpleCallback)
        ItemTouchHelper.attachToRecyclerView(myRecyclerView)
    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            var startPosition = viewHolder.adapterPosition
            var endPosition = target.adapterPosition

            Collections.swap(control.getListTareas(), startPosition, endPosition)
            myRecyclerView.adapter?.notifyItemMoved(startPosition, endPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            var pos = viewHolder.adapterPosition

            when(direction){
                ItemTouchHelper.LEFT -> {
                    notaEliminada = control.getListTareas().get(pos)
                    control.getListTareas().removeAt(pos)
                    myRecyclerView.adapter?.notifyItemRemoved(pos)

                    Snackbar.make(myRecyclerView, "${notaEliminada.tarea} ha sido eliminada", Snackbar.LENGTH_LONG).setAction("Deshacer", View.OnClickListener {
                        control.getListTareas().add(pos, notaEliminada)
                        myRecyclerView.adapter?.notifyItemInserted(pos)
                    }).show()

                }

                ItemTouchHelper.RIGHT -> {

                }
            }
        }

    }
}