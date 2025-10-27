package com.app.mathracer.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.ui.screens.profile.viewmodel.ProfileViewModel
import androidx.compose.runtime.collectAsState
import com.app.mathracer.ui.screens.profile.components.Friends
import com.app.mathracer.ui.screens.profile.components.Profile
import com.app.mathracer.ui.screens.profile.components.Settings

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()){

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra superior
        TopBarProfile(
            selectedTab = uiState.selectedTab,
            onTabSelected = viewModel::onTabSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Muestra composable según la pestaña seleccionada
        when (uiState.selectedTab) {
            "Perfil" -> Profile(
                userName = uiState.userName,
                gamesPlayed = uiState.gamesPlayed,
                points = uiState.points,
                userEmail = uiState.userEmail
            )

            "Amigos" -> Friends(friends = uiState.friends)

            "Ajustes" -> Settings(
                soundVolume = uiState.soundVolume,
                musicVolume = uiState.musicVolume,
                onSoundVolumeChange = viewModel::onSoundVolumeChange,
                onMusicVolumeChange = viewModel::onMusicVolumeChange,
                onLogout = viewModel::onLogout,
                onDeleteAccount = viewModel::onDeleteAccount
            )
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

