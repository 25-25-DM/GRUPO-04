package com.example.app_vehiculos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.app_vehiculos.model.Usuario
import com.example.app_vehiculos.model.Vehiculo
import com.example.app_vehiculos.navigation.AppNavigation
import com.example.app_vehiculos.ui.theme.App_VehiculosTheme
import com.example.app_vehiculos.view.AddVehiculoScreen
import com.example.app_vehiculos.view.HomeScreen
import com.example.app_vehiculos.view.LoginScreen
import com.example.app_vehiculos.view.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App_VehiculosTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("login") }
    val usuariosRegistrados = remember { mutableStateListOf(
        Usuario("Byron", "Flores"),
        Usuario("Jordi", "Pila"),
        Usuario("Veyker", "Barrionuevo")
    )}

    val vehiculos = remember {
        mutableStateListOf(
            Vehiculo("ABC123", "Toyota", 2020, "Rojo", 50.0, true, R.drawable.toyota),
            Vehiculo("XYZ789", "Chevrolet", 2019, "Azul", 45.0, false, R.drawable.chevrolet),
            Vehiculo("DEF456", "Nissan", 2022, "Blanco", 60.0, true, R.drawable.nissan)
        )
    }

    var vehiculoEnEdicion by remember { mutableStateOf<Vehiculo?>(null) }

    when (currentScreen) {
        "login" -> LoginScreen(
            usuariosRegistrados = usuariosRegistrados,
            onLoginSuccess = { currentScreen = "home" },
            onGoToRegister = { currentScreen = "register" }
        )

        "register" -> RegisterScreen(
            usuarios = usuariosRegistrados,
            onRegisterSuccess = {
                usuariosRegistrados.add(it)
                currentScreen = "login"
            }
        )

        "home" -> HomeScreen(
            vehiculos = vehiculos,
            onLogout = { currentScreen = "login" },
            onAddVehiculo = {
                vehiculoEnEdicion = null
                currentScreen = "addVehiculo"
            },
            onEditVehiculo = {
                vehiculoEnEdicion = it
                currentScreen = "addVehiculo"
            },
            onDeleteVehiculo = {
                vehiculos.remove(it)
            }
        )

        "addVehiculo" -> AddVehiculoScreen(
            vehiculoAEditar = vehiculoEnEdicion,
            onSave = {
                if (vehiculoEnEdicion != null) {
                    vehiculos.remove(vehiculoEnEdicion)
                }
                vehiculos.add(it)
                vehiculoEnEdicion = null
                currentScreen = "home"
            },
            onCancel = {
                vehiculoEnEdicion = null
                currentScreen = "home"
            }
        )
    }
}