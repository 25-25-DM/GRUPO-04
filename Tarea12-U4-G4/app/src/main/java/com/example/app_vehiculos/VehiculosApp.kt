package com.example.app_vehiculos

import android.app.Application
import com.example.app_vehiculos.data.local.AppDatabase
import com.example.app_vehiculos.repositoty.AppRepository

class VehiculosApp : Application() {
    private val database by lazy { AppDatabase.getInstance(this) }

    val repository by lazy { AppRepository(database.usuarioDao(), database.vehiculoDao(), this) }
}