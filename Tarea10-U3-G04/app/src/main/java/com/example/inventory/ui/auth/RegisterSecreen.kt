package com.example.inventory.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.R
import com.example.inventory.data.Usuario
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object RegisterDestination : NavigationDestination {
    override val route = "register"
    override val titleRes = R.string.app_name
    val groupRes = R.string.group_name
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var errorMsg by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Registro",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = Type,
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido (contraseña)") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val nombreTxt = nombre.text.trim()
            val apellidoTxt = apellido.text.trim()

            if (nombreTxt.isEmpty() || apellidoTxt.isEmpty()) {
                errorMsg = "Todos los campos son obligatorios"
            } else {
                coroutineScope.launch {
                    val valido = viewModel.verificarUser(nombreTxt, apellidoTxt)
                    if (valido) {
                        errorMsg = "Este usuario ya está registrado"
                    } else {
                        errorMsg = "Usuario registrado correctamente"
                        viewModel.registrarUsuarios(nombreTxt,apellidoTxt)
                    }
                }
            }
        }) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Regresar")
        }


        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }
}