package com.example.app_vehiculos.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.app_vehiculos.model.Usuario
import com.example.app_vehiculos.model.Vehiculo
import com.example.app_vehiculos.R
import com.example.app_vehiculos.view.*
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val usuarios = remember {
        mutableStateListOf(
            Usuario("Byron", "Flores"),
            Usuario("Jordi", "Pila"),
            Usuario("Veyker", "Barrionuevo"),
            Usuario("Joffre", "Arias"),
            Usuario("Edgar", "Tipan"),
            Usuario("Angelo", "Pujota"),
            Usuario("Cristian", "Lechon"),
            Usuario("Kevin", "Hurtado")
        )
    }

    val vehiculos = remember {
        mutableStateListOf(
            Vehiculo("ABC123", "Toyota", 2020, "Rojo", 50.0, true, R.drawable.toyota),
            Vehiculo("XYZ789", "Chevrolet", 2019, "Negro", 45.0, false, R.drawable.chevrolet),
            Vehiculo("DEF456", "Nissan", 2022, "Azul", 60.0, true, R.drawable.nissan),
            Vehiculo("AUC455", "Hyundai", 2025, "Negro", 75.0, true, R.drawable.hyundai),
            Vehiculo("TIL777", "Mazda", 2018, "Rojo", 35.0, true, R.drawable.mazda)


        )
    }

    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(null) }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(2000)
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("login") {
            LoginScreen(
                usuariosRegistrados = usuarios,
                onLoginSuccess = { navController.navigate("home") },
                onGoToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                usuarios = usuarios,
                onRegisterSuccess = {
                    usuarios.add(it)
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            HomeScreen(
                vehiculos = vehiculos,
                onLogout = {
                    navController.popBackStack("login", inclusive = false)
                },
                onAddVehiculo = {
                    navController.navigate("addVehiculo")
                },
                onEditVehiculo = { vehiculo ->
                    vehiculoSeleccionado = vehiculo
                    navController.navigate("editVehiculo")
                },
                onDeleteVehiculo = { vehiculo ->
                    vehiculos.remove(vehiculo)
                }
            )
        }

        composable("addVehiculo") {
            AddVehiculoScreen(
                onSave = {
                    vehiculos.add(it)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable("editVehiculo") {
            vehiculoSeleccionado?.let { vehiculo ->
                EditVehiculoScreen(
                    vehiculo = vehiculo,
                    onSave = { vehiculoEditado ->
                        val index = vehiculos.indexOfFirst { it.placa == vehiculo.placa }
                        if (index != -1) {
                            vehiculos[index] = vehiculoEditado
                        }
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}