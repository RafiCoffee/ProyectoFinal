package com.example.proyectofinal.Fragmentos

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
import com.example.proyectofinal.Adapters.TareasListAdapter
import com.example.proyectofinal.Modelos.Tarea
import com.example.proyectofinal.R
import java.util.Calendar

class Eventos : Fragment() {
    val hoy = Calendar.getInstance()

    private lateinit var eventosAdapter: ArrayAdapter<Tarea>
    private var eventosList = ArrayList<Tarea>()

    private lateinit var listaeventos: ListView
    private lateinit var annadirElementoBt: ImageView
    private lateinit var horaElegida: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eventos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaeventos = view.findViewById(R.id.listaTareas)
        annadirElementoBt = view.findViewById(R.id.annadirElementoBt)

        eventosAdapter = TareasListAdapter(requireContext(), eventosList)
        listaeventos.adapter = eventosAdapter

        annadirElementoBt.setOnClickListener {
            mostrarAnnadirTarea()
        }
    }

    private fun mostrarAnnadirTarea(){
        val contexto = requireContext()

        val builder = AlertDialog.Builder(contexto)
        builder.setTitle("Añade Un Evento")

        val contenedorPadre = LinearLayout(contexto)
        contenedorPadre.orientation = LinearLayout.VERTICAL

        val tituloTareaTxt = TextView(contexto)
        tituloTareaTxt.text = "Título:"
        contenedorPadre.addView(tituloTareaTxt)
        val tituloTarea = EditText(contexto)
        tituloTarea.hint = "Introduce el título"
        contenedorPadre.addView(tituloTarea)

        val descripcionTareaTxt = TextView(contexto)
        descripcionTareaTxt.text = "Descripción:"
        contenedorPadre.addView(descripcionTareaTxt)
        val descripcionTarea = EditText(contexto)
        descripcionTarea.hint = "Introduce una breve descripción, si es necesario"
        contenedorPadre.addView(descripcionTarea)

        val tareaConHoraTxt = TextView(contexto)
        tareaConHoraTxt.text = "Hora:"
        contenedorPadre.addView(tareaConHoraTxt)
        val tareaConHora = RadioGroup(contexto)
        tareaConHora.orientation = LinearLayout.HORIZONTAL

        val listaRadioBt = mutableListOf<RadioButton>()
        val conHora = RadioButton(contexto)
        conHora.id = View.generateViewId()
        conHora.text = "Si"
        tareaConHora.addView(conHora)
        listaRadioBt.add(conHora)
        val sinHora = RadioButton(contexto)
        sinHora.id = View.generateViewId()
        sinHora.text = "No"
        tareaConHora.addView(sinHora)
        listaRadioBt.add(sinHora)

        val elegirHora = Button(contexto)
        elegirHora.text = "Escoger Hora"
        elegirHora.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginStart = 100
            marginEnd = 50
        }
        horaElegida = TextView(contexto)
        val horas = hoy.get(Calendar.HOUR).toString().padEnd(2, '0')
        val minutos = hoy.get(Calendar.MINUTE).toString().padEnd(2, '0')
        horaElegida.text = "$horas:$minutos"
        horaElegida.textSize = 25f
        contenedorPadre.addView(tareaConHora)

        tareaConHora.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                conHora.id -> {
                    tareaConHora.addView(elegirHora)
                    tareaConHora.addView(horaElegida)
                }
                sinHora.id -> {
                    tareaConHora.removeView(elegirHora)
                    tareaConHora.removeView(horaElegida)
                }
            }
        }

        elegirHora.setOnClickListener {
            mostrarEleccionHora()
        }

        builder.setView(contenedorPadre)

        builder.setPositiveButton("Aceptar"){ _, _ ->
            var nuevaTarea: Tarea
            val fechaString = "${hoy.get(Calendar.DAY_OF_MONTH)}/${hoy.get(Calendar.MONTH) + 1}/${hoy.get(Calendar.YEAR)}"
            if (tareaConHora.checkedRadioButtonId == 0){
                nuevaTarea = Tarea(tituloTarea.text.toString(), descripcionTarea.text.toString(), Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), fechaString)
            }else{
                nuevaTarea = Tarea(tituloTarea.text.toString(), descripcionTarea.text.toString(), null, null, fechaString)
            }
            eventosList.add(nuevaTarea)
            eventosAdapter.notifyDataSetChanged()
        }

        builder.setNegativeButton("Cancelar"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    fun mostrarEleccionHora(){
        val contexto = requireContext()

        val builder = AlertDialog.Builder(contexto)

        val contenedorPadre = LinearLayout(contexto)

        val reloj = TimePicker(contexto)
        contenedorPadre.addView(reloj)

        builder.setView(contenedorPadre)

        builder.setPositiveButton("Aceptar"){ _, _ ->
            val horas = reloj.hour.toString().padEnd(2, '0')
            val minutos = reloj.minute.toString().padEnd(2, '0')
            horaElegida.text = "$horas:$minutos"
        }

        builder.setNegativeButton("Cancelar"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}