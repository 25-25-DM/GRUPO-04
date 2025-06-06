package com.example.marsphotos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marsphotos.R
import com.example.marsphotos.function.hashSHA256
import com.example.marsphotos.model.Usuario

val Type = FontFamily(
    Font(R.font.jetbrainsmono, FontWeight.Normal, FontStyle.Normal)
)

@Composable
fun LoginScreen(
    usuariosRegistrados: List<Usuario>,
    onLoginSuccess: (String, String) -> Unit,
    onGoToRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Iniciar Sesión",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = Type,
            )
        )
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la app",
            modifier = Modifier.size(220.dp)
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Contraseña (apellido)") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon =
                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description =
                    if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val nombreTxt = nombre.text.trim()
            val apellidoTxt = apellido.text.trim()

            if (nombreTxt.isEmpty() || apellidoTxt.isEmpty()) {
                errorMsg = "Todos los campos son obligatorios"
            } else {
                val hashIngresado = hashSHA256(apellidoTxt)
                val usuarioEncontrado = usuariosRegistrados.find {
                    it.nombre.equals(nombreTxt, ignoreCase = true) &&
                            it.apellidoHash == hashIngresado
                }
                if (usuarioEncontrado != null) {
                    errorMsg = ""
                    onLoginSuccess(nombreTxt, apellidoTxt)
                } else {
                    errorMsg = "Credenciales incorrectas"
                }
            }
        }) {
            Text("Ingresar")
        }

        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }
}