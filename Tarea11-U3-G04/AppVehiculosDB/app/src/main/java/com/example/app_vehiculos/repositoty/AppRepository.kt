package com.example.app_vehiculos.repository

import android.content.Context
import android.util.Log
import com.example.app_vehiculos.AwsConfig
import com.example.app_vehiculos.data.local.Usuario
import com.example.app_vehiculos.data.local.UsuarioDao
import com.example.app_vehiculos.data.local.Vehiculo
import com.example.app_vehiculos.data.local.VehiculoDao
import com.example.app_vehiculos.util.ConnectivityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.UnknownHostException

class AppRepository(
    private val usuarioDao: UsuarioDao,
    private val vehiculoDao: VehiculoDao,
    context: Context
) {
    private val dynamoDb = AwsConfig.dynamoDbClient
    private val vehiculosTableName = "Vehiculos"
    private val usuariosTableName = "Usuarios"
    private val connectivityHelper = ConnectivityHelper(context)

    val todosLosVehiculos: Flow<List<Vehiculo>> = vehiculoDao.getAllVehiculos()
    val todosLosUsuarios: Flow<List<Usuario>> = usuarioDao.getAllUsuarios()

    suspend fun sincronizarAmbasVias(): Result<Unit> = withContext(Dispatchers.IO) {
        if (!connectivityHelper.isNetworkAvailable()) {
            return@withContext Result.failure(Exception("Sin conexión a internet. No se pudo sincronizar."))
        }
        try {
            Log.i("AppRepository", "Iniciando sincronización bidireccional...")
            Log.i("AppRepository", "Subiendo datos locales a DynamoDB...")
            todosLosUsuarios.first().forEach { usuario ->
                val request = PutItemRequest.builder().tableName(usuariosTableName).item(usuario.toDynamoDbMap()).build()
                dynamoDb.putItem(request)
            }
            todosLosVehiculos.first().forEach { vehiculo ->
                val request = PutItemRequest.builder().tableName(vehiculosTableName).item(vehiculo.toDynamoDbMap()).build()
                dynamoDb.putItem(request)
            }
            Log.i("AppRepository", "Subida completada.")
            Log.i("AppRepository", "Descargando datos desde DynamoDB...")
            sincronizarTabla("Vehículos", vehiculosTableName, { it.toVehiculo() }, { vehiculoDao.insertarVehiculo(it) })
            sincronizarTabla("Usuarios", usuariosTableName, { it.toUsuario() }, { usuarioDao.insertarUsuario(it) })

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

    suspend fun insertarVehiculo(vehiculo: Vehiculo): Result<Unit> = withContext(Dispatchers.IO) {
        vehiculoDao.insertarVehiculo(vehiculo)
        sincronizarItemHaciaNube("vehículo", vehiculosTableName, vehiculo.toDynamoDbMap())
    }

    suspend fun actualizarVehiculo(vehiculo: Vehiculo): Result<Unit> = withContext(Dispatchers.IO) {
        vehiculoDao.actualizarVehiculo(vehiculo)
        sincronizarItemHaciaNube("vehículo", vehiculosTableName, vehiculo.toDynamoDbMap())
    }

    suspend fun eliminarVehiculo(vehiculo: Vehiculo): Result<Unit> = withContext(Dispatchers.IO) {
        vehiculoDao.eliminarVehiculo(vehiculo)
        if (!connectivityHelper.isNetworkAvailable()) {
            return@withContext Result.failure(Exception("Eliminado localmente. Se sincronizará más tarde."))
        }
        try {
            val keyToDelete = mapOf("placa" to AttributeValue.fromS(vehiculo.placa))
            val request = DeleteItemRequest.builder().tableName(vehiculosTableName).key(keyToDelete).build()
            dynamoDb.deleteItem(request)
            Log.d("AppRepository", "Vehículo ${vehiculo.placa} eliminado y sincronizado.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AppRepository", "Fallo al eliminar vehículo de DynamoDB.", e)
            Result.failure(e)
        }
    }

    suspend fun insertarUsuario(usuario: Usuario): Result<Unit> = withContext(Dispatchers.IO) {
        usuarioDao.insertarUsuario(usuario)
        sincronizarItemHaciaNube("usuario", usuariosTableName, usuario.toDynamoDbMap())
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
                val request = PutItemRequest.builder().tableName(tableName).item(mapper(item)).build()
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
    val item = mutableMapOf<String, AttributeValue>()
    item["nombre"] = AttributeValue.fromS(this.nombre)
    item["passwordHash"] = AttributeValue.fromS(this.passwordHash)
    item["esAdmin"] = AttributeValue.fromBool(this.esAdmin)
    return item
}

private fun Map<String, AttributeValue>.toUsuario(): Usuario {
    return Usuario(
        nombre = this["nombre"]!!.s(),
        passwordHash = this["passwordHash"]!!.s(),
        esAdmin = this["esAdmin"]!!.bool()
    )
}