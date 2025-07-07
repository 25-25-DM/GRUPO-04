package com.example.app_vehiculos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre LIMIT 1")
    suspend fun getUsuarioPorNombre(nombre: String): Usuario?

    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<Usuario>>
}

