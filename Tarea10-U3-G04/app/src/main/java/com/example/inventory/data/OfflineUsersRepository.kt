package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

class OfflineUsersRepository(private val userDao: UserDao) : UsuariosRepository {

    override fun getAllUsers(): Flow<List<Usuario>> = userDao.getAllUsers()

    override fun getUserId(id: Int): Flow<Usuario?> = userDao.getUser(id)

    override suspend fun insertUser(usuario: Usuario) = userDao.insert(usuario)

    override suspend fun updateUser(usuario: Usuario) = userDao.update(usuario)

    override suspend fun deleteUser(usuario: Usuario) = userDao.delete(usuario)
    override suspend fun getUserByNombreYHash(nombre: String, apellidoHash: String): Usuario? =
        userDao.getUserByNombreYHash(nombre, apellidoHash)
}