package com.example.proyectofinal.data.framework

import java.util.Random

object NotifyTaskVariety {
    val inicioFrases : Array<String> = arrayOf("Deberias echarle un ojo a la tarea ", "Tu tarea ",
        "No hagas esperar a tu tarea ", "No olvides completar tu tarea ")

    val finFrases : Array<String> = arrayOf(".", " te esta esperando.", ".", ".")
    fun devolverFrase(nombreTarea: String): String{
        val fraseIndex = Random(1).nextInt(inicioFrases.size)
        val frase = inicioFrases[fraseIndex] + nombreTarea + finFrases[fraseIndex]
        return frase
    }
}