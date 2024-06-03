package com.example.proyectofinal.ui.dialogues

import android.animation.ObjectAnimator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.text.SpannableStringBuilder
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.callbacks.DialogCallbackHora
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.ui.modelView.TareasViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@SuppressLint("UseSwitchCompatOrMaterialCode")
class SaveDialogue(myRecyclerView: RecyclerView, pantallasaveTarea: LinearLayout, tareasService: TareasService) {
    private val contexto = myRecyclerView.context
    private val pantallasaveTarea = pantallasaveTarea
    private lateinit var view : View
    private var fechaActual = "${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}/${Calendar.getInstance().get(Calendar.MONTH) + 1}/${Calendar.getInstance().get(Calendar.YEAR)}"

    private var eligeHora = false
    private var eligeRecordatorio = false

    private lateinit var contenedorPadre: LinearLayout
    private lateinit var atrasBt: ImageView
    private lateinit var saveTarea: TextView
    private lateinit var nombreTarea : EditText
    private lateinit var contenedorEleccionFecha : ConstraintLayout
    private lateinit var contenedorEleccionHora : ConstraintLayout
    private lateinit var contenedorEleccionRecordatorio : ConstraintLayout
    private lateinit var fechaElegidaTxt : TextView
    private lateinit var horaElegidaTxt : TextView
    private lateinit var recordatorioElegidoTxt : TextView

    private var recordatorioElegido : String = "0"

    private lateinit var switchHora: Switch
    private lateinit var eleccionHora: TimePicker

    private lateinit var switchRecordatorio: Switch
    private lateinit var contenedorEleccionesRecordatorio: LinearLayout
    private lateinit var eleccionRecordatorioY: Spinner
    private lateinit var eleccionRecordatorioZ: Spinner
    private lateinit var indicadorRecordatorioTxt: TextView

    private lateinit var eleccionFecha: DatePicker

