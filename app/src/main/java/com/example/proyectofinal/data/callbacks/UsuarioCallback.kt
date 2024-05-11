package com.example.proyectofinal.data.callbacks

import com.example.proyectofinal.data.models.Usuario

interface UsuarioCallback {
    fun onUsuarioObtenido(usuario: Usuario?)
}