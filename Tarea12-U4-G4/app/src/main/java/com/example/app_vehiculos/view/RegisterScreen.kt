package com.example.app_vehiculos.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_vehiculos.viewmodel.RegisterResult

@Composable
fun RegisterScreen(
    registerState: RegisterResult,
    onRegister: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var contrasena by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isPasswordFieldFocused by remember { mutableStateOf(false) }

    val iconColor = if (isPasswordFieldFocused) Color(0xFF91a0b4) else Color.Gray
    val errorMsg = if (registerState is RegisterResult.Error) registerState.message else ""

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
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Registrar un nuevo usuario",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.White,
                    )
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    colors = outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
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
                    onClick = {
                        if (registerState !is RegisterResult.Loading) {
                            onRegister(nombre.text.trim(), contrasena.text.trim())
                        }
                    },
                    enabled = registerState !is RegisterResult.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF09c292),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (registerState is RegisterResult.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF91a0b4)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF91a0b4)),
                ) {
                    Text("Cancelar", fontSize = 20.sp)
                }

                if (errorMsg.isNotEmpty()) {
                    Text(
                        errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}