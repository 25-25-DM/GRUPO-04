package com.example.app_vehiculos.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_vehiculos.data.local.Vehiculo
import com.example.app_vehiculos.repositoty.AppRepository
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

    fun sincronizarTodo(context: Context) {
        viewModelScope.launch {
            repository.sincronizarAmbasVias(context)
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

    fun agregarVehiculo(context: Context, vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.insertarVehiculo(vehiculo, context)
                .onSuccess { _userMessage.value = "Vehículo añadido." }
                .onFailure { _userMessage.value = it.message }
        }
    }

    fun agregarVehiculoConImagen(context: Context, vehiculo: Vehiculo, uriImagen: String?) {
        viewModelScope.launch {
            var urlS3: String? = null
            if (uriImagen != null) {
                urlS3 = repository.subirImagenAS3(uriImagen)
            }
            val vehiculoConImagen = vehiculo.copy(imagenUri = urlS3)
            repository.insertarVehiculo(vehiculoConImagen, context)
                .onSuccess { _userMessage.value = "Vehículo añadido." }
                .onFailure { _userMessage.value = it.message }
        }
    }

    fun editarVehiculo(context: Context, vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.actualizarVehiculo(vehiculo, context)
                .onSuccess { _userMessage.value = "Vehículo actualizado." }
                .onFailure { _userMessage.value = it.message }
        }
    }

    fun eliminarVehiculo(context: Context, vehiculo: Vehiculo) {
        viewModelScope.launch {
            repository.eliminarVehiculo(vehiculo, context)
                .onSuccess { _userMessage.value = "Vehículo eliminado." }
                .onFailure { _userMessage.value = it.message }
        }
    }

    fun migrarImagenes() {
        viewModelScope.launch {
            try {
                repository.migrarImagenesLocalesAS3()
                _userMessage.value = "Imágenes migradas a S3 correctamente."
            } catch (e: Exception) {
                _userMessage.value = "Error al migrar imágenes: ${e.message}"
            }
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