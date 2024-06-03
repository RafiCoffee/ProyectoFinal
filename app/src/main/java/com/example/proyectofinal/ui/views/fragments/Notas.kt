package com.example.proyectofinal.ui.views.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.interfaces.TareasListener
import com.example.proyectofinal.databinding.FragmentNotasBinding
import com.example.proyectofinal.ui.modelView.FragmentViewModel

class Notas constructor() : Fragment() {
    private lateinit var listener: TareasListener

    lateinit var tareasBinding: FragmentNotasBinding
    private lateinit var myRecyclerView: RecyclerView

    private lateinit var viewModel: FragmentViewModel

    fun setListener(listener: TareasListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(FragmentViewModel::class.java)
        viewModel.listener.let {
            if (it != null) {
                listener = it
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        tareasBinding = FragmentNotasBinding.inflate(inflater, container, false)
        myRecyclerView = tareasBinding.root.findViewById(R.id.my_recycler_view)

        return tareasBinding.root
    }

    override fun onStart() {
        super.onStart()

        listener.cargaTarea(1)
    }

}