package com.example.proyectofinal.ui.adapters.viewHolders

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.databinding.FriendLayoutBinding

class ViewHolderAmigo (view: View, firebaseService: FirebaseService, userService: UserService) : RecyclerView.ViewHolder (view) {
    private var firebaseService = firebaseService
    private var userService = userService
    private var view = view
    private var binding: FriendLayoutBinding

    private val images = intArrayOf(R.drawable.person_add_icon, R.drawable.check_icon)
    private var currentIndex = 0

    // Animations
    val inAnimation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
    val outAnimation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_out_right)

    init {
        binding = FriendLayoutBinding.bind(view)
    }

    fun renderize(amigo: Amigo) {
        var agregado = false

        firebaseService.cambiarImagenUsuario(view.context, binding.fotoUsuarioImg, amigo.foto!!)
        binding.nombreUsuario.text = amigo.nombre
        binding.codigoAmigo.text = amigo.idAmigo

        binding.addFriendImgSwitch.setFactory {
            val imageView = ImageView(view.context)
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView
        }

        binding.addFriendImgSwitch.setImageResource(images[currentIndex])

        binding.addFriendImgSwitch.inAnimation = inAnimation
        binding.addFriendImgSwitch.outAnimation = outAnimation

        binding.addFriendImgSwitch.setOnClickListener {
            if(!agregado){
                agregado = true
                currentIndex = (currentIndex + 1) % images.size
                binding.addFriendImgSwitch.setImageResource(images[currentIndex])
                userService.addFriendToUser(amigo)
            }
        }
    }
}