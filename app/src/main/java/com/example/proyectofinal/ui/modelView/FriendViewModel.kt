package com.example.proyectofinal.ui.modelView

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.repositories.FriendRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.adapters.AdapterAmigo
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
class FriendViewModel @Inject constructor(friendRepository: FriendRepository,
                                          userService: UserService,
                                          firebaseService: FirebaseService): ViewModel() {

    val friendRepository by lazy { friendRepository }
    val userService by lazy { userService }
    val firebaseService by lazy { firebaseService }

    lateinit var myRecyclerView: RecyclerView

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _friendList = MutableLiveData<List<Amigo>>(emptyList())
    val friendList: LiveData<List<Amigo>> get() = _friendList

    private val _friendFiltredList = MutableLiveData<List<Amigo>>(emptyList())
    val friendFiltredList: LiveData<List<Amigo>> get() = _friendFiltredList

    fun loadFriends(){
        viewModelScope.launch {
            _loading.value = true
            val posiblesAmigos = friendRepository.getAll()
            val amigosActuales = friendRepository.getAllActual()
            val idsAmigosActuales = amigosActuales.map { it.id }.toSet()
            posiblesAmigos.let { friendList ->
                _friendList.value = friendList.filterNot { it.id in idsAmigosActuales }
                _loading.value = false
            }
        }
    }

    fun loadFriendsByName(nombreIntroducido: String){
        viewModelScope.launch {
            _loading.value = true
            _friendFiltredList.value = _friendList.value?.filter { it.nombre.contains(nombreIntroducido, true) }
            _loading.value = false
        }
    }

    fun loadFriendsByCode(codigoIntroducido: String){
        viewModelScope.launch {
            _loading.value = true
            _friendFiltredList.value = _friendList.value?.filter { it.idAmigo.contains(codigoIntroducido) }
            _loading.value = false
        }
    }

    /**
     * Si busqueda = 0 -> Todos los usuarios
     * Si busqueda = 1 -> Usuarios por nombre o codigo
     */
    fun setAdapter(recyclerView: RecyclerView, busqueda: Int) {
        var friendsList = mutableListOf<Amigo>()
        when(busqueda){
            0 -> friendsList = _friendList.value?.toMutableList() ?: mutableListOf()
            1 -> friendsList = _friendFiltredList.value?.toMutableList() ?: mutableListOf()
        }
        recyclerView.adapter = AdapterAmigo(
            friendsList,
            firebaseService,
            userService
        )

        myRecyclerView = recyclerView
    }

    fun agregarAmigoRepo(nuevoAmigo: Amigo){
        viewModelScope.launch {
            _loading.value = true

            userService.addFriendToUser(nuevoAmigo)
            loadFriends()

            _loading.value = true
        }
    }

    /*fun eliminarUsuarioRepo(pos: Int, tipoTarea: Int){
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
class FriendViewModelFactory(private val friendRepository: FriendRepository, private val userService: UserService, private val firebaseService: FirebaseService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FriendViewModel(friendRepository, userService, firebaseService) as T
    }
}