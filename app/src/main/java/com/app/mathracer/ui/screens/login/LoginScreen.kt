@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

// Paleta rápida (ajustá a tu Theme)
private val TealButton = Color(0xFF2EB7A7)
private val FieldBg    = Color(0xCC1F1F1F) // negro con algo de transparencia
private val FieldText  = Color.White
private val HintText   = Color(0xFFB3C0C9)

@Composable
fun LoginScreen(
    onLogin: (user: String, pass: String) -> Unit,
    onLoginWithGoogle: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo
        Image(
            painter = painterResource(R.drawable.login_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa de oscurecimiento (opcional)
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        // ✅ Centramos todo el contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,   // 👈 centra verticalmente
            horizontalAlignment = Alignment.CenterHorizontally  // 👈 centra horizontalmente
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 28.dp) // ← espacio EXTERNO (separa del resto)
                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp) // ← espacio INTERNO (alrededor del logo)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Math Racer",
                    modifier = Modifier.width(200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Campo Usuario
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                placeholder = { Text("Usuario", color = Color.White.copy(alpha = 0.6f)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC1F1F1F)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(Modifier.height(12.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                placeholder = { Text("Contraseña", color = Color.White.copy(alpha = 0.6f)) },
                singleLine = true,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xCC1F1F1F)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { onLogin(user.trim(), pass) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EB7A7))
            ) {
                Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLoginWithGoogle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Continuar con Google")
                }
            }

            Spacer(Modifier.height(20.dp))

            // Texto inferior
            Row {
                Text(
                    text = "¿No tenés cuenta? ",
                    color = Color.White
                )
                Text(
                    text = "Registrate acá",
                    color = Color(0xFF7FD7FF),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
        }
    }
}

