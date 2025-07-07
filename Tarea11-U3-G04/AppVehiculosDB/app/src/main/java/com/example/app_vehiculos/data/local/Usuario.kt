package com.example.app_vehiculos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(

    @PrimaryKey
    val nombre: String,

    val passwordHash: String,
    val esAdmin: Boolean
)