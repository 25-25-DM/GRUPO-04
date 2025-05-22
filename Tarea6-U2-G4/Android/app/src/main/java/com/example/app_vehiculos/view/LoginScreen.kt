package com.example.app_vehiculos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.app_vehiculos.R
import com.example.app_vehiculos.model.Usuario

@Composable
fun LoginScreen(
    usuariosRegistrados: List<Usuario>,
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la aplicación",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 24.dp)
        )

        Text("Login", style = MaterialTheme.typography.displaySmall)
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
            label = { Text("Apellido") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val user = usuariosRegistrados.find {
                it.nombre.equals(nombre.text.trim(), ignoreCase = true) &&
                        it.apellido.equals(apellido.text.trim(), ignoreCase = true)
            }

            if (user != null) {
                errorMsg = ""
                onLoginSuccess()
            } else {
                errorMsg = "Credenciales incorrectas"
            }
        }) {
            Text("Ingresar")
        }

        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }

        TextButton(onClick = onGoToRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
