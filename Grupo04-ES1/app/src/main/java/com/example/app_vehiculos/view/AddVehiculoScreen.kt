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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app_vehiculos.R
import com.example.app_vehiculos.model.Vehiculo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehiculoScreen(
    onSave: (Vehiculo) -> Unit,
    onCancel: () -> Unit,
    vehiculoAEditar: Vehiculo? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var placa by remember { mutableStateOf(vehiculoAEditar?.placa ?: "") }
    var marca by remember { mutableStateOf(vehiculoAEditar?.marca ?: "") }
    var anio by remember { mutableStateOf(vehiculoAEditar?.anio?.toString() ?: "") }
    var color by remember { mutableStateOf(vehiculoAEditar?.color ?: "") }
    var costoPorDia by remember { mutableStateOf(vehiculoAEditar?.costoPorDia?.toString() ?: "") }
    var activo by remember { mutableStateOf(vehiculoAEditar?.activo ?: true) }
    var showErrors by remember { mutableStateOf(false) }

    var imagenUri by remember {
        mutableStateOf(vehiculoAEditar?.imagenUri?.let { Uri.parse(it) })
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagenUri = uri
        }
    }

    val placaRegex = "^[A-Z]{0,3}\\d{0,3}$".toRegex() // permite que el usuario escriba progresivamente
    val placaFullRegex = "^[A-Z]{3}\\d{3}$".toRegex()
    val soloLetrasRegex = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]*$".toRegex()

    val isPlacaValid = placaFullRegex.matches(placa)
    val isMarcaValid = marca.isNotBlank() && marca.matches(soloLetrasRegex)
    val anioInt = anio.toIntOrNull()
    val isAnioValid = anioInt != null && anioInt in 1950..2025
    val isColorValid = color.isNotBlank() && color.matches(soloLetrasRegex)
    val costoDouble = costoPorDia.toDoubleOrNull()
    val isCostoValid = costoDouble != null && costoDouble > 0 && costoDouble <= 200

    val imagenMaxSize = 200.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            if (vehiculoAEditar == null) "Nuevo Vehículo" else "Editar Vehículo",
            style = MaterialTheme.typography.headlineSmall
        )

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
            Text("Seleccionar imagen")
        }

        imagenUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .size(imagenMaxSize)
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                showErrors = true
                if (isPlacaValid && isMarcaValid && isAnioValid && isColorValid && isCostoValid) {
                    onSave(
                        Vehiculo(
                            placa = placa,
                            marca = marca,
                            anio = anio.toInt(),
                            color = color,
                            costoPorDia = costoPorDia.toDouble(),
                            activo = activo,
                            imagenResId = if (imagenUri == null && vehiculoAEditar?.imagenUri == null) R.drawable.toyota else null,
                            imagenUri = imagenUri?.toString() ?: vehiculoAEditar?.imagenUri
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