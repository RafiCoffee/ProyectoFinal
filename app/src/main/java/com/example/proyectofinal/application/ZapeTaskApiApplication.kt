package com.example.proyectofinal.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ZapeTaskApiApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setTemaFromSharedPreferences()
    }

    private fun setTemaFromSharedPreferences() {
        val tema = getSharedPreferences("ShPr", MODE_PRIVATE).getInt("Tema",1)
        if (tema == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}