package com.example.proyectofinal.ui.views.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.ui.adapters.TareasListAdapter
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.R
import com.example.proyectofinal.data.interfaces.TareasListener
import com.example.proyectofinal.databinding.FragmentEventosBinding
import com.example.proyectofinal.databinding.FragmentNotasBinding
import com.example.proyectofinal.ui.modelView.FragmentViewModel
import java.util.Calendar

class Eventos constructor() : Fragment() {
    private lateinit var listener: TareasListener

    lateinit var tareasBinding: FragmentEventosBinding
    private lateinit var myRecyclerView: RecyclerView

    private lateinit var viewModel: FragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[FragmentViewModel::class.java]
        viewModel.listener.let {
            if (it != null) {
                listener = it
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        tareasBinding = FragmentEventosBinding.inflate(inflater, container, false)
        myRecyclerView = tareasBinding.root.findViewById(R.id.my_recycler_view)

        return tareasBinding.root
    }

    override fun onStart() {
        super.onStart()

        listener.cargaTarea(3)
    }
}