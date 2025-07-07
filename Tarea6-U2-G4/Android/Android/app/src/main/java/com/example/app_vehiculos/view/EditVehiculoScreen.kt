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
    var showErrors by remember { mutableStateOf(false) }

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
    val placaRegex = "^[A-Z]{0,3}\\d{0,3}$".toRegex()
    val placaFullRegex = "^[A-Z]{3}\\d{3}$".toRegex()
    val soloLetrasRegex = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]*$".toRegex()

    val isPlacaValid = placaFullRegex.matches(placa)
    val isMarcaValid = marca.isNotBlank() && marca.matches(soloLetrasRegex)
    val anioInt = anio.toIntOrNull()
    val isAnioValid = anioInt != null && anioInt in 1950..2025
    val isColorValid = color.isNotBlank() && color.matches(soloLetrasRegex)
    val costoDouble = costoPorDia.toDoubleOrNull()
    val isCostoValid = costoDouble != null && costoDouble > 0 && costoDouble <= 200

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Editar Vehículo", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = placa,
            onValueChange = {
                val filtered = it.uppercase().replace("[^A-Z\\d]".toRegex(), "")
                if (placaRegex.matches(filtered) && filtered.length <= 6) {
                    placa = filtered
                }
            },
            label = { Text("Placa") },
            isError = showErrors && !isPlacaValid,
            supportingText = {
                if (showErrors && !isPlacaValid)
                    Text("Formato: 3 letras mayúsculas y 3 números (ej: ABC123)")
            }
        )
        OutlinedTextField(
            value = marca,
            onValueChange = {
                val filtered = it.filter { char -> char.isLetter() || char.isWhitespace() }
                marca = filtered
            },
            label = { Text("Marca") },
            isError = showErrors && !isMarcaValid,
            supportingText = {
                if (showErrors && !isMarcaValid)
                    Text("Solo letras, sin números")
            }
        )
        OutlinedTextField(
            value = anio,
            onValueChange = {
                val filtered = it.filter { char -> char.isDigit() }
                anio = filtered.take(4)
            },
            label = { Text("Año") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = showErrors && !isAnioValid,
            supportingText = {
                if (showErrors && !isAnioValid)
                    Text("Ingrese un año entre 1950 y 2025")
            }
        )
        OutlinedTextField(
            value = color,
            onValueChange = {
                val filtered = it.filter { char -> char.isLetter() || char.isWhitespace() }
                color = filtered
            },
            label = { Text("Color") },
            isError = showErrors && !isColorValid,
            supportingText = {
                if (showErrors && !isColorValid)
                    Text("Solo letras, sin números")
            }
        )
        OutlinedTextField(
            value = costoPorDia,
            onValueChange = {
                val filtered = it.replace("[^\\d.]".toRegex(), "")
                if (filtered.count { ch -> ch == '.' } <= 1) {
                    costoPorDia = filtered
                }
            },
            label = { Text("Costo por día") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = showErrors && !isCostoValid,
            supportingText = {
                if (showErrors && !isCostoValid)
                    Text("Debe ser un número positivo hasta $200")
            }
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
                showErrors = true
                if (isPlacaValid && isMarcaValid && isAnioValid && isColorValid && isCostoValid) {
                    val imagenResId = if (imagenUri == null && imagenSeleccionada.isNotEmpty()) {
                        imagenes[imagenSeleccionada] ?: R.drawable.preder
                    } else null

                    onSave(
                        Vehiculo(
                            placa = placa,
                            marca = marca,
                            anio = anio.toInt(),
                            color = color,
                            costoPorDia = costoPorDia.toDouble(),
                            activo = activo,
                            imagenResId = imagenResId,
                            imagenUri = imagenUri?.toString()
                        )
                    )
                }
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