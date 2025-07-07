package com.example.app_vehiculos.repositoty

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.example.app_vehiculos.AwsConfig
import com.example.app_vehiculos.data.local.Usuario
import com.example.app_vehiculos.data.local.UsuarioDao
import com.example.app_vehiculos.data.local.Vehiculo
import com.example.app_vehiculos.data.local.VehiculoDao
import com.example.app_vehiculos.notifications.mostrarNotificacionLocal
import com.example.app_vehiculos.util.ConnectivityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.model.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.net.UnknownHostException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AppRepository(
    private val usuarioDao: UsuarioDao,
    private val vehiculoDao: VehiculoDao,
    private val context: Context
) {
    private val dynamoDb = AwsConfig.dynamoDbClient
    private val vehiculosTableName = "Vehiculos"
    private val usuariosTableName = "Usuarios"
    private val connectivityHelper = ConnectivityHelper(context)

    val todosLosVehiculos: Flow<List<Vehiculo>> = vehiculoDao.getAllVehiculos()
    val todosLosUsuarios: Flow<List<Usuario>> = usuarioDao.getAllUsuarios()

    suspend fun sincronizarAmbasVias(context: Context): Result<Unit> = withContext(Dispatchers.IO) {
        if (!connectivityHelper.isNetworkAvailable()) {
            return@withContext Result.failure(Exception("Sin conexión a internet. No se pudo sincronizar."))
        }
        try {
            Log.i("AppRepository", "Iniciando sincronización bidireccional...")

            migrarImagenesLocalesAS3()

            Log.i("AppRepository", "Subiendo datos locales a DynamoDB...")
            todosLosUsuarios.first().forEach { usuario ->
                val request = PutItemRequest.builder().tableName(usuariosTableName)
                    .item(usuario.toDynamoDbMap()).build()
                dynamoDb.putItem(request)
            }
            todosLosVehiculos.first().forEach { vehiculo ->
                val request = PutItemRequest.builder().tableName(vehiculosTableName)
                    .item(vehiculo.toDynamoDbMap()).build()
                dynamoDb.putItem(request)
            }
            Log.i("AppRepository", "Subida completada.")

            Log.i("AppRepository", "Descargando datos desde DynamoDB...")
            sincronizarTabla(
                "Vehículos",
                vehiculosTableName,
                { it.toVehiculo() },
                { vehiculoDao.insertarVehiculo(it) })
            sincronizarTabla(
                "Usuarios",
                usuariosTableName,
                { it.toUsuario() },
                { usuarioDao.insertarUsuario(it) })

            val totalVehiculos = vehiculoDao.getAllVehiculos().first().size
            val totalUsuarios = usuarioDao.getAllUsuarios().first().size
            mostrarNotificacionLocal(
                context,
                "Sincronización completada",
                "Sincronización con éxito desde la nube, existen ($totalVehiculos) vehículos y ($totalUsuarios) usuarios"
            )

            Log.i("AppRepository", "Sincronización completada.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepository", "Error durante la sincronización bidireccional.", e)
            when (e) {
                is UnknownHostException -> Result.failure(Exception("No se pudo conectar a los servidores de AWS. Revise su conexión."))
                else -> Result.failure(e)
            }
        }
    }

    suspend fun insertarVehiculo(vehiculo: Vehiculo, context: Context): Result<Unit> =
        withContext(Dispatchers.IO) {
            vehiculoDao.insertarVehiculo(vehiculo)
            val totalVehiculos = vehiculoDao.getAllVehiculos().first().size

            if (connectivityHelper.isNetworkAvailable()) {
                val result = sincronizarItemHaciaNube(
                    "vehículo",
                    vehiculosTableName,
                    vehiculo.toDynamoDbMap()
                )
                if (result.isSuccess) {
                    mostrarNotificacionLocal(
                        context,
                        "Vehículo añadido",
                        "Un vehículo añadido con éxito en la nube, en total existen ($totalVehiculos) vehículos"
                    )
                }
                result
            } else {
                mostrarNotificacionLocal(
                    context,
                    "Vehículo añadido",
                    "Un vehículo añadido con éxito localmente, en total existen ($totalVehiculos) vehículos"
                )
                Result.success(Unit)
            }
        }

    suspend fun actualizarVehiculo(vehiculo: Vehiculo, context: Context): Result<Unit> = withContext(Dispatchers.IO) {
        vehiculoDao.actualizarVehiculo(vehiculo)
        mostrarNotificacionLocal(
            context,
            "Vehículo modificado",
            "Se modificó con éxito, vehículo con placas (${vehiculo.placa})"
        )
        sincronizarItemHaciaNube("vehículo", vehiculosTableName, vehiculo.toDynamoDbMap())
    }

    suspend fun eliminarVehiculo(vehiculo: Vehiculo, context: Context): Result<Unit> = withContext(Dispatchers.IO) {
        vehiculoDao.eliminarVehiculo(vehiculo)
        val totalVehiculos = vehiculoDao.getAllVehiculos().first().size

        if (!connectivityHelper.isNetworkAvailable()) {
            mostrarNotificacionLocal(
                context,
                "Vehículo eliminado",
                "Vehículo eliminado con éxito localmente, existen ($totalVehiculos) vehículos"
            )
            return@withContext Result.failure(Exception("Eliminado localmente. Se sincronizará más tarde."))
        }
        return@withContext try {
            val keyToDelete = mapOf("placa" to AttributeValue.fromS(vehiculo.placa))
            val request = DeleteItemRequest.builder().tableName(vehiculosTableName).key(keyToDelete).build()
            dynamoDb.deleteItem(request)
            mostrarNotificacionLocal(
                context,
                "Vehículo eliminado",
                "Vehículo eliminado con éxito en la nube, existen ($totalVehiculos) vehículos"
            )
            Log.d("AppRepository", "Vehículo ${vehiculo.placa} eliminado y sincronizado.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepository", "Fallo al eliminar vehículo de DynamoDB.", e)
            Result.failure(e)
        }
    }

    suspend fun insertarUsuario(usuario: Usuario, context: Context): Result<Unit> =
        withContext(Dispatchers.IO) {

        val idGenerado = usuarioDao.insertarUsuarioYDevolverId(usuario).toInt()
        val usuarioConId = usuario.copy(id = idGenerado)

        val totalUsuarios = usuarioDao.getAllUsuarios().first().size
            val isConnected = connectivityHelper.isNetworkAvailable() && tieneConexionReal()

        if (isConnected) {
            val result = sincronizarItemHaciaNube(
                "usuario",
                usuariosTableName,
                usuarioConId.toDynamoDbMap()
            )
            if (result.isSuccess) {
                mostrarNotificacionLocal(
                    context,
                    "Usuario añadido",
                    "Se añadió en la nube un usuario. Existen ($totalUsuarios) usuarios"

                )
            }
            result
        } else {
            mostrarNotificacionLocal(
                context,
                "Usuario añadido",
                "Se añadió localmente un usuario. Existen ($totalUsuarios) usuarios"
            )
            Result.success(Unit)
        }

    }

    suspend fun tieneConexionReal(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL("https://clients3.google.com/generate_204")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Android")
            connection.setRequestProperty("Connection", "close")
            connection.connectTimeout = 1500
            connection.readTimeout = 1500
            connection.connect()

            // Código 204 significa éxito sin contenido
            connection.responseCode == 204
        } catch (e: IOException) {
            false
        }
    }


    suspend fun getUsuarioPorNombre(nombre: String): Usuario? {
        return usuarioDao.getUsuarioPorNombre(nombre)
    }

    fun validarContrasenaSegura(password: String): Boolean {
        if (password.length < 16) return false
        return password.any { it.isUpperCase() } && password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() }
    }

    private suspend fun <T> sincronizarTabla(
        logTag: String,
        tableName: String,
        mapper: (Map<String, AttributeValue>) -> T,
        inserter: suspend (T) -> Unit
    ) {
        val scanRequest = ScanRequest.builder().tableName(tableName).build()
        val response = dynamoDb.scan(scanRequest)
        val itemsEnNube = response.items().map(mapper)
        itemsEnNube.forEach { inserter(it) }
        Log.i("AppRepository", "Sincronizados ${itemsEnNube.size} $logTag desde DynamoDB.")
    }

    private suspend fun <T> subirSiTablaVacia(
        logTag: String,
        tableName: String,
        localDataProvider: suspend () -> List<T>,
        mapper: (T) -> Map<String, AttributeValue>
    ) {
        val scanRequest = ScanRequest.builder().tableName(tableName).limit(1).build()
        if (dynamoDb.scan(scanRequest).count() == 0) {
            Log.i("AppRepository", "Tabla de $logTag vacía. Subiendo datos locales...")
            localDataProvider().forEach { item ->
                val request =
                    PutItemRequest.builder().tableName(tableName).item(mapper(item)).build()
                dynamoDb.putItem(request)
            }
        }
    }

    private suspend fun sincronizarItemHaciaNube(
        logTag: String,
        tableName: String,
        item: Map<String, AttributeValue>
    ): Result<Unit> {
        if (!connectivityHelper.isNetworkAvailable()) {
            Log.w("AppRepository", "Sin conexión a internet. No se sincronizó el item de $logTag.")
            return Result.failure(Exception("Guardado localmente. Se sincronizará cuando haya conexión."))
        }
        return try {
            val request = PutItemRequest.builder().tableName(tableName).item(item).build()
            dynamoDb.putItem(request)
            Log.d("AppRepository", "Item de $logTag sincronizado con DynamoDB.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepository", "Fallo al sincronizar item de $logTag.", e)
            Result.failure(e)
        }
    }

    // --- Subida de imágenes a S3 usando TransferUtility ---
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input?.copyTo(output)
            }
        }
        return tempFile
    }


    suspend fun subirImagenAS3(uri: String): String = withContext(Dispatchers.IO) {
        val parsedUri = Uri.parse(uri)
        val file = if (parsedUri.scheme == "file") {
            File(parsedUri.path!!)
        } else {
            uriToFile(context, parsedUri)
        }
        val bucket = "vehiculos-app"
        val key = "imagenes/${file.name}"

        val appContext = context.applicationContext
        TransferNetworkLossHandler.getInstance(appContext)

        val transferUtility = TransferUtility.builder()
            .context(context)
            .s3Client(AwsConfig.s3Client)
            .build()

        suspendCancellableCoroutine<String> { cont ->
            val uploadObserver = transferUtility.upload(bucket, key, file)
            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (!cont.isActive) return
                    when (state) {
                        TransferState.COMPLETED -> cont.resume("https://$bucket.s3.amazonaws.com/$key")
                        TransferState.FAILED, TransferState.CANCELED ->
                            cont.resumeWithException(Exception("Error al subir la imagen a S3"))

                        else -> {}
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                override fun onError(id: Int, ex: Exception?) {
                    cont.resumeWithException(ex ?: Exception("Error desconocido al subir a S3"))
                }
            })
        }
    }

    // --- Migración de imágenes locales a S3 ---
    @SuppressLint("UseKtx")
    suspend fun migrarImagenesLocalesAS3() = withContext(Dispatchers.IO) {
        val vehiculos = vehiculoDao.getAllVehiculos().first()
        for (vehiculo in vehiculos) {
            try {
                if (vehiculo.imagenUri?.startsWith("https://") == true) continue

                val uri: Uri? = when {
                    vehiculo.imagenUri?.startsWith("content://") == true -> Uri.parse(vehiculo.imagenUri)
                    vehiculo.imagenResId != null -> {
                        val bitmap =
                            BitmapFactory.decodeResource(context.resources, vehiculo.imagenResId!!)
                        val file = File(context.cacheDir, "temp_${vehiculo.placa}.jpg")
                        FileOutputStream(file).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        }
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                    }
                    else -> null
                }

                if (uri != null) {
                    Log.d("AppRepository", "Subiendo imagen para ${vehiculo.placa} desde $uri")
                    val urlS3 = subirImagenAS3(uri.toString())
                    val actualizado = vehiculo.copy(imagenUri = urlS3)
                    // Aquí se pasa el context correctamente
                    actualizarVehiculo(actualizado, context)
                } else {
                    Log.w("AppRepository", "No se encontró imagen para ${vehiculo.placa}")
                }
            } catch (e: Exception) {
                Log.e("AppRepository", "Error al migrar imagen de ${vehiculo.placa}", e)
            }
        }
    }
}


