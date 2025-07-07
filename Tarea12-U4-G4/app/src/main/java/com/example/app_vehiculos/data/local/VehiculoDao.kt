package com.example.app_vehiculos.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVehiculo(vehiculo: Vehiculo)

    @Update
    suspend fun actualizarVehiculo(vehiculo: Vehiculo)

    @Query("SELECT COUNT(*) FROM vehiculos")
    suspend fun countVehiculos(): Int

    @Delete
    suspend fun eliminarVehiculo(vehiculo: Vehiculo)

    @Query("SELECT * FROM vehiculos ORDER BY marca ASC")
    fun getAllVehiculos(): Flow<List<Vehiculo>>
}

