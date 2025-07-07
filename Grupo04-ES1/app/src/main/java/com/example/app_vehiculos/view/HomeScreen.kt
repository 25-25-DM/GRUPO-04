package com.example.app_vehiculos.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import com.example.app_vehiculos.model.Vehiculo
import com.example.app_vehiculos.R

@Composable
fun HomeScreen(
    vehiculos: List<Vehiculo>,
    onLogout: () -> Unit,
    onAddVehiculo: () -> Unit,
    onEditVehiculo: (Vehiculo) -> Unit,
    onDeleteVehiculo: (Vehiculo) -> Unit,
    onVerUsuarios: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Inicio", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onAddVehiculo) {
                Text("Agregar Vehículo")
            }
            Button(onClick = onLogout) {
                Text("Cerrar sesión")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onVerUsuarios,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Ver Usuarios Cifrados")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(vehiculos) { vehiculo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        val painter = when {
                            vehiculo.imagenUri != null -> rememberAsyncImagePainter(vehiculo.imagenUri)
                            vehiculo.imagenResId != null -> painterResource(id = vehiculo.imagenResId)
                            else -> painterResource(id = R.drawable.toyota)
                        }

                        Image(
                            painter = painter,
                            contentDescription = "Imagen de ${vehiculo.marca}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Placa: ${vehiculo.placa}")
                            Text("Marca: ${vehiculo.marca}")
                            Text("Año: ${vehiculo.anio}")
                            Text("Color: ${vehiculo.color}")
                            Text("Costo/día: $${vehiculo.costoPorDia}")
                            Text("Activo: ${if (vehiculo.activo) "Sí" else "No"}")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedButton(onClick = { onEditVehiculo(vehiculo) }) {
                                    Text("Editar")
                                }

                                OutlinedButton(onClick = { onDeleteVehiculo(vehiculo) }) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}