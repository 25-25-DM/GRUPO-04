package com.example.app_vehiculos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_vehiculos.viewmodel.LoginResult

@Composable
fun LoginScreen(
    loginState: LoginResult,
    onLogin: (String, String) -> Unit,
    onGoToRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isPasswordFieldFocused by remember { mutableStateOf(false) }

    val iconColor = if (isPasswordFieldFocused) Color(0xFF91a0b4) else Color.Gray
    val errorMsg = if (loginState is LoginResult.Error) loginState.message else ""

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Iniciar sesión",
                    style = TextStyle(
                        fontSize = 30.sp,
                        color = Color.White,
                    )
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    colors = outlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        val description =
                            if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = description,
                                tint = iconColor
                            )
                        }
                    },
                    colors = outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isPasswordFieldFocused = focusState.isFocused
                        },
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Button(
                    onClick = { onLogin(nombre.text.trim(), contrasena.text.trim()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF09c292),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = loginState !is LoginResult.Loading
                ) {
                    if (loginState is LoginResult.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF09c292)
                        )
                    } else {
                        Text("Ingresar", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { onGoToRegister() }) {
                        Text(
                            "¿Sin cuenta? ¡Regístrate aquí!",
                            color = Color(0xFFB0BEC5),
                            style = TextStyle(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                }

                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun outlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF91a0b4),
        unfocusedBorderColor = Color.Gray,
        cursorColor = Color(0xFF91a0b4),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = Color(0xFF91a0b4),
        unfocusedLabelColor = Color.Gray,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )
}
