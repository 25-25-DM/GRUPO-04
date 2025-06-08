package com.example.inventory.ui.auth

import androidx.lifecycle.ViewModel
import com.example.inventory.data.Usuario
import com.example.inventory.data.UsuariosRepository
import com.example.inventory.function.hashSHA256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(private val usuariosRepository: UsuariosRepository) : ViewModel() {

    suspend fun validarCredenciales(nombre: String, apellido: String): Usuario? {
        if (nombre.isBlank() || apellido.isBlank()) return null

        val apellidoHash = hashSHA256(apellido)

        return withContext(Dispatchers.IO) {
            usuariosRepository.getUserByNombreYHash(nombre, apellidoHash)
        }
    }
}