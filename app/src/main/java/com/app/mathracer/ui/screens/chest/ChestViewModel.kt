package com.app.mathracer.ui.screens.chest

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.model.ChestItem
import com.app.mathracer.data.repository.ChestRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChestViewModel : ViewModel() {

    var loading by mutableStateOf(true)
        private set

    var items by mutableStateOf<List<ChestItem>>(emptyList())
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var isOpening by mutableStateOf(false)
        private set

    var showOpenImage by mutableStateOf(false)
        private set

    var itemsVisibleCount by mutableStateOf(0)
        private set

    var fetched by mutableStateOf(false)
        private set
    var chestType: String = "tutorial"


    fun selectChestType(type: String) {
        when(type) {
            "tutorial" -> openChestTutorial()
            "world" -> openChestWorld()
            //"purchase" -> chestType = "purchase"
            else -> chestType = "tutorial"
        }
    }

    fun openChestTutorial() {
        if (isOpening || fetched) return
        isOpening = true
        viewModelScope.launch {
            try {
               val resp = ChestRepository.completeTutorial()

                if (resp.isSuccessful) {
                    val body = resp.body()

                    items = body?.items ?: emptyList()
                    fetched = true

                    delay(400)
                    showOpenImage = true

                    // Revelar items uno por uno
                    for (i in items.indices) {
                        itemsVisibleCount = i + 1
                        delay(300)
                    }

                } else {
                    error = resp.errorBody()?.string() ?: "Error ${resp.code()}"
                    isOpening = false
                }

            } catch (e: Exception) {
                error = e.message ?: "Excepción al abrir cofre"
                isOpening = false
            } finally {
                loading = false
            }
        }
    }

    fun openChestWorld() {
        if (isOpening || fetched) return
        isOpening = true
        viewModelScope.launch {
            try {
                val resp = ChestRepository.openChest()
                if (resp.isSuccessful) {
                    val body = resp.body()

                    items = body?.items ?: emptyList()
                    fetched = true

                    delay(400)
                    showOpenImage = true

                    // Revelar items uno por uno
                    for (i in items.indices) {
                        itemsVisibleCount = i + 1
                        delay(300)
                    }
                } else {
                    error = resp.errorBody()?.string() ?: "Error ${resp.code()}"
                    isOpening = false
                }
            } catch (e: Exception) {
                error = e.message ?: "Excepción al abrir cofre"
                isOpening = false
            } finally {
                loading = false
            }
        }
    }

    val canContinue: Boolean
        get() = fetched && itemsVisibleCount >= items.size && items.isNotEmpty()
}