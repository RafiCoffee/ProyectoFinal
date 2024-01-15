package com.example.proyectofinal.Dialogos

interface DialogCallback{
    fun onDialogResult(nuevaTarea: Array<String>, isCanceled: Boolean)
}