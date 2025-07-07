package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

interface UsuariosRepository {

    fun getAllUsers(): Flow<List<Usuario>>

    fun getUserId(id: Int): Flow<Usuario?>

    suspend fun insertUser(usuario: Usuario): Long

    suspend fun updateUser(usuario: Usuario)

    suspend fun deleteUser(usuario: Usuario)

    suspend fun getUserByNombreYHash(nombre: String, apellidoHash: String): Usuario?

}