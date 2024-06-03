package com.example.proyectofinal.data.dataSource.dataBase.framework

import kotlin.random.Random

object NotifyTaskVariety {
    val inicioFrases: List<String> = listOf("¿Como llevas la tarea ",
        "¿Te has olvidado de la tarea ", "¿Has hecho la tarea ")

    fun devolverFrase(nombreTarea: String): String{
        val fraseIndex = Random(0).nextInt(0, inicioFrases.size - 1)
        val fraseCompleta = inicioFrases[fraseIndex] + nombreTarea
        return fraseCompleta
    }
}