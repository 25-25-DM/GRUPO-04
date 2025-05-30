package com.ec.edu.uce.opendocument

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ec.edu.uce.opendocument.ui.theme.OpenDocumentTheme
import java.text.SimpleDateFormat
import java.util.*

enum class PdfScreen(@StringRes val title: Int) {
    Welcome(title = R.string.app_name),
    Home(title = R.string.home),
    Viewer(title = R.string.viewer)
}

@Composable
fun SetStatusBarColor(color: Color, darkIcons: Boolean) {
    val context = LocalContext.current
    val window = (context as Activity).window

    SideEffect {
        window.statusBarColor = color.toArgb()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (darkIcons) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = if (darkIcons)
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else
                0
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfAppBar(
    currentScreen: PdfScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    SetStatusBarColor(MaterialTheme.colorScheme.onPrimaryContainer, darkIcons = false)

    TopAppBar(
        title = {
            Text(
                text = stringResource(currentScreen.title),
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        tint = Color.White,
                        contentDescription = "Más opciones",
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.intro_message)) },

                        onClick = {
                            showMenu = false

                        }
                    )

                }
            }
        },

    )
}

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }

    val logo = painterResource(id = R.drawable.logo)

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = logo,
            contentDescription = "Logo del grupo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )
        Text("Ingrese su nombre",
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge)
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { username = it },
            label = {
                Text("Nombre de usuario") },

            singleLine = true
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(),
                    onClick = {
            if (username.isNotBlank()) {
                saveLogin(context, username)
                onContinue()
            } else {
                Toast.makeText(context, "Debe ingresar un nombre", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Continuar")
        }
    }
}

@SuppressLint("UseKtx")
private fun saveLogin(context: Context, username: String) {
    val sharedPreferences = context.getSharedPreferences("user_logins", Context.MODE_PRIVATE)
    val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("America/Guayaquil")
    val now = sdf.format(Date())

    val currentLogs = sharedPreferences.getStringSet("logs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    currentLogs.add("$username - $now")
    sharedPreferences.edit().putStringSet("logs", currentLogs).apply()
}

@SuppressLint("UseKtx")
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = when (backStackEntry?.destination?.route?.substringBefore("?")) {
        PdfScreen.Welcome.name -> PdfScreen.Welcome
        PdfScreen.Home.name -> PdfScreen.Home
        PdfScreen.Viewer.name -> PdfScreen.Viewer
        else -> PdfScreen.Welcome
    }

    Scaffold(
        topBar = {
            PdfAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = PdfScreen.Welcome.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(PdfScreen.Welcome.name) {
                WelcomeScreen(onContinue = {
                    navController.navigate(PdfScreen.Home.name)
                })
            }
            composable(PdfScreen.Home.name) {
                HomeScreen(onDocumentSelected = { uri ->
                    navController.navigate("${PdfScreen.Viewer.name}?uri=${Uri.encode(uri.toString())}")
                })
            }
            composable(
                "${PdfScreen.Viewer.name}?uri={uri}",
                arguments = listOf(navArgument("uri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uriString = backStackEntry.arguments?.getString("uri") ?: return@composable
                PdfViewerScreen(documentUri = Uri.parse(uriString))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PdfAppBarPreview() {
    OpenDocumentTheme {
        PdfAppBar(
            currentScreen = PdfScreen.Welcome,
            canNavigateBack = true,
            navigateUp = {}
        )
    }
}
