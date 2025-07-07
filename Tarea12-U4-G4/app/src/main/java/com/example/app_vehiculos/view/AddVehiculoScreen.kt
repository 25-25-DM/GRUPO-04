package com.example.app_vehiculos.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.app_vehiculos.data.local.Vehiculo
import com.example.app_vehiculos.notifications.mostrarNotificacionLocal
import com.example.app_vehiculos.viewmodel.VehiculoViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehiculoScreen(
    onSave: (Vehiculo) -> Unit,
    onCancel: () -> Unit,
    viewModel: VehiculoViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var placa by rememberSaveable { mutableStateOf("") }
    var marca by rememberSaveable { mutableStateOf("") }
    var anio by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var costoPorDia by rememberSaveable { mutableStateOf("") }
    var activo by rememberSaveable { mutableStateOf(true) }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var imagenUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var pendingCameraAction by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingCameraAction = true
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imagenUri = cameraImageUri
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenUri = uri
        }
    }

    LaunchedEffect(pendingCameraAction) {
        if (pendingCameraAction) {
            val photoUri = createImageUri(context)
            cameraImageUri = photoUri
            takePictureLauncher.launch(photoUri)
            pendingCameraAction = false
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
                    "Nuevo Vehículo",
                    style = TextStyle(
                        fontSize = 30.sp,
                        color = Color.White
                    )
                )

                OutlinedTextField(
                    value = placa,
                    onValueChange = {
                        if (it.length <= 6) placa =
                            it.uppercase().replace("[^A-Z\\d]".toRegex(), "")
                    },
                    label = { Text("Placa") },
                    isError = showErrors && !isPlacaValid,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        if (showErrors && !isPlacaValid) Text(
                            "Formato: 3 letras y 3 números (ej: ABC123)",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
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
                    onValueChange = {
                        if (it.length <= 4) anio = it.filter { char -> char.isDigit() }
                    },
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
                    singleLine = true,
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
                    IconButton(
                        onClick = {
                            when {
                                ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    pendingCameraAction = true
                                }

                                else -> {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        },
                        modifier = Modifier
                            .background(Color(0xFF6480aa), shape = RoundedCornerShape(10.dp))
                            .width(50.dp)
                            .height(50.dp)
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera, contentDescription = "Tomar foto",
                            tint = Color.White
                        )
                    }

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
                        Text("Seleccionar de la galería")
                    }
                }

                imagenUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(top = 8.dp)
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
                            if (isPlacaValid && isMarcaValid && isAnioValid && isColorValid && isCostoValid) {
                                val vehiculo = Vehiculo(
                                    placa = placa,
                                    marca = marca,
                                    anio = anio.toInt(),
                                    color = color,
                                    costoPorDia = costoPorDia.toDouble(),
                                    activo = activo,
                                    imagenResId = null,
                                    imagenUri = imagenUri?.toString()
                                )
                                viewModel.agregarVehiculoConImagen(
                                    context,
                                    vehiculo,
                                    imagenUri?.toString()
                                )
                                onSave(vehiculo)
                                mostrarNotificacionLocal(
                                    context,
                                    "Vehículo añadido",
                                    "El vehículo se ha registrado correctamente"
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
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFff6d68),
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

fun createImageUri(context: Context): Uri {
    val image = File.createTempFile("vehiculo_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        image
    )
}