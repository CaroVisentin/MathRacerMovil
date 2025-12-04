package com.app.mathracer.ui.screens.profile

import android.util.Log
import android.util.Patterns
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import com.app.mathracer.data.model.Player
import com.app.mathracer.ui.components.StarryBackground
import com.app.mathracer.ui.components.ProductImage
import com.app.mathracer.ui.screens.profile.components.Friends
import com.app.mathracer.ui.screens.profile.components.Profile
import com.app.mathracer.ui.screens.profile.components.Settings
import com.app.mathracer.ui.theme.DarkPurpleMR

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
            .background(DarkPurpleMR)
            .padding(top = 24.dp)
    ) {
        StarryBackground()

        var showAddDialog by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var newFriendIdText by remember { mutableStateOf("") }
        var inviteResultMsg by remember { mutableStateOf<String?>(null) }
        var deleteResultMsg by remember { mutableStateOf<String?>(null) }
        var friendToDeleteName by remember { mutableStateOf<String?>(null) }
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
                    actualLevel = uiState.actualLevel,
                    points = uiState.points,
                    userEmail = uiState.userEmail.toString()
                )

                "Amigos" -> {

                    if (uiState.pending.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                            Text(
                                text = "Solicitudes pendientes",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            uiState.pending.forEach { req ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ProductImage(productId = req.character?.id, fallbackRes = com.app.mathracer.R.drawable.avatar, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = req.name,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = {
                                        Log.d(
                                            "ProfileScreen",
                                            "Accepting request from id=${req.id}"
                                        )
                                        viewModel.acceptRequest(req.id) { success, msg ->
                                            inviteResultMsg = msg
                                                ?: if (success) "Solicitud aceptada" else "Error al aceptar"
                                            Log.d(
                                                "ProfileScreen",
                                                "acceptRequest result: success=$success msg=$inviteResultMsg"
                                            )
                                        }
                                    }) { Text("Aceptar", color = Color.Cyan) }
                                    TextButton(onClick = {
                                        Log.d(
                                            "ProfileScreen",
                                            "Rejecting request from id=${req.id}"
                                        )
                                        viewModel.rejectRequest(req.id) { success, msg ->
                                            inviteResultMsg = msg
                                                ?: if (success) "Solicitud rechazada" else "Error al rechazar"
                                            Log.d(
                                                "ProfileScreen",
                                                "rejectRequest result: success=$success msg=$inviteResultMsg"
                                            )
                                        }
                                    }) { Text("Rechazar", color = Color.Red) }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Friends(
                        friends = uiState.friends,
                        onAddFriend = { showAddDialog = true },
                        onDeleteFriend = { friend ->
                            friendToDeleteName = friend.name
                            showDeleteDialog = true
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
            Dialog(
                onDismissRequest = {
                    showAddDialog = false
                    inviteResultMsg = null
                }
            ) {
                Column(
                    modifier = Modifier
                        .background(Color(0xFF07112B), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Agregar amigo por email",
                        color = Color.Cyan,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // newFriendIdText ahora es el email
                    OutlinedTextField(
                        value = newFriendIdText,
                        onValueChange = { newFriendIdText = it },
                        label = { Text("Email del jugador") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.White,
                            fontSize = 16.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Cyan,
                            unfocusedBorderColor = Color.Cyan.copy(alpha = 0.6f),
                            focusedLabelColor = Color.Cyan,
                            unfocusedLabelColor = Color(0xFFB0BEC5),
                            cursorColor = Color.Cyan,
                            focusedContainerColor = Color(0xFF0C1735),
                            unfocusedContainerColor = Color(0xFF0C1735),
                            focusedPlaceholderColor = Color(0xFFB0BEC5),
                            unfocusedPlaceholderColor = Color(0xFF78909C)
                        )
                    )


                    Spacer(modifier = Modifier.height(12.dp))

                    val friendToSearch = uiState.friendToSearch
                    val mainButtonText = if (friendToSearch == null) "Buscar" else "Enviar"

                    friendToSearch?.let { friend ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0E1B3D), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Jugador encontrado:",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            FriendCard(
                                    friend = friend
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showAddDialog = false
                                inviteResultMsg = null
                            }
                        ) {
                            Text("Cancelar", color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = {
                                val email = newFriendIdText.trim()

                                if (friendToSearch == null) {
                                    // MODO BUSCAR
                                    if (email.isBlank()) {
                                        inviteResultMsg = "Ingresá un email"
                                        return@TextButton
                                    }

                                    val isValidEmail =
                                        Patterns.EMAIL_ADDRESS.matcher(email).matches()

                                    if (!isValidEmail) {
                                        inviteResultMsg = "Email inválido"
                                        Log.w(
                                            "ProfileScreen",
                                            "Invalid email entered: $email"
                                        )
                                        return@TextButton
                                    }

                                    inviteResultMsg = "Buscando jugador..."
                                    // Esta función debería actualizar uiState.friendToSearch internamente
                                    viewModel.searchPlayer(email)

                                } else {
                                    // MODO ENVIAR
                                    viewModel.inviteByPlayerId(friendToSearch.id) { success, msg ->
                                        inviteResultMsg = msg ?: if (success) {
                                            "Solicitud enviada"
                                        } else {
                                            "Error al enviar"
                                        }

                                        Log.d(
                                            "ProfileScreen",
                                            "inviteByPlayerId result: success=$success msg=$inviteResultMsg"
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(mainButtonText, color = Color.Cyan)
                        }
                    }

                    inviteResultMsg?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = msg, color = Color.White)
                    }
                }
            }
        }

        if (showDeleteDialog) {
            Dialog(onDismissRequest = { showDeleteDialog = false; friendToDeleteName = null; deleteResultMsg = null }) {
                Column(
                    modifier = Modifier
                        .background(Color(0xFF07112B), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Eliminar amigo",
                        color = Color.Cyan,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¿Estás seguro que querés eliminar a ${friendToDeleteName ?: "este amigo"}? Esta acción no se puede deshacer.",
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDeleteDialog = false; friendToDeleteName = null; deleteResultMsg = null }) {
                            Text("Cancelar", color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(onClick = {
                            val name = friendToDeleteName
                            if (name != null) {
                                val remote = uiState.remoteFriends.firstOrNull { it.name == name }
                                remote?.let {
                                    viewModel.deleteFriend(it.id) { success ->
                                        deleteResultMsg = if (success) "Amigo eliminado" else "Error al eliminar amigo"
                                    }
                                } ?: run { deleteResultMsg = "No se encontró el amigo" }
                            }
                            showDeleteDialog = false
                            friendToDeleteName = null
                        }) {
                            Text("Eliminar", color = Color.Red)
                        }
                    }

                    deleteResultMsg?.let { msg ->
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
    val tabs = listOf("Perfil", "Amigos", "Ajustes")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tabs.forEach { label ->
            val selected = selectedTab == label
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selected) Color.Magenta.copy(alpha = 0.15f)
                        else Color.Cyan.copy(alpha = 0.15f)
                    )
                    .clickable { onTabSelected(label) }
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selected) Color.Magenta else Color.Cyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FriendCard(friend: Player) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Cyan, RoundedCornerShape(8.dp))
            .background(Color(0x80000000), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = friend.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = friend.points.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
