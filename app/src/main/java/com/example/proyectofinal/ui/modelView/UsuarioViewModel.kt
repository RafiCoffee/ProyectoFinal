package com.example.proyectofinal.ui.modelView

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.callbacks.UsuarioCallback
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsuarioViewModel @Inject constructor(userService: UserService): ViewModel() {
    val userService by lazy { userService }

    private val _userLoaded = MutableLiveData(false)
    val userLoaded: LiveData<Boolean> get() = _userLoaded

    private val _loggedUser = MutableLiveData<Usuario?>()
    val loggedUser: LiveData<Usuario?> get() = _loggedUser

    fun loadUsuariosInit() {
        viewModelScope.launch {
            var usuarioActual: Usuario? = null
            userService.obtenerDatosUsuarioActual{ usuarioRecuperado ->
                usuarioActual = usuarioRecuperado
                usuarioActual.let { user ->
                    if(user != null){
                        _loggedUser.value = user
                    }
                    _userLoaded.value = true
                }
            }
        }
    }

    fun setLoaded(v: Boolean){
        _userLoaded.value = v
    }

    fun setLoggedUser(user: Usuario){
        _loggedUser.value = user
    }

    /*fun agregarUsuarioRepo(nuevaTarea: Tarea){
        viewModelScope.launch {
            _loading.value = true
            //delay(2000)

            tareasRepository.insertarTarea(nuevaTarea, userId)
            loadTareas()

            when (nuevaTarea.tipoTarea) {
                1 -> myRecyclerView.adapter?.notifyItemInserted(_tareasNota.value!!.size)
                2 -> myRecyclerView.adapter?.notifyItemInserted(_tareasDiarias.value!!.size)
                3 -> myRecyclerView.adapter?.notifyItemInserted(_tareasEvento.value!!.size)
            }

            _loading.value = true
        }
    }

    fun eliminarUsuarioRepo(pos: Int, tipoTarea: Int){
        viewModelScope.launch {
            _loading.value = true
            //delay(2000)

            val tareaEliminar = getTarea(pos, tipoTarea)!!

            tareasRepository.eliminarTarea(tareaEliminar)
            loadTareas()

            myRecyclerView.adapter?.notifyItemRemoved(pos)

            _loading.value = false
        }
    }

    fun getUsuario(pos: Int, tipoTarea: Int): Tarea?{
        var tarea: Tarea? = null

        when (tipoTarea) {
            1 -> tarea = _tareasNota.value?.get(pos)
            2 -> tarea = _tareasDiarias.value?.get(pos)
            3 -> tarea = _tareasEvento.value?.get(pos)
        }

        return tarea
    }*/
}

@Suppress("UNCHECKED_CAST")
class UsuarioViewModelFactory(private val userService: UserService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UsuarioViewModel(userService) as T
    }
}