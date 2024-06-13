package com.example.proyectofinal.ui.adapters.viewHolders

import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.framework.NotifyTaskVariety
import com.example.proyectofinal.data.models.Notificacion
import com.example.proyectofinal.data.repositories.NotificationRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.databinding.NotificationLayoutBinding

@RequiresApi(Build.VERSION_CODES.Q)
class ViewHolderNotificacion (view: View, firebaseService: FirebaseService, userService: UserService, tareasService: TareasService, notificationRepository: NotificationRepository) : RecyclerView.ViewHolder (view) {
    private var firebaseService = firebaseService
    private var userService = userService
    private var tareasService = tareasService
    private var notificationRepository = notificationRepository
    private var view = view
    private var binding: NotificationLayoutBinding

    private val images = intArrayOf(R.drawable.person_add_icon, R.drawable.check_icon)
    private var currentIndex = 0

    // Animations
    val inAnimation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
    val outAnimation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_out_right)

    init {
        binding = NotificationLayoutBinding.bind(view)
    }

    fun renderize(notificacion: Notificacion) {
        if(notificacion.tipoNotificacion == 1){
            binding.friendNotificationLayout.visibility = LinearLayout.GONE

            tareasService.getTareaById(notificacion.idInfo){ task ->
                binding.infoTarea.text = NotifyTaskVariety.devolverFrase(task?.nombreTarea!!)
            }
        }else{
            var decidido = false

            binding.tareaNotificationLayout.visibility = LinearLayout.GONE
            userService.getUserById(notificacion.idInfo){ user ->
                firebaseService.cambiarImagenUsuario(view.context, binding.fotoUsuarioImg, user?.foto!!){}
                binding.infoAmigo.text = "El usuario ${user.nombre} quiere ser tu amigo"
            }

            if(!decidido){
                binding.acceptFriendBt.setOnClickListener {
                    decidido = true
                    binding.rejectFriendBt.visibility = LinearLayout.GONE
                    userService.acceptFriend(notificacion.idInfo)
                    notificationRepository.eliminarNotificacion(notificacion.id)
                }

                binding.rejectFriendBt.setOnClickListener {
                    decidido = true
                    binding.acceptFriendBt.visibility = LinearLayout.GONE
                    userService.rejectFriend(notificacion.idInfo)
                    notificationRepository.eliminarNotificacion(notificacion.id)
                }
            }
        }
    }
}