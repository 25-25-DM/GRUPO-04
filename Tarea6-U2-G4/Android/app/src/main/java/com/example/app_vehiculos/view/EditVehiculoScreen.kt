package com.example.app_vehiculos.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app_vehiculos.R
import com.example.app_vehiculos.model.Vehiculo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVehiculoScreen(
    vehiculo: Vehiculo,
    onSave: (Vehiculo) -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()
    var placa by remember { mutableStateOf(vehiculo.placa) }
    var marca by remember { mutableStateOf(vehiculo.marca) }
    var anio by remember { mutableStateOf(vehiculo.anio.toString()) }
    var color by remember { mutableStateOf(vehiculo.color) }
    var costoPorDia by remember { mutableStateOf(vehiculo.costoPorDia.toString()) }
    var activo by remember { mutableStateOf(vehiculo.activo) }

    var imagenUri by remember { mutableStateOf(vehiculo.imagenUri?.let { Uri.parse(it) }) }

    val imagenes = mapOf(
        "toyota" to R.drawable.toyota,
        "chevrolet" to R.drawable.chevrolet,
        "nissan" to R.drawable.nissan,
        "hyundai" to R.drawable.hyundai,
        "mazda" to R.drawable.mazda,
        "preder" to R.drawable.preder
    )

    var imagenSeleccionada by remember {
        mutableStateOf(
            when {
                vehiculo.imagenUri != null -> ""
                vehiculo.imagenResId == R.drawable.toyota -> "toyota"
                vehiculo.imagenResId == R.drawable.chevrolet -> "chevrolet"
                vehiculo.imagenResId == R.drawable.nissan -> "nissan"
                vehiculo.imagenResId == R.drawable.hyundai -> "hyundai"
                vehiculo.imagenResId == R.drawable.mazda -> "mazda"
                vehiculo.imagenResId == R.drawable.preder -> "preder"
                else -> "preder"
            }
        )
    }

    var expanded by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagenUri = uri
            imagenSeleccionada = ""
        }
    }

    val imagenMaxSize = 200.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Editar Vehículo", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = placa, onValueChange = { placa = it }, label = { Text("Placa") })
        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") })
        OutlinedTextField(
            value = anio,
            onValueChange = { anio = it },
            label = { Text("Año") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") })
        OutlinedTextField(
            value = costoPorDia,
            onValueChange = { costoPorDia = it },
            label = { Text("Costo por día") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = activo, onCheckedChange = { activo = it })
            Text("¿Activo?")
        }

        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Seleccionar imagen de galería")
        }

        if (imagenUri == null) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = imagenSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Imagen precargada") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    imagenes.keys.forEach { nombre ->
                        DropdownMenuItem(
                            text = { Text(nombre.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                imagenSeleccionada = nombre
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            when {
                imagenUri != null -> Image(
                    painter = rememberAsyncImagePainter(imagenUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.size(imagenMaxSize)
                )
                imagenSeleccionada.isNotEmpty() -> Image(
                    painter = painterResource(id = imagenes[imagenSeleccionada] ?: R.drawable.preder),
                    contentDescription = "Imagen precargada",
                    modifier = Modifier.size(imagenMaxSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                val imagenResId = if (imagenUri == null && imagenSeleccionada.isNotEmpty()) {
                    imagenes[imagenSeleccionada] ?: R.drawable.preder
                } else null

                onSave(
                    Vehiculo(
                        placa = placa,
                        marca = marca,
                        anio = anio.toIntOrNull() ?: 0,
                        color = color,
                        costoPorDia = costoPorDia.toDoubleOrNull() ?: 0.0,
                        activo = activo,
                        imagenResId = imagenResId,
                        imagenUri = imagenUri?.toString()
                    )
                )
            }) {
                Text("Guardar")
            }

            OutlinedButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}