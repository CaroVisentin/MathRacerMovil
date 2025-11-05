package com.app.mathracer.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
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
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onHelpClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 24.dp)
    ) {
        var showAddDialog by remember { mutableStateOf(false) }
        var newFriendIdText by remember { mutableStateOf("") }
        var inviteResultMsg by remember { mutableStateOf<String?>(null) }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBarProfile(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (uiState.selectedTab) {
                "Perfil" -> Profile(
                    userName = uiState.userName,
                    gamesPlayed = uiState.gamesPlayed,
                    points = uiState.points,
                    userEmail = uiState.userEmail.toString()
                )

                "Amigos" -> {
                   
                    if (uiState.pending.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                            Text(text = "Solicitudes pendientes", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            uiState.pending.forEach { req ->
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = req.name, color = Color.White, modifier = Modifier.weight(1f))
                                    TextButton(onClick = {
                                        android.util.Log.d("ProfileScreen", "Accepting request from id=${req.id}")
                                        viewModel.acceptRequest(req.id) { success, msg ->
                                            inviteResultMsg = msg ?: if (success) "Solicitud aceptada" else "Error al aceptar"
                                            android.util.Log.d("ProfileScreen", "acceptRequest result: success=$success msg=$inviteResultMsg")
                                        }
                                    }) { Text("Aceptar", color = Color.Cyan) }
                                    TextButton(onClick = {
                                        android.util.Log.d("ProfileScreen", "Rejecting request from id=${req.id}")
                                        viewModel.rejectRequest(req.id) { success, msg ->
                                            inviteResultMsg = msg ?: if (success) "Solicitud rechazada" else "Error al rechazar"
                                            android.util.Log.d("ProfileScreen", "rejectRequest result: success=$success msg=$inviteResultMsg")
                                        }
                                    }) { Text("Rechazar", color = Color.Red) }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Friends(friends = uiState.friends,
                        onAddFriend = { showAddDialog = true },
                        onDeleteFriend = { friend ->
                            
                            val remote = uiState.remoteFriends.firstOrNull { it.name == friend.name }
                            remote?.let { viewModel.deleteFriend(it.id) }
                        }
                    )
                }

                "Ajustes" -> Settings(
                    soundVolume = uiState.soundVolume,
                    musicVolume = uiState.musicVolume,
                    onSoundVolumeChange = viewModel::onSoundVolumeChange,
                    onMusicVolumeChange = viewModel::onMusicVolumeChange,
                    onLogout = onLogout,
                    onDeleteAccount = viewModel::onDeleteAccount
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            IconButton(
                onClick = onHelpClick,
                modifier = Modifier
                    .size(56.dp)
                    .border(2.dp, Color.Cyan, RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "?",
                    color = Color.Cyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
        }

        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false; inviteResultMsg = null }) {
                Column(modifier = Modifier
                    .background(Color(0xFF07112B), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)) {
                    Text(text = "Agregar amigo por ID", color = Color.Cyan, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newFriendIdText,
                        onValueChange = { newFriendIdText = it },
                        label = { Text("ID del jugador") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { showAddDialog = false; inviteResultMsg = null }) { Text("Cancelar", color = Color.White) }
                        TextButton(onClick = {
                            val id = newFriendIdText.toIntOrNull()
                            if (id != null) {
                                android.util.Log.d("ProfileScreen", "Sending friend request from current user to id=$id")
                                viewModel.inviteByPlayerId(id) { success, msg ->
                                    inviteResultMsg = msg ?: if (success) "Solicitud enviada" else "Error al enviar"
                                    android.util.Log.d("ProfileScreen", "inviteByPlayerId result: success=$success msg=$inviteResultMsg")
                                }
                            } else {
                                inviteResultMsg = "ID inválido"
                                android.util.Log.w("ProfileScreen", "Invalid ID entered: $newFriendIdText")
                            }
                        }) { Text("Enviar", color = Color.Cyan) }
                    }
                    inviteResultMsg?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = msg, color = Color.White)
                    }
                }
            }
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
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onTabSelected("Perfil") }
        )

        Text(
            text = "Amigos",
            color = if (selectedTab == "Amigos") Color.Magenta else Color.Cyan,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onTabSelected("Amigos") }
        )

        Text(
            text = "Ajustes",
            color = if (selectedTab == "Ajustes") Color.Magenta else Color.Cyan,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onTabSelected("Ajustes") }
        )
    }
}

