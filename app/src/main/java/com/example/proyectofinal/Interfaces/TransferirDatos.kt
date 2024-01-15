package com.example.proyectofinal.Interfaces

import android.widget.ListView

interface TransferirDatos {
    fun transferirListView(dato: ListView)
    fun getListView(): ListView
}
