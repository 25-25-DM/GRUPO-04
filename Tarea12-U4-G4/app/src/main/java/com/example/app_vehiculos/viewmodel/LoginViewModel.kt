package com.example.app_vehiculos.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_vehiculos.data.local.Usuario
import com.example.app_vehiculos.function.hashSHA256
import com.example.app_vehiculos.notifications.mostrarNotificacionLocal
import com.example.app_vehiculos.repositoty.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AppRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterResult>(RegisterResult.Idle)
    val registerState = _registerState.asStateFlow()

    val todosLosUsuarios = repository.todosLosUsuarios.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun login(nombre: String, contrasena: String) {
        viewModelScope.launch {
            _loginState.value = LoginResult.Loading
            val usuario = repository.getUsuarioPorNombre(nombre)

            if (usuario == null) {
                _loginState.value = LoginResult.Error("Usuario no encontrado.")
                return@launch
            }

            if (hashSHA256(contrasena) == usuario.passwordHash) {
                _loginState.value = LoginResult.Success(usuario.nombre)
            } else {
                _loginState.value = LoginResult.Error("Credenciales incorrectas.")
            }
        }
    }

    fun register(nombre: String, contrasena: String, context: Context) {
        viewModelScope.launch {
            _registerState.value = RegisterResult.Loading

            if (nombre.isBlank() || contrasena.isBlank()) {
                _registerState.value = RegisterResult.Error("Todos los campos son obligatorios.")
                return@launch
            }

            if (!repository.validarContrasenaSegura(contrasena)) {
                _registerState.value = RegisterResult.Error("Contraseña insegura (mín. 8 chars, Mayús, minús, núm, símbolo).")
                return@launch
            }
            if (repository.getUsuarioPorNombre(nombre) != null) {
                _registerState.value = RegisterResult.Error("El nombre de usuario ya está en uso.")
                return@launch
            }

            val nuevoUsuario = Usuario(
                nombre = nombre,
                passwordHash = hashSHA256(contrasena),
                esAdmin = false
            )

            val resultado = repository.insertarUsuario(nuevoUsuario, context)

            if (resultado.isSuccess) {
                _registerState.value = RegisterResult.Success
            } else {
                _registerState.value = RegisterResult.Error("Error al añadir el usuario.")
            }

        }
    }

    fun resetLoginState() { _loginState.value = LoginResult.Idle }
    fun resetRegisterState() { _registerState.value = RegisterResult.Idle }
}

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    data class Success(val nombre: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class RegisterResult {
    object Idle : RegisterResult()
    object Loading : RegisterResult()
    object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

// --- FACTORY ---
class LoginViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}