    val tareasService = tareasService
    @SuppressLint("ClickableViewAccessibility")
    fun mostrarDialogoSaveTarea(viewModel: TareasViewModel, tipoTarea: Int, fechaElegida: String, pos: Int?, tareaEditable: Tarea?){
        view = pantallasaveTarea
        pantallasaveTarea.visibility = LinearLayout.VISIBLE

        asociarElementosPantallaTarea()
        contenedorPadre.visibility = LinearLayout.VISIBLE
        fechaActual = fechaElegida

        view.setOnTouchListener { _, _ -> true }

        val alturaPantalla = getScreenHeight(view.context).toFloat()/2
        view.animate().translationY(30f).setDuration(750).start()

        fechaElegidaTxt.text = fechaActual
        horaElegidaTxt.text = view.context.getString(R.string.todo_el_dia)
        recordatorioElegidoTxt.text = view.context.getString(R.string.sin_recordatorios)

        contenedorEleccionFecha.setOnClickListener { mostrarEleccionFecha() }
        contenedorEleccionHora.setOnClickListener { elegirHora() }
        contenedorEleccionRecordatorio.setOnClickListener { mostrarEleccionRecordatorio() }

        val animator = ObjectAnimator.ofFloat(view, "translationY", (alturaPantalla * 2))
        animator.duration = 750

        if(tareaEditable == null){
            saveTarea.text = "Crear Tarea"
        }else{
            saveTarea.text = "Editar Tarea"
            nombreTarea.text = SpannableStringBuilder(tareaEditable.nombreTarea)
            horaElegidaTxt.text = tareaEditable.hora.ifEmpty { "Todo el día" }
            val recordatorioCode = tareaEditable.recordatorio.split("-")
            var frecuenciaRecordatorio = ""
            when(recordatorioCode[1]){
                "1" -> frecuenciaRecordatorio = "día"
                "2" -> frecuenciaRecordatorio = "semana"
                "3" -> frecuenciaRecordatorio = "mes"
                "4" -> frecuenciaRecordatorio = "año"
            }
            recordatorioElegidoTxt.text = if(recordatorioCode[0].equals("1")) "Cada ${frecuenciaRecordatorio}" else "Cada ${recordatorioCode[2]} ${if(recordatorioCode[1].equals("3")) "mese" else frecuenciaRecordatorio}s"
        }

        saveTarea.setOnClickListener {
            val nuevaTarea = saveTarea(tipoTarea, fechaActual, recordatorioElegido)
            if(tareaEditable != null) nuevaTarea?.fechaCompletada = "${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}/${Calendar.getInstance().get(Calendar.MONTH) + 1}/${Calendar.getInstance().get(Calendar.YEAR)}"

            if(nuevaTarea != null){
                viewModel.viewModelScope.launch {
                    if(tareaEditable == null) viewModel.agregarTareaRepo(nuevaTarea) else viewModel.editarTareaRepo(pos!!, nuevaTarea)
                    animator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            vaciarPantalla()
                            contenedorPadre.visibility = LinearLayout.GONE
                            pantallasaveTarea.visibility = LinearLayout.GONE
                        }
                    })
                    animator.start()
                }
            }else{
                Toast.makeText(contexto, "Existe algun campo vacio", Toast.LENGTH_SHORT).show()
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        vaciarPantalla()
                        contenedorPadre.visibility = LinearLayout.GONE
                        pantallasaveTarea.visibility = LinearLayout.GONE
                    }
                })
                animator.start()
            }
        }

        atrasBt.setOnClickListener {
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    vaciarPantalla()
                    contenedorPadre.visibility = LinearLayout.GONE
                    pantallasaveTarea.visibility = LinearLayout.GONE
                }
            })

            animator.start()
        }
    }

    fun asociarElementosPantallaTarea(){
        contenedorPadre = view.findViewById(R.id.pantallaSaveTarea)
        atrasBt = view.findViewById(R.id.atrasBt)
        saveTarea = view.findViewById(R.id.saveTareaBt)
        nombreTarea = view.findViewById(R.id.nombreTarea)
        contenedorEleccionFecha = view.findViewById(R.id.contenedorFecha)
        contenedorEleccionHora = view.findViewById(R.id.contenedorHora)
        contenedorEleccionRecordatorio = view.findViewById(R.id.contenedorRecordatorio)
        fechaElegidaTxt = view.findViewById(R.id.fechaElegidaTxt)
        horaElegidaTxt = view.findViewById(R.id.horaElegidaTxt)
        recordatorioElegidoTxt = view.findViewById(R.id.recordatorioElegidoTxt)
    }

    fun asociarElementosPantallaHora(){
        switchHora = view.findViewById(R.id.switchHora)
        eleccionHora = view.findViewById(R.id.eleccionHora)
    }

    fun asociarElementosPantallaFecha(){
        eleccionFecha = view.findViewById(R.id.eleccionFecha)
    }

    fun asociarElementosPantallaRecordatorio(){
        switchRecordatorio = view.findViewById(R.id.switchRecordatorio)
        contenedorEleccionesRecordatorio = view.findViewById(R.id.contenedorElecciones)
        eleccionRecordatorioY = view.findViewById(R.id.eleccionRecordatorioY)
        eleccionRecordatorioZ = view.findViewById(R.id.eleccionRecordatorioZ)
        indicadorRecordatorioTxt = view.findViewById(R.id.indicadorRecordatorioTxt)

        ArrayAdapter.createFromResource(contexto, R.array.recordatorioY_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                eleccionRecordatorioY.adapter = adapter
            }
        ArrayAdapter.createFromResource(contexto, R.array.recordatorioZ_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                eleccionRecordatorioZ.adapter = adapter
            }
    }

    fun saveTarea(tipoTarea: Int, fechaElegida: String, recordatorioElegido: String) : Tarea? {
        val nuevaTarea = Array(6){""}
            nuevaTarea[0] = tareasService.generateTareaId()!!
            nuevaTarea[1] = nombreTarea.text.toString()
            nuevaTarea[2] = if(eligeHora) horaElegidaTxt.text.toString() else ""
            nuevaTarea[3] = fechaElegida
            nuevaTarea[4] = if(eligeRecordatorio) recordatorioElegido else "0-0-0"
            nuevaTarea[5] = tipoTarea.toString()

        /**Funcionamiento Recordatorio
         * Patron -> x-y-z
         * x puede ser 0 o 1 -> 0(No se notifica), 1(Se notifica)
         * Posibles y -> 1(Diario), 2(Semanal), 3(Mensual), 4(Anual)
         * z significa frecuencia
         * Ejemplo:
         * 1-2-3
         * Esta tarea se recordara cada 3 semanas
         */

        if(nuevaTarea[0].isNotEmpty() && nuevaTarea[1].isNotEmpty() && nuevaTarea[3].isNotEmpty() && nuevaTarea[5].isNotEmpty()){
            val tareaCreada = Tarea(nuevaTarea[0], nuevaTarea[1], nuevaTarea[2], nuevaTarea[3], nuevaTarea[4], tipoTarea, false, "")
            return tareaCreada
        }else{
            Toast.makeText(contexto, "Error al crear la tarea", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun elegirHora() {
        mostrarEleccionHora(object : DialogCallbackHora {
            override fun onDialogResult(hora: String, isCanceled: Boolean) {
                if (!isCanceled) {
                    horaElegidaTxt.text = hora
                }
            }
        })
    }

    fun mostrarEleccionHora(callbackHora: DialogCallbackHora) {
        val inflater = LayoutInflater.from(contexto)
        view = inflater.inflate(R.layout.eleccion_hora, null)

        asociarElementosPantallaHora()

        eleccionHora.visibility = LinearLayout.INVISIBLE

        switchHora.setOnClickListener {
            eleccionHora.visibility = if(eleccionHora.visibility == LinearLayout.INVISIBLE) LinearLayout.VISIBLE else LinearLayout.INVISIBLE
            eligeHora = if(eleccionHora.visibility == LinearLayout.VISIBLE) true else false
        }

        val builder = AlertDialog.Builder(contexto)
        builder.setView(view)

        builder.setPositiveButton("Aceptar") { _, _ ->
            val horas = eleccionHora.hour.toString().padStart(2, '0')
            val minutos = eleccionHora.minute.toString().padStart(2, '0')
            val horaCompleta = "$horas:$minutos"

            callbackHora.onDialogResult(horaCompleta, false)
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            callbackHora.onDialogResult("", false)
            dialog.cancel()
        }

        val dialogo = builder.create()

        dialogo.show()
        dialogo.window?.setBackgroundDrawableResource(R.drawable.borde_redondeado)
    }
    @SuppressLint("SetTextI18n")
    fun mostrarEleccionFecha(){
        val inflater = LayoutInflater.from(contexto)
        view = inflater.inflate(R.layout.eleccion_fecha, null)

        asociarElementosPantallaFecha()

        val builder = AlertDialog.Builder(contexto)
        builder.setView(view)

        builder.setPositiveButton("Aceptar"){ _, _ ->
            fechaActual = "${eleccionFecha.dayOfMonth}/${(eleccionFecha.month) + 1}/${eleccionFecha.year}"
            fechaElegidaTxt.text = fechaActual
        }

        builder.setNegativeButton("Cancelar"){ dialog, _ ->
            dialog.cancel()
        }

        val dialogo = builder.create()

        dialogo.show()
        dialogo.window?.setBackgroundDrawableResource(R.drawable.borde_redondeado)
    }

    fun mostrarEleccionRecordatorio(){
        val inflater = LayoutInflater.from(contexto)
        view = inflater.inflate(R.layout.eleccion_recordatorio, null)

        var recordatorioX = "0"
        var recordatorioY = "0"
        var recordatorioZ = "0"

        asociarElementosPantallaRecordatorio()
        indicadorRecordatorioTxt.text = "Se recordara cada día"

        contenedorEleccionesRecordatorio.visibility = LinearLayout.INVISIBLE

        switchRecordatorio.setOnClickListener {
            contenedorEleccionesRecordatorio.visibility = if(contenedorEleccionesRecordatorio.visibility == LinearLayout.INVISIBLE) LinearLayout.VISIBLE else LinearLayout.INVISIBLE
            eligeRecordatorio = contenedorEleccionesRecordatorio.visibility == LinearLayout.VISIBLE
            recordatorioX = if(eligeRecordatorio) "1" else "0"
        }

        eleccionRecordatorioY.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Obtener el elemento seleccionado del array de strings
                val selectedItem = parent.getItemAtPosition(position).toString()

                recordatorioY = (position + 1).toString()
                var frecuenciaRecordatorio = ""
                when(recordatorioY){
                    "1" -> frecuenciaRecordatorio = "día"
                    "2" -> frecuenciaRecordatorio = "semana"
                    "3" -> frecuenciaRecordatorio = "mes"
                    "4" -> frecuenciaRecordatorio = "año"
                }
                indicadorRecordatorioTxt.text = "Se recordara ${if(recordatorioZ.equals("1")) "cada ${frecuenciaRecordatorio}" else "cada ${recordatorioZ} ${if(recordatorioY.equals("3")) "mese" else frecuenciaRecordatorio}s"}"
                Log.d("Spinner", "Elemento seleccionado: $selectedItem")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Manejar el caso en el que no se seleccione ningún elemento
            }
        }

        eleccionRecordatorioZ.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Obtener el elemento seleccionado del array de strings
                val selectedItem = parent.getItemAtPosition(position).toString()

                recordatorioZ = selectedItem
                var frecuenciaRecordatorio = ""
                when(recordatorioY){
                    "1" -> frecuenciaRecordatorio = "día"
                    "2" -> frecuenciaRecordatorio = "semana"
                    "3" -> frecuenciaRecordatorio = "mes"
                    "4" -> frecuenciaRecordatorio = "año"
                }
                indicadorRecordatorioTxt.text = "Se recordara ${if(recordatorioZ.equals("1")) "cada ${frecuenciaRecordatorio}" else "cada ${recordatorioZ} ${if(recordatorioY.equals("3")) "mese" else frecuenciaRecordatorio}s"}"
                Log.d("Spinner", "Elemento seleccionado: $selectedItem")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Manejar el caso en el que no se seleccione ningún elemento
            }
        }

        val builder = AlertDialog.Builder(contexto)
        builder.setView(view)

        builder.setPositiveButton("Aceptar"){ _, _ ->
            recordatorioElegido = "${recordatorioX}-${recordatorioY}-${recordatorioZ}"
            if(recordatorioX.equals("0")){
                recordatorioElegidoTxt.text = "Sin recordatorios"
            }else{
                var frecuenciaRecordatorio = ""
                when(recordatorioY){
                    "1" -> frecuenciaRecordatorio = "día"
                    "2" -> frecuenciaRecordatorio = "semana"
                    "3" -> frecuenciaRecordatorio = "mes"
                    "4" -> frecuenciaRecordatorio = "año"
                }
                recordatorioElegidoTxt.text = if(recordatorioZ.equals("1")) "Cada ${frecuenciaRecordatorio}" else "Cada ${recordatorioZ} ${if(recordatorioY.equals("3")) "mese" else frecuenciaRecordatorio}s"
            }
        }

        builder.setNegativeButton("Cancelar"){ dialog, _ ->
            dialog.cancel()
        }

        val dialogo = builder.create()

        dialogo.show()
        dialogo.window?.setBackgroundDrawableResource(R.drawable.borde_redondeado)
    }

    fun getScreenHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun vaciarPantalla(){
        nombreTarea.text.clear()
        fechaElegidaTxt.text = fechaActual
        horaElegidaTxt.text = view.context.getString(R.string.todo_el_dia)
        recordatorioElegidoTxt.text = view.context.getString(R.string.sin_recordatorios)
    }
}