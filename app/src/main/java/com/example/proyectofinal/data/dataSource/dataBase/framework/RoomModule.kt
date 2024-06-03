package com.example.proyectofinal.data.dataSource.dataBase.framework

import android.app.Application
import androidx.room.Room
import com.example.proyectofinal.data.dataSource.dataBase.AppDatabase
import com.example.proyectofinal.data.dataSource.dataBase.dao.NotificacionDao
import com.example.proyectofinal.data.dataSource.dataBase.dao.TareaDao
import com.example.proyectofinal.data.dataSource.dataBase.dao.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    fun provideDataBase(app: Application): AppDatabase{
        return Room.databaseBuilder(
            app, AppDatabase::class.java,
            "ZapeTask"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUsuarioDao(dataBase: AppDatabase): UsuarioDao {
        return dataBase.usuarioDao()
    }

    @Provides
    fun provideTareaDao(dataBase: AppDatabase): TareaDao{
        return dataBase.tareaDao()
    }

    @Provides
    fun provideNotificacionDao(dataBase: AppDatabase): NotificacionDao {
        return dataBase.notificacionDao()
    }
}