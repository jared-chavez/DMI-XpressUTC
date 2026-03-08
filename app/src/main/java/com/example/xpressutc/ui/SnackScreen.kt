package com.example.xpressutc.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.xpressutc.api.ProductData
import com.example.xpressutc.api.RetrofitClient
import kotlinx.coroutines.launch

class SnackViewModel : ViewModel() {
    var productInfo by mutableStateOf<ProductData?>(null)
    var searchStatus by mutableStateOf("Ingresa un código o nombre para buscar.")
    var isLoading by mutableStateOf(false)

    fun searchProduct(query: String) {
        if (query.isBlank()) return
        
        isLoading = true
        searchStatus = "Buscando en OpenFoodFacts..."
        productInfo = null
        
        viewModelScope.launch {
            try {
                val isBarcode = query.all { it.isDigit() } && query.length >= 8
                val product = if (isBarcode) {
                    val response = RetrofitClient.foodInstance.getProductByBarcode(query)
                    if (response.status == 1) response.product else null
                } else {
                    val response = RetrofitClient.foodInstance.searchProductByName(query)
                    response.products.firstOrNull()
                }

                if (product != null) {
                    productInfo = product
                    searchStatus = "¡Producto encontrado!"
                } else {
                    searchStatus = "❌ No se encontró el producto."
                }
            } catch (e: Exception) {
                searchStatus = "⚠️ Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnackScreen(viewModel: SnackViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Info Nutricional 🍎",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Bar Section
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Ej: Takis, Coca-Cola o Código", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = Color.Gray)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.searchProduct(searchQuery)
                        focusManager.clearFocus()
                    }),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color(0xFF1A1A1A),
                        unfocusedContainerColor = Color(0xFF1A1A1A)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        viewModel.searchProduct(searchQuery)
                        focusManager.clearFocus()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !viewModel.isLoading && searchQuery.isNotBlank()
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Buscar Producto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                AnimatedVisibility(
                    visible = viewModel.productInfo != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    viewModel.productInfo?.let { product ->
                        ProductResultCard(product = product)
                    }
                }

                if (viewModel.productInfo == null && !viewModel.isLoading) {
                    EmptyStateView(viewModel.searchStatus)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
                FooterInfo()
            }
        }
    }
}

@Composable
fun ProductResultCard(product: ProductData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(250.dp).fillMaxWidth()) {
                AsyncImage(
                    model = product.image_url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                
                // Gradient overlay at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 400f
                            )
                        )
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = product.product_name_es ?: product.product_name ?: "Nombre no disponible",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NutritionItem(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "Energía",
                        value = "${product.nutriments?.energyKcal?.toInt() ?: "N/A"}",
                        unit = "kcal",
                        color = Color(0xFFFF5722),
                        modifier = Modifier.weight(1f)
                    )
                    NutritionItem(
                        icon = Icons.Default.WaterDrop,
                        label = "Azúcares",
                        value = "${product.nutriments?.sugars ?: "N/A"}",
                        unit = "g",
                        color = Color(0xFF03A9F4),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Warning section (Simplified)
                val sugar = product.nutriments?.sugars ?: 0.0
                if (sugar > 10.0) {
                    Surface(
                        color = Color(0xFFFFEB3B).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Contenido elevado de azúcares.",
                                color = Color(0xFFFFC107),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionItem(
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(2.dp))
                Text(unit, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
            }
        }
    }
}

@Composable
fun EmptyStateView(status: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.Fastfood,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.DarkGray
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = status,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun FooterInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Datos proporcionados por",
            color = Color.Gray,
            fontSize = 12.sp
        )
        Text(
            "Open Food Facts",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
