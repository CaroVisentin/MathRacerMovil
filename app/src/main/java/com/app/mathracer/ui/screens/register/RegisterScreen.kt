@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.mathracer.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R
import com.app.mathracer.ui.screens.register.viewmodel.RegisterViewModel

private val PlateBg      = Color(0xCC000000)
private val FieldBg      = Color(0xCC0F2A3B)
private val FieldHint    = Color(0xFFB8D9EA)
private val FieldStroke  = Color(0xFF1EC5FF)
private val PrimaryTeal  = Color(0xFF2EB7A7)

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onGoogleSignIn: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetError()
        }
    }
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            viewModel.resetSuccess()
            onRegisterSuccess()
        }
    }

    Box(Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.25f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 28.dp)
                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Mathi Racer",
                    modifier = Modifier.width(200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = { Text("Email", color = Color.White.copy(alpha = 0.6f),fontSize = 20.sp,) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC1F1F1F)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.user,
                onValueChange = viewModel::onUserChange,
                placeholder = { Text("Usuario", color = Color.White.copy(alpha = 0.6f), fontSize = 20.sp,) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC1F1F1F)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )
            Spacer(Modifier.height(10.dp))

            var showPass by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = { Text("Contraseña", color = Color.White.copy(alpha = 0.6f), fontSize = 20.sp,) },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC1F1F1F)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )
            Spacer(Modifier.height(10.dp))

            var showRePass by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = uiState.repeatPassword,
                onValueChange = viewModel::onRepeatPasswordChange,
                placeholder = { Text("Repetir contraseña", color = Color.White.copy(alpha = 0.6f), fontSize = 20.sp,) },
                visualTransformation = if (showRePass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showRePass = !showRePass }) {
                        Icon(
                            imageVector = if (showRePass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC1F1F1F)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.registerUser() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2EB7A7),
                    contentColor = Color.White
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading)
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                else
                    Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(Modifier.height(12.dp))


            Spacer(Modifier.height(16.dp))
            Row {
                Text("¿Ya tenés cuenta? ", color = Color.White, fontSize = 20.sp,)
                Text(
                    "Iniciá sesión acá",
                    color = Color(0xFF7FD7FF),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValue: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        placeholder = { Text(placeholder, color = FieldHint) },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(FieldBg),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FieldStroke,
            unfocusedBorderColor = FieldStroke.copy(alpha = 0.7f),
            cursorColor = Color.White
        )
    )
}

@Composable
private fun AuthPasswordField(
    value: String,
    onValue: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onToggleVisible: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        placeholder = { Text(placeholder, color = FieldHint) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisible) {
                Icon(
                    imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(FieldBg),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FieldStroke,
            unfocusedBorderColor = FieldStroke.copy(alpha = 0.7f),
            cursorColor = Color.White
        )
    )
}
