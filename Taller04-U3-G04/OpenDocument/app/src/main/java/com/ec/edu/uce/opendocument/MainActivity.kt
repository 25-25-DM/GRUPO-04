package com.ec.edu.uce.opendocument

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ec.edu.uce.opendocument.ui.theme.OpenDocumentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenDocumentTheme {
                MyApp()
            }

        }
    }
}