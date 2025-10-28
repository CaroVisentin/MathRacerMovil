package com.app.mathracer.ui.screens.levels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.ui.screens.levels.viewmodel.LevelUiModel
import com.app.mathracer.ui.screens.levels.viewmodel.LevelsViewModel

@Composable
fun LevelsScreen(viewModel: LevelsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Cyan)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1B102C))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = uiState.worldName,
                color = Color.Cyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(uiState.levels) { index, level ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (index % 2 == 0) Alignment.CenterStart else Alignment.CenterEnd
                    ) {
                        LevelCard(level)
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: LevelUiModel) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (level.isUnlocked) Color(0xFFFFC107) else Color.Gray
        )
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = level.name,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
