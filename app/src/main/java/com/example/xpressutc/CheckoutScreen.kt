package com.example.xpressutc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

// ESTADOS PARA EVALUACIÓN (Carga, Éxito, Error)
sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Processing : PaymentUiState()
    data class Success(val transactionId: String) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class CheckoutViewModel : ViewModel() {
    // LLAVE DE STRIPE PROPORCIONADA
    private val STRIPE_PUBLISHABLE_KEY = "pk_test_51T7PRLFscDUqItan4u7cg1QJUEv7f6DADMWKBu3XrLuYaifWAqQXL1UAShxe8VsTOrDsi7yxqqmonfromY8hnQ5A00lM3tSKKX"

    var uiState by mutableStateOf<PaymentUiState>(PaymentUiState.Idle)
        private set

    fun processPayment(total: Double) {
        uiState = PaymentUiState.Processing
        
        viewModelScope.launch {
            try {
                // SIMULACIÓN DE PETICIÓN ASÍNCRONA A STRIPE
                // Aquí se usaría la STRIPE_PUBLISHABLE_KEY para tokenizar la tarjeta
                delay(2500) 

                if (total > 0) {
                    uiState = PaymentUiState.Success("STRIPE-CHG-${System.currentTimeMillis()}")
                } else {
                    uiState = PaymentUiState.Error("Monto inválido para procesar")
                }
            } catch (e: IOException) {
                uiState = PaymentUiState.Error("Fallo de red al conectar con Stripe")
            } catch (e: Exception) {
                uiState = PaymentUiState.Error("Error en pasarela: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() { uiState = PaymentUiState.Idle }
}

@Composable
fun CheckoutScreen(navController: NavHostController, total: Double) {
    val viewModel: CheckoutViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    // Verificación de sesión
    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            navController.navigate("login") { popUpTo("home") }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
        Text("Pago Seguro via Stripe", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Resumen de compra", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                Text("$${total} MXN", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text("Usuario: ${auth.currentUser?.email ?: "Sesión activa"}", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        when (val state = viewModel.uiState) {
            is PaymentUiState.Idle -> {
                Button(
                    onClick = { viewModel.processPayment(total) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Pagar Ahora con Stripe", fontWeight = FontWeight.Bold)
                }
            }
            is PaymentUiState.Processing -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Text("Conectando con servidores de Stripe...", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodySmall)
            }
            is PaymentUiState.Success -> {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(72.dp))
                Text("¡Pedido Confirmado!", fontWeight = FontWeight.ExtraBold, color = Color.Green, fontSize = 22.sp)
                Text("ID de Transacción: ${state.transactionId}", fontSize = 10.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { navController.navigate("home") }) { Text("Volver a la Cafetería") }
            }
            is PaymentUiState.Error -> {
                Text("❌ ${state.message}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                Button(onClick = { viewModel.resetState() }, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Intentar de nuevo")
                }
            }
        }
    }
}
