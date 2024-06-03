package com.example.proyectofinal.ui.adapters.viewHolders

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.databinding.ActualFriendLayoutBinding
import com.example.proyectofinal.databinding.FriendLayoutBinding
import com.example.proyectofinal.databinding.MediaLayoutBinding

class ViewHolderAmigoMedia (view: View, firebaseService: FirebaseService) : RecyclerView.ViewHolder (view) {
    private var firebaseService = firebaseService
    private var view = view
    private var binding: MediaLayoutBinding

    init {
        binding = MediaLayoutBinding.bind(view)
    }

    fun renderize(amigo: Amigo) {
        firebaseService.cambiarImagenUsuario(view.context, binding.fotoUsuarioImg, amigo.foto!!)
        binding.nombreAmigo.text = amigo.nombre

        binding.recyclerView.id = amigo.id.hashCode()
    }
}