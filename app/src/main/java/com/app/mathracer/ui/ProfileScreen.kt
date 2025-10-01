package com.app.mathracer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R

@Preview(showBackground = true)
@Composable
fun ProfileScreen(){

    var selectedTab by remember { mutableStateOf("Perfil") }

    Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(top = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra superior
        TopBarProfile(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Muestra composable según la pestaña seleccionada
        when (selectedTab) {
            "Perfil" -> Profile(
                userName = "Usuario",
                gamesPlayed = 27,
                points = 11.512,
                userEmail = "jugador3309@gmail.com"
            )

            "Amigos" -> Friends()

            "Ajustes" -> Settings()
        }
    }

}
@Composable
fun TopBarProfile(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Perfil",
            color = if (selectedTab == "Perfil") Color.Magenta else Color.Cyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onTabSelected("Perfil") }
        )

        Text(
            text = "Amigos",
            color = if (selectedTab == "Amigos") Color.Magenta else Color.Cyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onTabSelected("Amigos") }
        )

        Text(
            text = "Ajustes",
            color = if (selectedTab == "Ajustes") Color.Magenta else Color.Cyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onTabSelected("Ajustes") }
        )
    }
}


@Composable
fun Profile(
    userName: String,
    gamesPlayed: Int,
    points: Double,
    userEmail: String
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)

        )
        Text(
            text = userName,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DataCardProfile(title = "Partidas jugadas", value = gamesPlayed.toString())
            DataCardProfile(title = "Puntuación", value = points.toString())
            DataCardProfile(title = "Email registrado", value = userEmail)
        }

    }
}


@Composable
fun DataCardProfile(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, Color.Cyan), // borde cian
        colors = CardDefaults.cardColors(containerColor = Color(0x80000000)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.Cyan,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Friends(){}

@Composable
fun Settings(){}