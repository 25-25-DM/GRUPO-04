package com.example.app_vehiculos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.app_vehiculos.function.hashSHA256
import com.example.app_vehiculos.model.Usuario

@Composable
fun RegisterScreen(
    usuarios: List<Usuario>,
    onRegisterSuccess: (Usuario) -> Unit,
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
        Text("Registro", style = MaterialTheme.typography.headlineMedium)
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
                val hashedApellido = hashSHA256(apellidoTxt)

                if (usuarios.any {
                        it.nombre.equals(nombreTxt, ignoreCase = true) &&
                                it.apellido == hashedApellido
                    }) {
                    errorMsg = "Este usuario ya está registrado"
                } else {
                    errorMsg = ""
                    onRegisterSuccess(Usuario(nombreTxt, hashedApellido))
                }
            }
        }) {
            Text("Registrarse")
        }

        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }
}
