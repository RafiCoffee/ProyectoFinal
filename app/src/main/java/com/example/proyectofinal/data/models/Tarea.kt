package com.example.proyectofinal.data.models

class Tarea(
    var id: String = "",
    var nombreTarea: String = "",
    var hora: String = "",
    var fecha: String = "",
    var recordatorio: String = "",
    var tipoTarea: Int = 0,
    var completada: Boolean = false,
    var fechaCompletada: String = ""
)