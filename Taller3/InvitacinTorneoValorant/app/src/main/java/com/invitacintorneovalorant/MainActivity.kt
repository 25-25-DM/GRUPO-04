package com.invitacintorneovalorant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.invitacintorneovalorant.ui.theme.InvitaciónTorneoValorantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InvitaciónTorneoValorantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFAFB4F5A)
                    ) {
                    GreetingText(
                        message = "TOURNAMENT VALORANT!",
                        from = "TE INVITA: Ragnarok Gaming House",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingText(message: String, from: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = message,
            fontSize = 55.sp,
            lineHeight = 60.sp,
            textAlign = TextAlign.Center,

        )
        Image(
            painter = painterResource(id = R.drawable.logo_valorant),
            contentDescription = "Logo de Valorant",
            modifier = Modifier
                .size(355.dp, 225.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .padding(10.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "📍 Lugar: Universidad Central del Ecuador",
            fontSize = 20.sp,
            modifier = Modifier
                .clickable {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=Universidad Central del Ecuador")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }
                .padding(16.dp))
        Text(
            text = "📅 Fecha-Hora: 25 de mayo, 16:00",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp))
        Text(
            text = "👤 Contacto: Ragnarok Gaming House",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    val url =
                        "https://www.facebook.com/ragnarokgaminghouse/photos/pb.100063505511101.-2207520000/1815568208600219/?type=3"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
        )
        Text(
            text = "📧 Correo: pendril@ragnarok-gaming.com",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    val emailIntent = Intent(
                        Intent.ACTION_SENDTO,
                        Uri.parse("mailto:pendril@ragnarok-gaming.com")
                    )
                    context.startActivity(emailIntent)
                })
        Text(
            text = "📞 Teléfono: 0987654321",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:0987654321")
                    context.startActivity(intent)
                }
        )

        Text(
            text = from,
            fontSize = 30.sp,
            lineHeight = 35.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .align(alignment = Alignment.End),
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InvitaciónTorneoValorantTheme {
        GreetingText(message = "TOURNAMENT VALORANT", from = "TE INVITA: Ragnarok Gaming House")

    }
}