package com.example.inventory.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Usuario
import com.example.inventory.data.UsuariosRepository
import com.example.inventory.function.hashSHA256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterViewModel(private val usuariosRepository: UsuariosRepository) : ViewModel() {

    fun registrarUsuarios(nombre: String, apellido: String){
        if (nombre.isBlank()|| apellido.isBlank()) return

        val apellidoHash = hashSHA256(apellido)

        val newUser = Usuario(
            nombre = nombre,
            apellido = apellido,
            apellidoHash = apellidoHash,
        )

        viewModelScope.launch {
            usuariosRepository.insertUser(newUser)
        }

    }

    suspend fun verificarUser(nombre: String, apellido: String): Boolean {
        if (nombre.isBlank() || apellido.isBlank()) return false

        val apellidoHash = hashSHA256(apellido)

        return withContext(Dispatchers.IO) {
            val usuario = usuariosRepository.getUserByNombreYHash(nombre, apellidoHash)
            usuario != null
        }
    }

}