package com.example.app_vehiculos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
) {
    val vehiculos by vehiculoViewModel.vehiculosState.collectAsState()
    val userMessage by vehiculoViewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vehiculoViewModel.migrarImagenes()
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            vehiculoViewModel.onMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFF222222),
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2A2A2A),
                    titleContentColor = Color.White
                ),
                actions = {
                    Row {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    vehiculoViewModel.sincronizarTodo(context)
                                }
                            }, modifier = Modifier
                                .background(Color(0xFF6480aa), shape = RoundedCornerShape(6.dp))
                                .width(50.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh, contentDescription = "Sincronizar",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            onClick = onVerUsuarios,
                            modifier = Modifier
                                .background(Color(0xFF09c292), shape = RoundedCornerShape(6.dp))
                                .width(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Usuarios registrados",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .background(Color(0xFFff6964), shape = RoundedCornerShape(6.dp))
                                .width(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            )
        },


        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddVehiculo,
                containerColor = Color(0xFF434c62),
                modifier = Modifier
                    .background(Color.Transparent)
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = "Añadir Vehículo",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(vehiculos) { vehiculo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF333333)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Imagen
                        val painter = when {
                            !vehiculo.imagenUri.isNullOrEmpty() -> rememberAsyncImagePainter(
                                vehiculo.imagenUri
                            )

                            else -> getSafePainterResource(vehiculo.imagenResId)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(0.dp, Color.Transparent, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = "Imagen de ${vehiculo.marca}",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Título
                        Text(
                            text = "${vehiculo.marca} - ${vehiculo.placa}",
                            fontSize = 22.sp,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Contenido y botones
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Columna de textos
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${vehiculo.anio}, ${vehiculo.color}",
                                    fontSize = 18.sp,
                                    color = Color(0xFFc6cdd4)
                                ) //Año y Color
                                Text(
                                    text = "$${"%.2f".format(vehiculo.costoPorDia)}/dia",
                                    fontSize = 18.sp,
                                    color = Color(0xFFc6cdd4)
                                )
                                Text(
                                    text = if (vehiculo.activo) "Activo" else "Inactivo",
                                    fontSize = 18.sp,
                                    color = if (vehiculo.activo) Color(0xFF09c292) else Color(
                                        0xFFe04e4e
                                    ),
                                    fontStyle = FontStyle.Italic,
                                )
                            }

                            // Columna de botones
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                OutlinedButton(
                                    onClick = { onEditVehiculo(vehiculo) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF6a6a6a),
                                        contentColor = Color.White
                                    ),
                                    border = null,
                                    modifier = Modifier
                                        .width(120.dp),
                                ) {
                                    Text(text = "Editar", fontSize = 16.sp)
                                }

                                OutlinedButton(
                                    onClick = { vehiculoViewModel.eliminarVehiculo(context,vehiculo) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFff6d68),
                                        contentColor = Color.White
                                    ),
                                    border = null,
                                    modifier = Modifier
                                        .width(120.dp),
                                ) {
                                    Text(text = "Eliminar", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getSafePainterResource(resId: Int?): Painter {
    val context = LocalContext.current
    return if (resId != null && resId != 0) {
        val typeName = try {
            context.resources.getResourceTypeName(resId)
        } catch (e: Exception) {
            null
        }
        if (typeName == "drawable") {
            painterResource(id = resId)
        } else {
            painterResource(id = R.drawable.preder)
        }
    } else {
        painterResource(id = R.drawable.preder)
    }
}