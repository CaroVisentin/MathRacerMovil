package com.app.mathracer.ui.screens.signalrtest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalRTestScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SignalRTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    
    // Auto-scroll a los logs más recientes
    LaunchedEffect(uiState.logs.size) {
        if (uiState.logs.isNotEmpty()) {
            listState.animateScrollToItem(uiState.logs.size - 1)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isConnected) Color(0xFF4CAF50) else Color(0xFF2196F3)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🧪 SignalR Test",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = uiState.connectionState,
                    fontSize = 16.sp,
                    color = Color.White
                )
                
                if (uiState.isConnected) {
                    Text(
                        text = "🎮 Estado: Conectado y listo",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Player Name Input
        OutlinedTextField(
            value = uiState.playerName,
            onValueChange = viewModel::updatePlayerName,
            label = { Text("Nombre del jugador") },
            placeholder = { Text("Ingresa tu nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.testConnection() },
                enabled = !uiState.isConnecting && !uiState.isConnected,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (uiState.isConnecting) "Conectando..." else "🔗 Conectar")
            }
            
            Button(
                onClick = { viewModel.findMatch() },
                enabled = uiState.isConnected,
                modifier = Modifier.weight(1f)
            ) {
                Text("🔍 Buscar Partida")
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.disconnect() },
                enabled = uiState.isConnected,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                modifier = Modifier.weight(1f)
            ) {
                Text("🔌 Desconectar")
            }
            
            Button(
                onClick = { viewModel.clearLogs() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)),
                modifier = Modifier.weight(1f)
            ) {
                Text("🗑️ Limpiar Logs")
            }
        }
        
        Button(
            onClick = onNavigateBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("← Volver al Home")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Connection Info
        if (uiState.isConnected) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "✅ Conexión SignalR Activa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Puedes buscar partidas o enviar comandos",
                        color = Color(0xFF388E3C)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Logs Section
        Text(
            text = "📋 Logs (${uiState.logs.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
        ) {
            if (uiState.logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay logs aún...\nPresiona 'Conectar' para empezar",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(uiState.logs) { log ->
                        Text(
                            text = log,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}