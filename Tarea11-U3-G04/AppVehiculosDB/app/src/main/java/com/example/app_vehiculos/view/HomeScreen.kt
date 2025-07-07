package com.example.app_vehiculos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app_vehiculos.R
import com.example.app_vehiculos.data.local.Vehiculo
import com.example.app_vehiculos.viewmodel.VehiculoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vehiculoViewModel: VehiculoViewModel,
    onLogout: () -> Unit,
    onAddVehiculo: () -> Unit,
    onEditVehiculo: (Vehiculo) -> Unit,
    onVerUsuarios: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vehiculos by vehiculoViewModel.vehiculosState.collectAsState()
    val userMessage by vehiculoViewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userMessage) {
        userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            vehiculoViewModel.onMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
                actions = {
                    Row {
                        IconButton(onClick = {
                            scope.launch {
                                vehiculoViewModel.sincronizarTodo()
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Sincronizar")
                        }
                        TextButton(onClick = onVerUsuarios) {
                            Text("Usuarios")
                        }
                        Button(onClick = onLogout) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddVehiculo) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Vehículo")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(vehiculos) { vehiculo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        val painter = when {
                            vehiculo.imagenUri != null -> rememberAsyncImagePainter(vehiculo.imagenUri)
                            vehiculo.imagenResId != null -> painterResource(id = vehiculo.imagenResId)
                            else -> painterResource(id = R.drawable.preder)
                        }

                        Image(
                            painter = painter,
                            contentDescription = "Imagen de ${vehiculo.marca}",
                            modifier = Modifier.fillMaxWidth().height(180.dp)
                        )

                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Placa: ${vehiculo.placa}", style = MaterialTheme.typography.titleLarge)
                            Text("Marca: ${vehiculo.marca}", style = MaterialTheme.typography.bodyLarge)
                            Text("Año: ${vehiculo.anio}")
                            Text("Color: ${vehiculo.color}")
                            Text("Costo/día: $${"%.2f".format(vehiculo.costoPorDia)}")
                            Text("Activo: ${if (vehiculo.activo) "Sí" else "No"}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedButton(onClick = { onEditVehiculo(vehiculo) }) {
                                    Text("Editar")
                                }
                                OutlinedButton(onClick = { vehiculoViewModel.eliminarVehiculo(vehiculo) }) {
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