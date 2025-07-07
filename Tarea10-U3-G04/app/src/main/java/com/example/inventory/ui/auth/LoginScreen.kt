package com.example.inventory.ui.auth

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

val Type = FontFamily(
    Font(R.font.jetbrainsmono, FontWeight.Normal, FontStyle.Normal)
)

object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.app_name
    val groupRes = R.string.group_name
}

@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit,
    onGoToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

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
                coroutineScope.launch {
                    val valido = viewModel.validarCredenciales(nombreTxt, apellidoTxt)
                    if (valido != null) {
                        SesionUsuario.usuarioId = valido.id
                        errorMsg = "Iniciando sesión"
                        onLoginSuccess(nombreTxt, apellidoTxt)
                    } else {
                        errorMsg = "Credenciales incorrectas"
                    }
                }
            }
        }) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { onGoToRegister() }) {
            Text("¿No tienes cuenta? Regístrate")
        }

        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }
}

object SesionUsuario {
    var usuarioId: Int? = null
}
