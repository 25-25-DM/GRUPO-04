package com.example.app_vehiculos.view

import android.annotation.SuppressLint
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app_vehiculos.R
import com.example.app_vehiculos.data.local.Vehiculo

@SuppressLint("UseKtx")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVehiculoScreen(
    vehiculo: Vehiculo,
    onSave: (Vehiculo) -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()

    var marca by rememberSaveable { mutableStateOf(vehiculo.marca) }
    var anio by rememberSaveable { mutableStateOf(vehiculo.anio.toString()) }
    var color by rememberSaveable { mutableStateOf(vehiculo.color) }
    var costoPorDia by rememberSaveable { mutableStateOf(vehiculo.costoPorDia.toString()) }
    var activo by rememberSaveable { mutableStateOf(vehiculo.activo) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    var imagenUri by rememberSaveable { mutableStateOf(vehiculo.imagenUri?.let { Uri.parse(it) }) }

    val imagenes = mapOf(
        "toyota" to R.drawable.toyota,
        "chevrolet" to R.drawable.chevrolet,
        "nissan" to R.drawable.nissan,
        "hyundai" to R.drawable.hyundai,
        "mazda" to R.drawable.mazda,
        "default" to R.drawable.preder
    )

    var imagenSeleccionada by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagenUri = uri
            imagenSeleccionada = ""
        }
    }

    val soloLetrasRegex = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$".toRegex()
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
        Text("Editar Vehículo", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = vehiculo.placa,
            onValueChange = {  },
            label = { Text("Placa (no editable)") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, isError = showErrors && !isMarcaValid, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = anio, onValueChange = { anio = it.filter { c -> c.isDigit() } }, label = { Text("Año") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = showErrors && !isAnioValid, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, isError = showErrors && !isColorValid, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = costoPorDia, onValueChange = { costoPorDia = it }, label = { Text("Costo por día") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), isError = showErrors && !isCostoValid, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = activo, onCheckedChange = { activo = it })
            Text("¿Activo?")
        }

        Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Seleccionar de Galería") }

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = imagenSeleccionada.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                onValueChange = {},
                readOnly = true,
                label = { Text("O elegir imagen precargada") },
                modifier = Modifier.menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                imagenes.keys.forEach { nombre ->
                    DropdownMenuItem(
                        text = { Text(nombre.replaceFirstChar { it.titlecase() }) },
                        onClick = {
                            imagenSeleccionada = nombre
                            imagenUri = null
                            expanded = false
                        }
                    )
                }
            }
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            val painter = when {
                imagenUri != null -> rememberAsyncImagePainter(imagenUri)
                imagenSeleccionada.isNotEmpty() -> painterResource(id = imagenes[imagenSeleccionada] ?: R.drawable.preder)
                vehiculo.imagenUri != null -> rememberAsyncImagePainter(vehiculo.imagenUri)
                vehiculo.imagenResId != null -> painterResource(id = vehiculo.imagenResId)
                else -> painterResource(id = R.drawable.preder)
            }
            Image(painter = painter, contentDescription = "Imagen del vehículo", modifier = Modifier.size(200.dp))
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                showErrors = true
                if (isMarcaValid && isAnioValid && isColorValid && isCostoValid) {
                    onSave(
                        Vehiculo(
                            placa = vehiculo.placa,
                            marca = marca,
                            anio = anio.toInt(),
                            color = color,
                            costoPorDia = costoPorDia.toDouble(),
                            activo = activo,
                            imagenUri = if (imagenUri != null) imagenUri.toString() else if (imagenSeleccionada.isEmpty()) vehiculo.imagenUri else null,
                            imagenResId = if (imagenUri == null && imagenSeleccionada.isNotEmpty()) imagenes[imagenSeleccionada] else if (imagenUri == null && imagenSeleccionada.isEmpty()) vehiculo.imagenResId else null
                        )
                    )
                }
            }) { Text("Guardar Cambios") }

            OutlinedButton(onClick = onCancel) { Text("Cancelar") }
        }
    }
}