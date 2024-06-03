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
import com.example.proyectofinal.data.repositories.FriendRepository
import com.example.proyectofinal.data.repositories.NotificationRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.ui.adapters.AdapterAmigo
import com.example.proyectofinal.ui.adapters.AdapterAmigoActual
import com.example.proyectofinal.ui.adapters.AdapterNotificacion
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
class InfoViewModel @Inject constructor(friendRepository: FriendRepository, notificationRepository: NotificationRepository, userService: UserService, firebaseService: FirebaseService, tareasService: TareasService): ViewModel() {
    val friendRepository by lazy { friendRepository }
    val notificationRepository by lazy { notificationRepository }
    val userService by lazy { userService }
    val tareasService by lazy { tareasService }
    val firebaseService by lazy { firebaseService }

    lateinit var myRecyclerView: RecyclerView

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _actualFriendList = MutableLiveData<List<Amigo>>(emptyList())
    val actualFriendList: LiveData<List<Amigo>> get() = _actualFriendList

    private val _notificationList = MutableLiveData<List<Notificacion>>(emptyList())
    val notificationList: LiveData<List<Notificacion>> get() = _notificationList
    fun loadActualFriends(){
        viewModelScope.launch {
            _loading.value = true
            val amigosActuales = friendRepository.getAllActual()
            amigosActuales.let { actualFriendList ->
                _actualFriendList.value = actualFriendList
                _loading.value = false
            }
        }
    }

    fun loadNotifications(){
        viewModelScope.launch {
            _loading.value = true
            val notificaciones = notificationRepository.getAll()
            notificaciones.let { notificacionesList ->
                _notificationList.value = notificacionesList.filter { it.seMuestra }
                _loading.value = false
            }
        }
    }

    fun setNotificationAdapter(recyclerView: RecyclerView) {
        val notificationList = _notificationList.value?.toMutableList() ?: mutableListOf()

        recyclerView.adapter = AdapterNotificacion(
            notificationList,
            firebaseService,
            userService,
            tareasService,
            notificationRepository
        )

        myRecyclerView = recyclerView
    }

    fun setActualFriendAdapter(recyclerView: RecyclerView) {
        val actualFriendList = _actualFriendList.value?.toMutableList() ?: mutableListOf()

        recyclerView.adapter = AdapterAmigoActual(
            actualFriendList,
            firebaseService
        )

        myRecyclerView = recyclerView
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Suppress("UNCHECKED_CAST")
class InfoViewModelFactory(private val friendRepository: FriendRepository, private val notificationRepository: NotificationRepository, private val userService: UserService, private val firebaseService: FirebaseService, private val tareasService: TareasService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InfoViewModel(friendRepository, notificationRepository, userService, firebaseService, tareasService) as T
    }
}