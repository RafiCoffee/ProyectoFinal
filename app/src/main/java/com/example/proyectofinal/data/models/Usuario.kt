package com.example.proyectofinal.data.models

data class Usuario(var nombre: String, var email: String, var clave: String, var foto: String?) {
    constructor() : this("", "", "", "")
}