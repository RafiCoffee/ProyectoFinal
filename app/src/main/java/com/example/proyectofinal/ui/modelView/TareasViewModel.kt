package com.example.proyectofinal.ui.modelView

import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.data.repositories.TareasRepository
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.ui.adapters.AdapterTarea
import com.example.proyectofinal.ui.dialogues.SaveDialogue
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class TareasViewModel @Inject constructor(userId: String, tareasRepository: TareasRepository, tareasService: TareasService, pantallaCrearTarea: LinearLayout) : ViewModel() {
    val userId by lazy { userId }
    val tareasRepository by lazy { tareasRepository }
    val tareasService by lazy { tareasService }
    val pantallaCrearTarea by lazy { pantallaCrearTarea }

    lateinit var myRecyclerView: RecyclerView

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _tareasNota = MutableLiveData<List<Tarea>>(emptyList())
    val tareasNota: LiveData<List<Tarea>> get() = _tareasNota

    private val _tareasDiarias = MutableLiveData<List<Tarea>>(emptyList())
    val tareasDiarias: LiveData<List<Tarea>> get() = _tareasDiarias

    private val _tareasEvento = MutableLiveData<List<Tarea>>(emptyList())
    val tareasEvento: LiveData<List<Tarea>> get() = _tareasEvento

    private val _fecha = MutableLiveData<String>()
    val fecha: LiveData<String> get() = _fecha


    lateinit var saveTarea: SaveDialogue

    private var fechaActual = "${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}/${
        Calendar.getInstance().get(
            Calendar.MONTH) + 1}/${Calendar.getInstance().get(Calendar.YEAR)}"
    init{
        loadFecha(fechaActual)
    }

    fun loadTareas(fecha: String) {
        viewModelScope.launch {
            _loading.value = true
            val tareas = tareasRepository.getAll(userId)
            tareas.let { tareaList ->
                _tareasNota.value = tareaList.filter { it.tipoTarea == 1 }
                _tareasNota.value = _tareasNota.value!!.filter { it.fecha.equals(fecha) }
                _tareasDiarias.value = tareaList.filter { it.tipoTarea == 2 }
                _tareasDiarias.value = _tareasDiarias.value!!.filter { it.fecha.equals(fecha) }
                _tareasEvento.value = tareaList.filter { it.tipoTarea == 3 }
                _tareasEvento.value = _tareasEvento.value!!.filter { it.fecha.equals(fecha) }

                _loading.value = false
            }
        }
    }

    fun loadFecha(fechaElegida: String){
        viewModelScope.launch {
            _fecha.value = fechaElegida
            loadTareas(fecha.value!!)
        }
    }

    fun setAdapter(recyclerView: RecyclerView, tipoTarea: Int) {
        var tareasList = mutableListOf<Tarea>()
        when (tipoTarea) {
            1 -> tareasList = _tareasNota.value?.toMutableList() ?: mutableListOf()
            2 -> tareasList = _tareasDiarias.value?.toMutableList() ?: mutableListOf()
            3 -> tareasList = _tareasEvento.value?.toMutableList() ?: mutableListOf()
        }
        recyclerView.adapter = AdapterTarea(
            tareasList,
            { pos, isCompleted, tipoTareaEditable -> completarTarea(pos, isCompleted, tipoTareaEditable) },
            { pos -> editarTarea(pos, tareasList[pos]) }
            //{ pos -> eliminarVideoJuego(pos) },
            //{ pos -> editarVideojuego(pos) }
        )

        myRecyclerView = recyclerView

        saveTarea = SaveDialogue(recyclerView, pantallaCrearTarea, tareasService)
    }

    fun setAddButton(addButton : ImageView, tipoTarea: Int, fechaElegida: String){
        addButton.setOnClickListener {
            saveTarea.mostrarDialogoSaveTarea(this, tipoTarea, fechaElegida, null, null)
        }
    }

    fun editarTarea(pos: Int, tareaEditable: Tarea){
        saveTarea.mostrarDialogoSaveTarea(this, tareaEditable.tipoTarea, tareaEditable.fecha, pos, tareaEditable)
    }

    fun completarTarea(pos: Int, isCompleted: Boolean, tipoTarea: Int){
        viewModelScope.launch {
            lateinit var tarea: Tarea
            when(tipoTarea){
                1 -> tarea = _tareasNota.value?.get(pos)!!
                2 -> tarea = _tareasDiarias.value?.get(pos)!!
                3 -> tarea = _tareasEvento.value?.get(pos)!!
            }

            tarea.completada = isCompleted
            if(!isCompleted) tarea.fechaCompletada = ""
            tareasRepository.editarTarea(tarea, userId)
        }
    }

    fun agregarTareaRepo(nuevaTarea: Tarea){
        viewModelScope.launch {
            _loading.value = true

            tareasRepository.insertarTarea(nuevaTarea, userId)
            loadTareas(fecha.value!!)

            when (nuevaTarea.tipoTarea) {
                1 -> myRecyclerView.adapter?.notifyItemInserted(_tareasNota.value!!.size)
                2 -> myRecyclerView.adapter?.notifyItemInserted(_tareasDiarias.value!!.size)
                3 -> myRecyclerView.adapter?.notifyItemInserted(_tareasEvento.value!!.size)
            }

            _loading.value = true
        }
    }

    fun editarTareaRepo(pos: Int, tarea: Tarea){
        viewModelScope.launch {
            _loading.value = true

            tareasRepository.editarTarea(tarea, userId)
            loadTareas(fecha.value!!)

            myRecyclerView.adapter?.notifyItemChanged(pos)
            _loading.value = true
        }
    }

    fun eliminarTareaRepo(pos: Int, tipoTarea: Int){
        viewModelScope.launch {
            _loading.value = true

            val tareaEliminar = getTarea(pos, tipoTarea)!!

            tareasRepository.eliminarTarea(tareaEliminar)
            loadTareas(fecha.value!!)

            myRecyclerView.adapter?.notifyItemRemoved(pos)

            _loading.value = false
        }
    }

    fun getTarea(pos: Int, tipoTarea: Int): Tarea?{
        var tarea: Tarea? = null

        when (tipoTarea) {
            1 -> tarea = _tareasNota.value?.get(pos)
            2 -> tarea = _tareasDiarias.value?.get(pos)
            3 -> tarea = _tareasEvento.value?.get(pos)
        }

        return tarea
    }
}

@Suppress("UNCHECKED_CAST")
class TareasViewModelFactory(private val userId: String, private val tareasRepository: TareasRepository, private val tareasService: TareasService, private val pantallaCrearTarea: LinearLayout) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TareasViewModel(userId, tareasRepository, tareasService, pantallaCrearTarea) as T
    }
}