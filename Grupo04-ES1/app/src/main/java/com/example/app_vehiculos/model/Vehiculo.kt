package com.example.app_vehiculos.model

data class Vehiculo(
    val placa: String,
    val marca: String,
    val anio: Int,
    val color: String,
    val costoPorDia: Double,
    val activo: Boolean,
    val imagenResId: Int? = null,
    val imagenUri: String? = null

)
