package com.example.app_vehiculos.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app_vehiculos.R
import com.example.app_vehiculos.data.local.AppDatabase
import kotlinx.coroutines.delay

val Fuente = FontFamily(
    Font(R.font.pacifico_regular, FontWeight.Normal, FontStyle.Normal)
)

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible.value = true
        // Precarga datos iniciales
        val database = AppDatabase.getInstance(context)
        database.prepopulateIfNeeded()

        delay(2500)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222)),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                animationSpec = tween(1000),
                initialScale = 0.7f
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.wrapContentHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_nobg),
                    contentDescription = "Logo de la app",
                    modifier = Modifier.size(220.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Vehiculos Don Veyker",
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontFamily = Fuente,
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
