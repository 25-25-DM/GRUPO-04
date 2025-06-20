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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app_vehiculos.data.local.Vehiculo

@SuppressLint("UseKtx")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehiculoScreen(
    onSave: (Vehiculo) -> Unit,
    onCancel: () -> Unit,
) {
    val scrollState = rememberScrollState()

    var placa by rememberSaveable { mutableStateOf("") }
    var marca by rememberSaveable { mutableStateOf("") }
    var anio by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var costoPorDia by rememberSaveable { mutableStateOf("") }
    var activo by rememberSaveable { mutableStateOf(true) }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var imagenUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imagenUri = uri
            }
        }

    val placaRegex = "^[A-Z]{3}\\d{3}$".toRegex()
    val soloLetrasRegex = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$".toRegex()

    val isPlacaValid = placaRegex.matches(placa)
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
        Text("Nuevo Vehículo", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = placa,
            onValueChange = { if (it.length <= 6) placa = it.uppercase().replace("[^A-Z\\d]".toRegex(), "") },
            label = { Text("Placa") },
            isError = showErrors && !isPlacaValid,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { if (showErrors && !isPlacaValid) Text("Formato: 3 letras y 3 números (ej: ABC123)") }
        )
        // ... (otros OutlinedTextFields se quedan igual) ...
        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            isError = showErrors && !isMarcaValid,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { if (showErrors && !isMarcaValid) Text("Solo letras") }
        )
        OutlinedTextField(
            value = anio,
            onValueChange = { if (it.length <= 4) anio = it.filter { char -> char.isDigit() } },
            label = { Text("Año") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && !isAnioValid,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { if (showErrors && !isAnioValid) Text("Año entre 1950 y 2025") }
        )
        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color") },
            isError = showErrors && !isColorValid,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { if (showErrors && !isColorValid) Text("Solo letras") }
        )
        OutlinedTextField(
            value = costoPorDia,
            onValueChange = { costoPorDia = it.replace("[^\\d.]".toRegex(), "") },
            label = { Text("Costo por día") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = showErrors && !isCostoValid,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { if (showErrors && !isCostoValid) Text("Número positivo hasta 200") }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = activo, onCheckedChange = { activo = it })
            Text("¿Activo?")
        }
        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Seleccionar imagen")
        }
        imagenUri?.let {
            Image(painter = rememberAsyncImagePainter(it), contentDescription = "Imagen seleccionada", modifier = Modifier.size(200.dp).padding(top = 8.dp))
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
                            imagenResId = null,
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
    }
}