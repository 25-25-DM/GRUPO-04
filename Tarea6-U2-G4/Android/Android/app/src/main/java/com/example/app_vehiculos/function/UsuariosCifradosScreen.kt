package com.example.app_vehiculos.function

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_vehiculos.model.Usuario
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun UsuariosCifradosScreen(
    usuarios: List<Usuario>,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Usuarios Registrados (Apellidos Cifrados)", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(usuarios) {
                Text("Nombre: ${it.nombre}, Apellido (cifrado): ${it.apellido}")
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}