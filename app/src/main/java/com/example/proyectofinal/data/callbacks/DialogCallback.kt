package com.example.proyectofinal.data.callbacks

interface DialogCallback{
    fun onDialogResult(nuevaTarea: Array<String>, isCanceled: Boolean)
}