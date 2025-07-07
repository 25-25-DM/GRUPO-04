// En: view/EditVehiculoScreen.kt
package com.example.app_vehiculos.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var imagenSeleccionada by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val imagenes = mapOf(
        "toyota" to R.drawable.toyota,
        "chevrolet" to R.drawable.chevrolet,
        "nissan" to R.drawable.nissan,
        "hyundai" to R.drawable.hyundai,
        "mazda" to R.drawable.mazda,
        "default" to R.drawable.preder
    )

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
            .background(Color(0xFF222222))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Editar Vehículo",
                    style = TextStyle(
                        fontSize = 30.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                OutlinedTextField(
                    value = vehiculo.placa,
                    onValueChange = { /* No hacer nada */ },
                    label = { Text("Placa (no editable)") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it },
                    label = { Text("Marca") },
                    isError = showErrors && !isMarcaValid,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        if (showErrors && !isMarcaValid) Text(
                            "Solo letras",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = anio,
                    onValueChange = { if (it.length <= 4) anio = it.filter { c -> c.isDigit() } },
                    label = { Text("Año") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showErrors && !isAnioValid,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        if (showErrors && !isAnioValid) Text(
                            "Año entre 1950 y 2025",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") },
                    isError = showErrors && !isColorValid,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (showErrors && !isColorValid) Text(
                            "Solo letras",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = costoPorDia,
                    onValueChange = { costoPorDia = it.replace("[^\\d.]".toRegex(), "") },
                    label = { Text("Costo por día") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = showErrors && !isCostoValid,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        if (showErrors && !isCostoValid) Text(
                            "Número positivo hasta 200",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = activo,
                        onCheckedChange = { activo = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF09c292),
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        )
                    )
                    Text("¿Activo?", color = Color.White)
                }

                Text(
                    "Agregar Imágenes",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color(0xFFc3cad2)
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6480aa),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Seleccionar de la galería", fontWeight = FontWeight.Bold)
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = imagenSeleccionada.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("O elegir imagen precargada") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        colors = outlinedTextFieldColors(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
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

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val painter = when {
                        imagenUri != null -> rememberAsyncImagePainter(imagenUri)
                        imagenSeleccionada.isNotEmpty() -> painterResource(id = imagenes[imagenSeleccionada] ?: R.drawable.preder)
                        vehiculo.imagenUri != null -> rememberAsyncImagePainter(vehiculo.imagenUri)
                        vehiculo.imagenResId != null -> painterResource(id = vehiculo.imagenResId)
                        else -> painterResource(id = R.drawable.preder)
                    }
                    Image(
                        painter = painter,
                        contentDescription = "Imagen del vehículo",
                        modifier = Modifier.size(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
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
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF09c292),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFff6d68),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "Cancelar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}