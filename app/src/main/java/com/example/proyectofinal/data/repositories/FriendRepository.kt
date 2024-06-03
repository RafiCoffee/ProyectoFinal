package com.example.proyectofinal.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.data.models.Amigo
import com.example.proyectofinal.data.services.UserService

@RequiresApi(Build.VERSION_CODES.Q)
class FriendRepository(private var userService: UserService) {
    suspend fun getAll(): List<Amigo>{
        return userService.getAllFriends()
    }

    suspend fun getAllActual(): List<Amigo>{
        return userService.getAllActualFriends()
    }
}