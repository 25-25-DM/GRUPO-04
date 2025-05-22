// AppNavigation.kt
package com.example.app_vehiculos.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.app_vehiculos.model.Usuario
import com.example.app_vehiculos.model.Vehiculo
import com.example.app_vehiculos.view.*
import kotlinx.coroutines.delay
import com.example.app_vehiculos.R
import com.example.app_vehiculos.function.UsuariosCifradosScreen
import com.example.app_vehiculos.function.hashSHA256

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val usuarios = remember {
        mutableStateListOf(
            Usuario("Byron", hashSHA256("Flores")),
            Usuario("Jordi", hashSHA256("Pila")),
            Usuario("Veyker", hashSHA256("Barrionuevo")),
            Usuario("Joffre", hashSHA256("Arias")),
            Usuario("Edgar", hashSHA256("Tipan")),
            Usuario("Angelo", hashSHA256("Pujota")),
            Usuario("Cristian", hashSHA256("Lechon")),
            Usuario("Kevin", hashSHA256("Hurtado"))
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
                onLogout = { navController.popBackStack("login", inclusive = false) },
                onAddVehiculo = { navController.navigate("addVehiculo") },
                onEditVehiculo = {
                    vehiculoSeleccionado = it
                    navController.navigate("editVehiculo")
                },
                onDeleteVehiculo = { vehiculos.remove(it) },
                onVerUsuarios = { navController.navigate("usuariosCifrados") }
            )
        }

        composable("addVehiculo") {
            AddVehiculoScreen(
                onSave = {
                    vehiculos.add(it)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("editVehiculo") {
            vehiculoSeleccionado?.let {
                EditVehiculoScreen(
                    vehiculo = it,
                    onSave = { editado ->
                        val index = vehiculos.indexOfFirst { v -> v.placa == it.placa }
                        if (index != -1) vehiculos[index] = editado
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }

        composable("usuariosCifrados") {
            UsuariosCifradosScreen(
                usuarios = usuarios,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
