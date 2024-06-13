package com.example.proyectofinal.data.services

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import com.example.proyectofinal.R
import com.example.proyectofinal.ui.modelView.TareasViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

class GeneralService @Inject constructor(private var firebaseService: FirebaseService) {
    private lateinit var sP: SharedPreferences

    fun abrirGaleria(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 1)
    }

    fun comprobarTema(context: Context) : Int{
        sP = context.getSharedPreferences("ShPr", MODE_PRIVATE)
        return sP.getInt("Tema",1)
    }

    fun setTema(context: Context){
        val tema = comprobarTema(context)

        if(tema == 0){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun copiarPortapapeles(texto: String, context: Context) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Texto copiado", texto)
        clipboardManager.setPrimaryClip(clip)

        Toast.makeText(context, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    fun comprobarUsuarioLogueado(context: Context) : Boolean{
        sP = context.getSharedPreferences("ShPr", MODE_PRIVATE)
        return sP.getBoolean("UsuarioLogueado",false)
    }
    fun setUsuarioLogueado(context: Context, isLogueado: Boolean){
        val sPEdit = context.getSharedPreferences("ShPr", MODE_PRIVATE).edit()
        sPEdit.putBoolean("UsuarioLogueado", isLogueado)
        sPEdit.apply()
    }

    fun isValidEmail(email: String): Boolean {
        val regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return regex.matches(email)
    }

    fun mostrarObjetosClave(contexto: Context, objetosList: MutableList<String>){
        val objetosListAux = objetosList

        val builder = AlertDialog.Builder(contexto)
        builder.setTitle("Introducir Objetos Clave")

        val contenedorPadre = LinearLayout(contexto)

        val parametrosContenedoresHijos =  LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        parametrosContenedoresHijos.weight = 1f

        val contenedorIzq = LinearLayout(contexto)
        contenedorIzq.orientation = LinearLayout.VERTICAL
        contenedorIzq.gravity = Gravity.CENTER
        contenedorIzq.layoutParams = parametrosContenedoresHijos
        contenedorIzq.setPadding(10,5,10,5)

        val contenedorDer = LinearLayout(contexto)
        contenedorDer.orientation = LinearLayout.VERTICAL
        contenedorDer.gravity = Gravity.CENTER
        contenedorDer.layoutParams = parametrosContenedoresHijos
        contenedorDer.setPadding(10,5,10,5)

        val objetoClaveTxt = TextView(contexto)
        objetoClaveTxt.text = "Objeto:"
        contenedorIzq.addView(objetoClaveTxt)
        val objetoClave = EditText(contexto)
        objetoClave.hint = "Introduce un objeto"
        contenedorIzq.addView(objetoClave)

        val introducirBt = Button(contexto)
        introducirBt.text = "Introducir"
        contenedorIzq.addView(introducirBt)

        val eliminarBt = Button(contexto)
        eliminarBt.text = "Eliminar"
        contenedorIzq.addView(eliminarBt)

        val contenedorLista = LinearLayout(contexto)
        contenedorLista.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 400)
        val listaObjetos = ListView(contexto)
        listaObjetos.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val objetosClaveAdapter = ArrayAdapter(contexto, R.layout.list_objetos_clave, objetosListAux)
        listaObjetos.adapter = objetosClaveAdapter
        contenedorLista.addView(listaObjetos)
        contenedorDer.addView(contenedorLista)

        contenedorPadre.addView(contenedorIzq)
        contenedorPadre.addView(contenedorDer)

        builder.setView(contenedorPadre)

        /*listaObjetos.setOnItemClickListener { _, _, position, _ ->
            objetoClave.text = objetosListAux.get(position)
        }*/

        introducirBt.setOnClickListener {
            objetosListAux.add(objetoClave.text.toString())
            objetosClaveAdapter.notifyDataSetChanged()
            objetoClave.text.clear()
        }

        eliminarBt.setOnClickListener {
            objetosListAux.remove(objetoClave.text.toString())
            objetosClaveAdapter.notifyDataSetChanged()
            objetoClave.text.clear()
        }

        builder.setPositiveButton("Aceptar"){ _, _ ->
            objetosList.removeAll(objetosList)
            objetosList.addAll(objetosListAux)
        }

        builder.setNegativeButton("Cancelar"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    fun mostrarEleccionFecha(contexto: Context, fechaElegidaTxt: TextView, viewModel: TareasViewModel){
        var fechaSeleccionada = fechaElegidaTxt.text.toString()

        val builder = AlertDialog.Builder(contexto)

        val contenedorPadre = LinearLayout(contexto)

        val calendario = DatePicker(contexto)
        calendario.spinnersShown = true
        calendario.calendarViewShown = false
        val fechaActual = Calendar.getInstance()
        calendario.minDate = fechaActual.timeInMillis
        var fechaActualSeleccionada = fechaSeleccionada.toCalendar()
        calendario.init(fechaActualSeleccionada.get(Calendar.YEAR), fechaActualSeleccionada.get(Calendar.MONTH), fechaActualSeleccionada.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, day ->
            val realMonth = month + 1
            fechaSeleccionada = "$day/$realMonth/$year"
        }
        contenedorPadre.addView(calendario)

        builder.setView(contenedorPadre)

        builder.setPositiveButton("Aceptar"){ _, _ ->
            fechaElegidaTxt.text = fechaSeleccionada
            viewModel.loadFecha(fechaSeleccionada)
        }

        builder.setNegativeButton("Cancelar"){ dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    fun String.toCalendar(dateFormat: String = "dd/MM/yyyy"): Calendar {
        val sdf = SimpleDateFormat(dateFormat)
        val date = sdf.parse(this)
        return Calendar.getInstance().apply {
            time = date
        }
    }

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    suspend fun cambiarImagen(imagen: ImageView, imageUri: Uri): Boolean{
        imagen.setImageURI(imageUri)
        return true
    }

}