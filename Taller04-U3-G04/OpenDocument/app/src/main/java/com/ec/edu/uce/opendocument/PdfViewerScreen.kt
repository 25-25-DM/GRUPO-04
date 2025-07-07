package com.ec.edu.uce.opendocument

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PdfViewerScreen(documentUri: Uri, viewModel: PdfViewerViewModel = viewModel()) {
    val bitmap by viewModel.bitmap.collectAsState()
    val currentPage by viewModel.currentPageIndex.collectAsState()
    val pageCount by viewModel.pageCount.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(documentUri) {
        viewModel.loadPDF(documentUri)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        errorMessage?.let {
            Text(text = it, color = Color.Red)
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "PdfRenderer",
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        } ?: Text("Cargando PDF...",
            color = Color.Black)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.prevPage() },

                enabled = currentPage > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )) {
                Text("Anterior")
            }
            Text("Página ${currentPage + 1} / $pageCount",
                color = Color.Black)
            Button(
                onClick = { viewModel.nextPage() },
                enabled = currentPage + 1 < pageCount) {
                Text("Siguiente")
            }
        }
    }
}