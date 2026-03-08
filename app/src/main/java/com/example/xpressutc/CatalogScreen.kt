package com.example.xpressutc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.xpressutc.model.Platillo
import com.example.xpressutc.repository.MenuRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(drawerState: DrawerState, scope: kotlinx.coroutines.CoroutineScope, onAddToCart: (Platillo) -> Unit) {
    val repository = remember { MenuRepository() }
    var platillos by remember { mutableStateOf<List<Platillo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // CARGAR DATOS REALES DE FIREBASE
    LaunchedEffect(Unit) {
        platillos = repository.obtenerPlatillos()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menú de la Cafetería", color = Color.White) },
                actions = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding).fillMaxSize().padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(platillos) { platillo ->
                    CatalogItemCard(platillo, onAddToCart)
                }
            }
        }
    }
}

@Composable
fun CatalogItemCard(platillo: Platillo, onAddToCart: (Platillo) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // IMAGEN REAL DESDE URL (Usando Coil)
            AsyncImage(
                model = platillo.imagenUrl,
                contentDescription = platillo.nombre,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(platillo.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Text("$${platillo.precio}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAddToCart(platillo) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Agregar", fontSize = 12.sp)
                }
            }
        }
    }
}
