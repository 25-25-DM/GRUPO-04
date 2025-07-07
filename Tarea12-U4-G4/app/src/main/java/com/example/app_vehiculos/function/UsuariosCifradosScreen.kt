// Copia y pega este código completo en tu archivo UsuariosCifradosScreen.kt

package com.example.app_vehiculos.function

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_vehiculos.data.local.Usuario // Asegúrate que el import sea el correcto
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosCifradosScreen(
    // La pantalla ahora solo necesita la lista de usuarios y una acción para volver
    usuarios: List<Usuario>,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = Color(0xFF2A2A2A),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Usuarios Registrados") },
                navigationIcon = {
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF333333),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color(0xFF2A2A2A)),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            // Ya no necesitamos mostrar el "usuario ingresado", esta pantalla es solo para listar.

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(usuarios) { usuario ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = "Usuario: ${usuario.nombre}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                // ¡CORRECCIÓN AQUÍ! Usamos passwordHash
                                text = "Password Hash: ${usuario.passwordHash}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1 // Para que no ocupe mucho espacio
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (usuario.esAdmin) "Administrador" else "Usuario",
                                color = if (usuario.esAdmin) Color(0xFFFFA14E) else Color.Gray,
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6480aa),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Volver", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}