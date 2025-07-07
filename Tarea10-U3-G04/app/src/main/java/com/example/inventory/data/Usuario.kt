package com.example.inventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val apellidoHash: String
)
