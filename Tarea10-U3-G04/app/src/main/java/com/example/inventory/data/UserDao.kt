package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usuario: Usuario): Long

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("SELECT * from users WHERE id = :id")
    fun getUser(id: Int): Flow<Usuario>

    @Query("SELECT * from users ORDER BY nombre ASC")
    fun getAllUsers(): Flow<List<Usuario>>

    @Query("SELECT * FROM users WHERE nombre = :nombre AND apellidoHash = :apellidoHash LIMIT 1")
    suspend fun getUserByNombreYHash(nombre: String, apellidoHash: String): Usuario?

}