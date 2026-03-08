package com.example.xpressutc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import com.example.xpressutc.api.ProductData
import com.example.xpressutc.api.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException

// 1. DEFINICIÓN DE ESTADOS (Para cumplir con la evaluación de Carga, Éxito y Error)
sealed class SnackUiState {
    object Idle : SnackUiState()
    object Loading : SnackUiState()
    data class Success(val product: ProductData) : SnackUiState()
    data class Error(val message: String) : SnackUiState()
}

class SnackViewModel : ViewModel() {
    var uiState by mutableStateOf<SnackUiState>(SnackUiState.Idle)
        private set

    // 2. FUNCIÓN ASÍNCRONA
    fun buscarSnack(codigo: String) {
        if (codigo.isBlank()) {
            uiState = SnackUiState.Error("Ingresa un código de barras")
            return
        }

        uiState = SnackUiState.Loading // ESTADO DE CARGA

        viewModelScope.launch { // EJECUCIÓN ASÍNCRONA EN CORRUTINA
            try {
                val respuesta = RetrofitClient.foodInstance.getProductByBarcode(codigo)

                if (respuesta.status == 1 && respuesta.product != null) {
                    uiState = SnackUiState.Success(respuesta.product) // ESTADO DE ÉXITO
                } else {
                    uiState = SnackUiState.Error("Producto no encontrado en la base de datos")
                }
            } catch (e: IOException) {
                // MANEJO DE ERROR DE RED (Sin internet o tiempo de espera agotado)
                uiState = SnackUiState.Error("Error de red: Revisa tu conexión a internet")
            } catch (e: Exception) {
                // OTROS ERRORES
                uiState = SnackUiState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }
}

@Composable
fun SnackScreen(viewModel: SnackViewModel = viewModel()) {
    var codigoBarras by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Info Nutricional de Snacks 🍎", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = codigoBarras,
            onValueChange = { codigoBarras = it },
            label = { Text("Código de barras (Ej. 7501055365470)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.buscarSnack(codigoBarras) },
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.uiState !is SnackUiState.Loading
        ) {
            Text("Consultar con API")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. RENDERIZADO SEGÚN EL ESTADO
        when (val state = viewModel.uiState) {
            is SnackUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator() // ESTO EVALÚA EL MANEJO DE CARGA
                }
            }
            is SnackUiState.Success -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val p = state.product
                        Text("✅ ${p.product_name_es ?: p.product_name}", style = MaterialTheme.typography.titleMedium)
                        Text("🔥 Calorías: ${p.nutriments?.energyKcal ?: 0.0} kcal")
                        Text("🍬 Azúcar: ${p.nutriments?.sugars ?: 0.0} g")
                    }
                }
            }
            is SnackUiState.Error -> {
                Text(text = "⚠️ ${state.message}", color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
