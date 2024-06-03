package com.example.proyectofinal.data.modules

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.NotificationService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.data.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@RequiresApi(Build.VERSION_CODES.Q)
@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {
    @Provides
    fun provideGeneralService(): GeneralService{
        return GeneralService(provideFirebaseService())
    }
    @Provides
    fun provideUserService(): UserService{
        return UserService(provideFirebaseService(), provideNotificationService())
    }
    @Provides
    fun provideFirebaseService(): FirebaseService{
        return FirebaseService()
    }
    @Provides
    fun provideTareasService(): TareasService{
        return TareasService(provideFirebaseService(), provideNotificationService())
    }

    @Provides
    fun provideNotificationService(): NotificationService{
        return NotificationService(provideFirebaseService())
    }
}