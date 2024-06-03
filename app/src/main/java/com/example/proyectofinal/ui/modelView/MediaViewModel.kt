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
import com.example.proyectofinal.data.models.Notificacion
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.data.repositories.FriendRepository
import com.example.proyectofinal.data.repositories.NotificationRepository
import com.example.proyectofinal.data.repositories.TareasRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.adapters.AdapterAmigo
import com.example.proyectofinal.ui.adapters.AdapterAmigoActual
import com.example.proyectofinal.ui.adapters.AdapterAmigoMedia
import com.example.proyectofinal.ui.adapters.AdapterNotificacion
import com.example.proyectofinal.ui.adapters.AdapterTareaMedia
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
class MediaViewModel @Inject constructor(friendRepository: FriendRepository, tareasRepository: TareasRepository, firebaseService: FirebaseService): ViewModel() {
    val friendRepository by lazy { friendRepository }
    val tareasRepository by lazy { tareasRepository }
    val firebaseService by lazy { firebaseService }

    lateinit var myMainMediaRecyclerView: RecyclerView
    lateinit var tareasRecyclerViews: List<RecyclerView>

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _actualMediaFriendList = MutableLiveData<List<Amigo>>(emptyList())
    val actualMediaFriendList: LiveData<List<Amigo>> get() = _actualMediaFriendList

    private val _tareasMediaList = MutableLiveData<Map<String, List<Tarea>>>(emptyMap())
    val tareasMediaList: LiveData<Map<String, List<Tarea>>> get() = _tareasMediaList
    fun loadAmigosMedia(){
        viewModelScope.launch {
            _loading.value = true
            val amigosActuales = friendRepository.getAllActual()
            amigosActuales.let { friendList ->
                _actualMediaFriendList.value = friendList
                _loading.value = false
            }
            loadTareasMedia()
        }
    }

    fun loadTareasMedia(){
        viewModelScope.launch {
            _loading.value = true
            val tareasAmigos: MutableMap<String, List<Tarea>> = mutableMapOf()
            for (amigo in _actualMediaFriendList.value!!){
                val tareasAmigo = tareasRepository.getAllTareasByFriend(amigo.id)
                tareasAmigos[amigo.id] = tareasAmigo
            }
            tareasAmigos.let { tareasMap ->
                _tareasMediaList.value = tareasMap.toMap()
                _loading.value = false
            }
        }
    }

    fun setMediaFriendAdapter(mainRecyclerView: RecyclerView) {
        val actualFriendList = _actualMediaFriendList.value?.toMutableList() ?: mutableListOf()

        mainRecyclerView.adapter = AdapterAmigoMedia(
            actualFriendList,
            firebaseService
        )

        myMainMediaRecyclerView = mainRecyclerView
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Suppress("UNCHECKED_CAST")
class MediaViewModelFactory(private val friendRepository: FriendRepository, private val tareasRepository: TareasRepository, private val firebaseService: FirebaseService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MediaViewModel(friendRepository, tareasRepository, firebaseService) as T
    }
}