private fun Vehiculo.toDynamoDbMap(): Map<String, AttributeValue> {
    val item = mutableMapOf<String, AttributeValue>()
    item["placa"] = AttributeValue.fromS(this.placa)
    item["marca"] = AttributeValue.fromS(this.marca)
    item["anio"] = AttributeValue.fromN(this.anio.toString())
    item["color"] = AttributeValue.fromS(this.color)
    item["costoPorDia"] = AttributeValue.fromN(this.costoPorDia.toString())
    item["activo"] = AttributeValue.fromBool(this.activo)
    this.imagenUri?.let { item["imagenUri"] = AttributeValue.fromS(it) }
    this.imagenResId?.let { item["imagenResId"] = AttributeValue.fromN(it.toString()) }
    return item
}

private fun Map<String, AttributeValue>.toVehiculo(): Vehiculo {
    return Vehiculo(
        placa = this["placa"]!!.s(),
        marca = this["marca"]!!.s(),
        anio = this["anio"]!!.n().toInt(),
        color = this["color"]!!.s(),
        costoPorDia = this["costoPorDia"]!!.n().toDouble(),
        activo = this["activo"]!!.bool(),
        imagenUri = this["imagenUri"]?.s(),
        imagenResId = this["imagenResId"]?.n()?.toInt()
    )
}

private fun Usuario.toDynamoDbMap(): Map<String, AttributeValue> {
    return mapOf(
        "id" to AttributeValue.fromN(this.id.toString()),
        "nombre" to AttributeValue.fromS(this.nombre),
        "passwordHash" to AttributeValue.fromS(this.passwordHash),
        "esAdmin" to AttributeValue.fromBool(this.esAdmin)
    )
}

private fun Map<String, AttributeValue>.toUsuario(): Usuario {
    return Usuario(
        id = this["id"]!!.n().toInt(),
        nombre = this["nombre"]!!.s(),
        passwordHash = this["passwordHash"]!!.s(),
        esAdmin = this["esAdmin"]!!.bool()
    )
}
