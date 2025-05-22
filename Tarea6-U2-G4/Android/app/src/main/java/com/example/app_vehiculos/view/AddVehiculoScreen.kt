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

    val isPlacaValid = placa.isNotBlank()
    val isMarcaValid = marca.isNotBlank()
    val isAnioValid = anio.toIntOrNull() != null
    val isColorValid = color.isNotBlank()
    val isCostoValid = costoPorDia.toDoubleOrNull()?.let { it > 0 } == true
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
            onValueChange = { placa = it },
            label = { Text("Placa") },
            isError = showErrors && !isPlacaValid,
            supportingText = { if (showErrors && !isPlacaValid) Text("La placa es obligatoria") }
        )

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            isError = showErrors && !isMarcaValid,
            supportingText = { if (showErrors && !isMarcaValid) Text("La marca es obligatoria") }
        )

        OutlinedTextField(
            value = anio,
            onValueChange = { anio = it },
            label = { Text("Año") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = showErrors && !isAnioValid,
            supportingText = { if (showErrors && !isAnioValid) Text("Debe ingresar un año válido") }
        )

        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color") },
            isError = showErrors && !isColorValid,
            supportingText = { if (showErrors && !isColorValid) Text("El color es obligatorio") }
        )

        OutlinedTextField(
            value = costoPorDia,
            onValueChange = { costoPorDia = it },
            label = { Text("Costo por día") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = showErrors && !isCostoValid,
            supportingText = { if (showErrors && !isCostoValid) Text("Debe ser un número positivo") }
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