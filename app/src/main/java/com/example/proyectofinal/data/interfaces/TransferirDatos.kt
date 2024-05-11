package com.example.proyectofinal.data.interfaces

import android.widget.ListView

interface TransferirDatos {
    fun transferirListView(dato: ListView)
    fun getListView(): ListView
}
