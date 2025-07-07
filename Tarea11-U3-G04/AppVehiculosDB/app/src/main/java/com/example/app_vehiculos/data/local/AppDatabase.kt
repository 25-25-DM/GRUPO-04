package com.example.app_vehiculos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_vehiculos.R
import com.example.app_vehiculos.function.hashSHA256
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Usuario::class, Vehiculo::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun vehiculoDao(): VehiculoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_vehiculos_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prePopulateDatabase(database.usuarioDao(), database.vehiculoDao())
                }
            }
        }

        suspend fun prePopulateDatabase(usuarioDao: UsuarioDao, vehiculoDao: VehiculoDao) {
            val adminUsers = listOf(
                Usuario(nombre = "Byron", passwordHash = hashSHA256("Flores"), esAdmin = true),
                Usuario(nombre = "Jordi", passwordHash = hashSHA256("Pila"), esAdmin = true),
                Usuario(nombre = "Michael", passwordHash = hashSHA256("Barrionuevo"), esAdmin = true),
                Usuario(nombre = "Joffre", passwordHash = hashSHA256("Arias"), esAdmin = true),
                Usuario(nombre = "Edgar", passwordHash = hashSHA256("Tipan"), esAdmin = true),
                Usuario(nombre = "Kevin", passwordHash = hashSHA256("Hurtado"), esAdmin = true),
                Usuario(nombre = "Angelo", passwordHash = hashSHA256("Pujota"), esAdmin = true),
                Usuario(nombre = "Cristian", passwordHash = hashSHA256("Lechon"), esAdmin = true),
            )
            adminUsers.forEach { usuarioDao.insertarUsuario(it) }

            val initialVehicles = listOf(
                Vehiculo("ABC123", "Toyota", 2020, "Rojo", 50.0, true, R.drawable.toyota, null),
                Vehiculo("XYZ789", "Chevrolet", 2019, "Negro", 45.0, false, R.drawable.chevrolet, null),
                Vehiculo("DEF456", "Nissan", 2022, "Azul", 60.0, true, R.drawable.nissan, null),
                Vehiculo("AUC455", "Hyundai", 2025, "Negro", 75.0, true, R.drawable.hyundai,null),
                Vehiculo("TIL777", "Mazda", 2018, "Rojo", 35.0, true, R.drawable.mazda,null)
            )
            initialVehicles.forEach { vehiculoDao.insertarVehiculo(it) }
        }
    }
}