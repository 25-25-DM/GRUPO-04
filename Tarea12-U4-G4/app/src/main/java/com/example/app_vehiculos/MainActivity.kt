package com.example.app_vehiculos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.app_vehiculos.controller.AppNavigation
import com.example.app_vehiculos.ui.theme.App_VehiculosTheme
import com.example.app_vehiculos.viewmodel.LoginViewModel
import com.example.app_vehiculos.viewmodel.LoginViewModelFactory
import com.example.app_vehiculos.viewmodel.VehiculoViewModel
import com.example.app_vehiculos.viewmodel.VehiculoViewModelFactory
import com.example.app_vehiculos.notifications.NotificacionesChannel

class MainActivity : ComponentActivity() {
    private val vehiculoViewModel: VehiculoViewModel by viewModels {
        VehiculoViewModelFactory((application as VehiculosApp).repository)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as VehiculosApp).repository)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pedir permiso si es Android 13 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        NotificacionesChannel.crearCanales(this)
        setContent {
            App_VehiculosTheme {
                AppNavigation(vehiculoViewModel, loginViewModel)
            }
        }
    }
}