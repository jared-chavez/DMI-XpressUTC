package com.example.xpressutc

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val titles = listOf("Gestión de Pedidos", "Menú Cafetería", "Monedero Admin", "Inventario")
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titles[selectedTab], fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                actions = {
                    if (selectedTab == 1) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Agregar Platillo", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Black) {
                AdminNavItem(selectedTab == 0, "Pedidos", Icons.AutoMirrored.Filled.Assignment) { selectedTab = 0 }
                AdminNavItem(selectedTab == 1, "Menú", Icons.Default.RestaurantMenu) { selectedTab = 1 }
                AdminNavItem(selectedTab == 2, "Monedero", Icons.Default.AccountBalanceWallet) { selectedTab = 2 }
                AdminNavItem(selectedTab == 3, "Inventario", Icons.Default.Inventory) { selectedTab = 3 }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> AdminOrdersTab()
                1 -> AdminMenuTab()
                2 -> AdminWalletTab()
                3 -> AdminInventoryTab()
            }
        }

        if (showAddDialog) {
            AddMenuItemDialog(onDismiss = { showAddDialog = false })
        }
    }
}

@Composable
fun RowScope.AdminNavItem(selected: Boolean, label: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = Color.Gray,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = Color.Transparent
        )
    )
}

@Composable
fun AdminOrdersTab() {
    val orders = listOf(
        Order("#2001", "Hace 2 mins", 110.0, "Pendiente"),
        Order("#2002", "Hace 5 mins", 55.0, "En preparación"),
        Order("#1999", "Hace 1 hora", 85.0, "Listo para entrega")
    )
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pedidos Activos", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orders) { order ->
                AdminOrderCard(order)
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(order.id, color = Color.White, fontWeight = FontWeight.Bold)
                Text(order.fecha, color = Color.Gray, fontSize = 12.sp)
                Text("$${order.total}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Button(onClick = { /* Cambiar estado */ }, shape = RoundedCornerShape(8.dp)) {
                Text(order.estado)
            }
        }
    }
}

@Composable
fun AdminMenuTab() {
    val items = listOf("Hamburguesa", "Torta de Pollo", "Ensalada César", "Tacos", "Café")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            AdminCatalogItemCard(item)
        }
    }
}

@Composable
fun AdminCatalogItemCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.DarkGray))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(name, fontWeight = FontWeight.Bold, color = Color.White)
                Text("$55.00", color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { /* Editar */ }) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray) }
                    IconButton(onClick = { /* Borrar */ }) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                }
            }
        }
    }
}

@Composable
fun AdminWalletTab() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF004D40))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Ventas Totales Hoy", color = Color.White.copy(alpha = 0.8f))
                Text("$4,580.00", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Últimos Movimientos", color = Color.White, fontWeight = FontWeight.Bold)
        // Lista de transacciones...
    }
}

@Composable
fun AdminInventoryTab() {
    val stock = listOf("Pan de hamburguesa" to 45, "Carne de res" to 30, "Pollo" to 20, "Café grano" to 15)
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Control de Insumos", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stock) { item ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item.first, color = Color.White)
                        Text("${item.second} unidades", color = if (item.second < 20) Color.Red else Color.Green, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuItemDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                Toast.makeText(context, "Platillo Guardado", Toast.LENGTH_SHORT).show()
                onDismiss()
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Nuevo Platillo") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Selector de Imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Text("Subir Foto", color = Color.Gray)
                        }
                    } else {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Ingredientes") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}