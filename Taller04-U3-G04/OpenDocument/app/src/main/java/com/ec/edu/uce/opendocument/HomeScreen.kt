package com.ec.edu.uce.opendocument

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onDocumentSelected: (Uri) -> Unit) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onDocumentSelected(it)
            }
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_pick_file_black_24dp),
            tint = Color.Gray,
            contentDescription = "Logo del grupo",
            modifier = Modifier.fillMaxHeight(0.3f).fillMaxWidth(0.3f)
        )
        Text("Haga clic en abrir para ver el contenido de un PDF.",
            color = Color.Black)
        Spacer(
            Modifier.height(16.dp)
        )
        Button(
            modifier = Modifier
                .padding(horizontal = 30.dp),
            onClick = {
                launcher.launch(
                    arrayOf("application/pdf")
                )
            }
        ) {
            Text("OPEN FILE")
        }
    }
}