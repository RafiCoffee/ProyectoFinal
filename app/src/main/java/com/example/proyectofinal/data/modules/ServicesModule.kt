package com.example.proyectofinal.data.modules

import com.example.proyectofinal.data.services.GeneralService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {
    @Provides
    fun provideConfigurationService(): GeneralService{
        return GeneralService()
    }
}