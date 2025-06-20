package com.example.app_vehiculos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.app_vehiculos.controller.AppNavigation
import com.example.app_vehiculos.ui.theme.App_VehiculosTheme
import com.example.app_vehiculos.viewmodel.LoginViewModel
import com.example.app_vehiculos.viewmodel.LoginViewModelFactory
import com.example.app_vehiculos.viewmodel.VehiculoViewModel
import com.example.app_vehiculos.viewmodel.VehiculoViewModelFactory

class MainActivity : ComponentActivity() {

    private val vehiculoViewModel: VehiculoViewModel by viewModels {
        VehiculoViewModelFactory((application as VehiculosApp).repository)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as VehiculosApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App_VehiculosTheme {
                AppNavigation(vehiculoViewModel, loginViewModel)
            }
        }
    }
}