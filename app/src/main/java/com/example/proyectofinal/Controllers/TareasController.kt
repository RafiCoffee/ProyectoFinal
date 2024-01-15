package com.example.proyectofinal.Controllers

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.Adapters.AdapterTarea
//import com.example.proyectofinal.dao.DaoTareas
import com.example.proyectofinal.databinding.FragmentNotasBinding
import com.example.proyectofinal.Dialogos.DialogCallback
import com.example.proyectofinal.Dialogos.DialogCallbackHora
import com.example.proyectofinal.Modelos.Tarea
import java.util.Calendar

class TareasController(val contexto: Context, val tareasBinding: FragmentNotasBinding) {
    val hoy = Calendar.getInstance()

    private lateinit var listTareas: MutableList<Tarea>
    private lateinit var adapterTareas: AdapterTarea
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: ImageView
    private lateinit var horaElegida: TextView

    init {
        initData()
    }

    private fun initData() {
        listTareas = mutableListOf()
    }

    fun getListTareas(): MutableList<Tarea> {
        return listTareas
    }

    fun setAdapter() {
        adapterTareas = AdapterTarea(listTareas)
        tareasBinding.myRecyclerView.adapter = adapterTareas
    }

    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun setAddButton(addButton: ImageView) {
        this.addButton = addButton
        this.addButton.setOnClickListener { crearTarea() }
    }

    private fun crearTarea() {
        mostrarAnnadirTarea(object : DialogCallback {
            override fun onDialogResult(nuevaTarea: Array<String>, isCanceled: Boolean) {
                if (!isCanceled) {
                    val horaString = nuevaTarea[2]
                    val minutoString = nuevaTarea[3]

                    val horas = if (horaString.isNotEmpty()) horaString.toInt() else null
                    val minutos = if (minutoString.isNotEmpty()) minutoString.toInt() else null

                    val TareaCreada = Tarea(nuevaTarea[0], nuevaTarea[1], horas, minutos, nuevaTarea[4])
                    listTareas.add(TareaCreada)
                    Toast.makeText(contexto, TareaCreada.tarea + " creado", Toast.LENGTH_LONG).show()

                    val newPos = (listTareas.size - 1)
                    adapterTareas.notifyItemInserted(newPos)

                    recyclerView.smoothScrollToPosition(newPos)
                }
            }
        })
    }

    private fun elegirHora() {
        mostrarEleccionHora(object : DialogCallbackHora {
            override fun onDialogResult(hora: String, isCanceled: Boolean) {
                if (!isCanceled) {
                    horaElegida.text = hora
                }
            }
        })
    }

    private fun mostrarAnnadirTarea(callback: DialogCallback) {
        var nuevaTarea = Array(5) {""}

        val builder = AlertDialog.Builder(contexto)
        builder.setTitle("Añade Una Nota")

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
        elegirHora.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 100
            marginEnd = 50
        }
        horaElegida = TextView(contexto)
        val horas = hoy.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val minutos = hoy.get(Calendar.MINUTE).toString().padStart(2, '0')
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
            elegirHora()
        }

        builder.setView(contenedorPadre)

        builder.setPositiveButton("Aceptar") { _, _ ->
            val fechaString = "${hoy.get(Calendar.DAY_OF_MONTH)}/${hoy.get(Calendar.MONTH) + 1}/${hoy.get(Calendar.YEAR)}"
            if (tareaConHora.checkedRadioButtonId == conHora.id) {
                nuevaTarea[0] = tituloTarea.text.toString()
                nuevaTarea[1] = descripcionTarea.text.toString()
                nuevaTarea[2] = horaElegida.text.toString().substring(0,2).padStart(2,'0')
                nuevaTarea[3] = horaElegida.text.toString().substring(3,5).padStart(2,'0')
                nuevaTarea[4] = fechaString
            } else {
                nuevaTarea[0] = tituloTarea.text.toString()
                nuevaTarea[1] = descripcionTarea.text.toString()
                nuevaTarea[2] = ""
                nuevaTarea[3] = ""
                nuevaTarea[4] = fechaString
            }

            callback.onDialogResult(nuevaTarea, false)
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            callback.onDialogResult(nuevaTarea, false)
            dialog.cancel()
        }

        builder.show()
    }

    fun mostrarEleccionHora(callbackHora: DialogCallbackHora) {
        val builder = AlertDialog.Builder(contexto)

        val contenedorPadre = LinearLayout(contexto)

        val reloj = TimePicker(contexto)
        reloj.setIs24HourView(true)
        contenedorPadre.addView(reloj)

        builder.setView(contenedorPadre)

        builder.setPositiveButton("Aceptar") { _, _ ->
            val horas = reloj.hour.toString().padStart(2, '0')
            val minutos = reloj.minute.toString().padStart(2, '0')
            val horaCompleta = "$horas:$minutos"

            callbackHora.onDialogResult(horaCompleta, false)
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            callbackHora.onDialogResult("", false)
            dialog.cancel()
        }

        builder.show()
    }
}