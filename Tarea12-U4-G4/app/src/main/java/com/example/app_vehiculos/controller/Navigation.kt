package com.example.app_vehiculos.controller

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.app_vehiculos.function.UsuariosCifradosScreen
import com.example.app_vehiculos.view.*
import com.example.app_vehiculos.viewmodel.LoginResult
import com.example.app_vehiculos.viewmodel.LoginViewModel
import com.example.app_vehiculos.viewmodel.RegisterResult
import com.example.app_vehiculos.viewmodel.VehiculoViewModel

@Composable
fun AppNavigation(
    vehiculoViewModel: VehiculoViewModel,
    loginViewModel: LoginViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            val loginState by loginViewModel.loginState.collectAsState()
            LoginScreen(
                loginState = loginState,
                onLogin = { user, pass -> loginViewModel.login(user, pass) },
                onGoToRegister = { navController.navigate("register") }
            )
            LaunchedEffect(loginState) {
                if (loginState is LoginResult.Success) {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                    loginViewModel.resetLoginState()
                }
            }
        }

        composable("register") {
            val registerState by loginViewModel.registerState.collectAsState()
            val context = LocalContext.current

            RegisterScreen(
                registerState = registerState,
                onRegister = { nombre, contrasena ->
                    loginViewModel.register(nombre, contrasena, context)
                },
                onBack = { navController.popBackStack() }
            )

            LaunchedEffect(registerState) {
                if (registerState is RegisterResult.Success) {
                    Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                    loginViewModel.resetRegisterState()
                }
            }
        }

        composable("home") {
            HomeScreen(
                vehiculoViewModel = vehiculoViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onAddVehiculo = { navController.navigate("addVehiculo") },
                onEditVehiculo = { vehiculo ->
                    navController.navigate("editVehiculo/${vehiculo.placa}")
                },
                onVerUsuarios = { navController.navigate("usuariosCifrados") }
            )
        }

        composable("addVehiculo") {
            val context = LocalContext.current
            AddVehiculoScreen(
                onSave = { vehiculo ->
                    vehiculoViewModel.agregarVehiculo(context, vehiculo)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() },
                viewModel = vehiculoViewModel
            )
        }

        composable(
            route = "editVehiculo/{placa}",
            arguments = listOf(navArgument("placa") { type = NavType.StringType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val placa = backStackEntry.arguments?.getString("placa")
            val vehiculos by vehiculoViewModel.vehiculosState.collectAsState()
            val vehiculoAEditar = vehiculos.find { it.placa == placa }

            vehiculoAEditar?.let {
                EditVehiculoScreen(
                    vehiculo = it,
                    onSave = { vehiculoEditado ->
                        vehiculoViewModel.editarVehiculo(context, vehiculoEditado)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }

        composable("usuariosCifrados") {
            val listaUsuarios by loginViewModel.todosLosUsuarios.collectAsState()
            UsuariosCifradosScreen(
                usuarios = listaUsuarios,
                onBack = { navController.popBackStack() }
            )
        }
    }
}