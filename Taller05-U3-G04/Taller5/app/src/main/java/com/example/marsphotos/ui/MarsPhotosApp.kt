/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.marsphotos.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.marsphotos.R
import com.example.marsphotos.function.hashSHA256
import com.example.marsphotos.model.Usuario
import com.example.marsphotos.ui.screens.HomeScreen
import com.example.marsphotos.ui.screens.HomeScreenCount
import com.example.marsphotos.ui.screens.LoginScreen
import com.example.marsphotos.ui.screens.MarsViewModel
import com.example.marsphotos.ui.screens.MarsViewModelCount
import com.example.marsphotos.ui.screens.Type

@Composable
fun MarsPhotosApp() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val usuarios = remember {
        mutableStateListOf(
            Usuario("Byron", "Flores", hashSHA256("Flores")),
            Usuario("Jordi", "Pila", hashSHA256("Pila")),
            Usuario("Veyker", "Barrionuevo", hashSHA256("Barrionuevo")),
            Usuario("Joffre", "Arias", hashSHA256("Arias")),
            Usuario("Edgar", "Tipan", hashSHA256("Tipan")),
            Usuario("Angelo", "Pujota", hashSHA256("Pujota")),
            Usuario("Cristian", "Lechon", hashSHA256("Lechon")),
            Usuario("Kevin", "Hurtado", hashSHA256("Hurtado"))

        )
    }

    var apellidoIngresado by remember { mutableStateOf("") }
    var nombreIngresado by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MarsTopAppBar(scrollBehavior = scrollBehavior) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("login") {
                    LoginScreen(
                        usuariosRegistrados = usuarios,
                        onLoginSuccess = { nombreLogin, apellidoLogin ->
                            apellidoIngresado = apellidoLogin
                            nombreIngresado = nombreLogin
                            navController.navigate("home")
                        },
                        onGoToRegister = { navController.navigate("register") }
                    )
                }
                composable("home") {
                    val marsViewModel: MarsViewModel = viewModel()
                    HomeScreen(
                        marsUiState = marsViewModel.marsUiState,
                        contentPadding = innerPadding,
                        onNavigateToHome = {
                            navController.navigate("count")
                        }
                    )
                }

                composable("count") {
                    val marsViewModelCount: MarsViewModelCount = viewModel()
                    HomeScreenCount(
                        marsUiState = marsViewModelCount.marsUiStateCount,

                        )
                }

            }

        }
    }
}

@Composable
fun MarsTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name) + "\n   "
                        + stringResource(R.string.group_name),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = Type,
                ),
            )
        },
        modifier = modifier
    )
}
