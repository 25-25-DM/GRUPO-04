package com.example.app_vehiculos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_vehiculos.data.local.Vehiculo
import com.example.app_vehiculos.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehiculoViewModel(private val repository: AppRepository) : ViewModel() {

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage = _userMessage.asStateFlow()

    val vehiculosState = repository.todosLosVehiculos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        sincronizarTodo()
    }

    fun sincronizarTodo() {
        viewModelScope.launch {
            repository.sincronizarAmbasVias()
                .onSuccess {
                    _userMessage.value = "Datos sincronizados con éxito."
                    Log.i("VehiculoViewModel", "Sincronización bidireccional completada.")
                }
                .onFailure {
                    _userMessage.value = it.message ?: "Error desconocido en la sincronización."
                    Log.e("VehiculoViewModel", "Fallo en la sincronización: ${it.message}")
                }
        }
    }

    fun agregarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.insertarVehiculo(vehiculo)
                .onSuccess { _userMessage.value = "Vehículo añadido." }
                .onFailure { _userMessage.value = it.message }
        }
    }

    fun editarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.actualizarVehiculo(vehiculo)
                .onSuccess { _userMessage.value = "Vehículo actualizado." }
                .onFailure { _userMessage.value = it.message }
        }
    }

    fun eliminarVehiculo(vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.eliminarVehiculo(vehiculo)
                .onSuccess { _userMessage.value = "Vehículo eliminado." }
                .onFailure { _userMessage.value = it.message }
        }
    }

    fun onMessageShown() {
        _userMessage.value = null
    }
}

class VehiculoViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehiculoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VehiculoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}