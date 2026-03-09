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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.xpressutc.ui.theme.UniversityGreen
import com.example.xpressutc.ui.theme.AccentGreen
import com.example.xpressutc.ui.theme.DeepBlack
import com.example.xpressutc.ui.theme.LightGray
import com.example.xpressutc.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val titles = listOf("Pedidos Hoy", "Gestión Menú", "Finanzas", "Inventario")
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titles[selectedTab], fontWeight = FontWeight.ExtraBold, color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                actions = {
                    if (selectedTab == 1) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Agregar", tint = UniversityGreen, modifier = Modifier.size(30.dp))
                        }
                    }
                    IconButton(onClick = { navController.navigate("home") { popUpTo(0) } }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Salir", tint = Color.Red)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Black, tonalElevation = 8.dp) {
                AdminNavItem(selectedTab == 0, "Pedidos", Icons.AutoMirrored.Filled.Assignment) { selectedTab = 0 }
                AdminNavItem(selectedTab == 1, "Menú", Icons.Default.RestaurantMenu) { selectedTab = 1 }
                AdminNavItem(selectedTab == 2, "Caja", Icons.Default.AccountBalanceWallet) { selectedTab = 2 }
                AdminNavItem(selectedTab == 3, "Stock", Icons.Default.Inventory) { selectedTab = 3 }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.padding(padding).background(Color.Black)) {
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
            selectedIconColor = UniversityGreen,
            unselectedIconColor = Color.Gray,
            selectedTextColor = UniversityGreen,
            indicatorColor = Color.Transparent
        )
    )
}

@Composable
fun AdminOrdersTab() {
    val orders = listOf(
        Order("#2001", "Hace 2 mins", 110.0, "Pendiente"),
        Order("#2002", "Hace 5 mins", 55.0, "En preparación"),
        Order("#1999", "Hace 1 hora", 85.0, "Listo")
    )
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Órdenes Activas", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Surface(color = UniversityGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
                Text("${orders.size} TOTAL", color = UniversityGreen, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(order.id, color = White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(order.fecha, color = Color.Gray, fontSize = 12.sp)
                Text("$${order.total}", color = UniversityGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Button(
                onClick = { /* Cambiar estado */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (order.estado == "Pendiente") Color(0xFFFFA500) else UniversityGreen
                )
            ) {
                Text(order.estado, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminMenuTab() {
    val items = listOf("Hamburguesa", "Torta de Pollo", "Ensalada César", "Tacos", "Café", "Agua")
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Catálogo de Productos", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                AdminCatalogItemCard(item)
            }
        }
    }
}

@Composable
fun AdminCatalogItemCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.DarkGray)) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = White.copy(alpha = 0.2f), modifier = Modifier.size(50.dp).align(Alignment.Center))
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(name, fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                Text("$55.00", color = UniversityGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { }) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) }
                    IconButton(onClick = { }) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun AdminWalletTab() {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Estado Financiero", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = UniversityGreen)
        ) {
            Box(modifier = Modifier.background(Brush.linearGradient(listOf(UniversityGreen, AccentGreen))).padding(24.dp)) {
                Column {
                    Text("Ventas Totales Hoy", color = White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text("$4,580.00", color = White, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                        Text(" +15% vs ayer", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Resumen Mensual", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        AdminStatRow("Total Pedidos", "452", Icons.Default.ShoppingCart)
        AdminStatRow("Ticket Promedio", "$85.00", Icons.Default.ConfirmationNumber)
    }
}

@Composable
fun AdminStatRow(title: String, value: String, icon: ImageVector) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        color = Color(0xFF111111),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = UniversityGreen)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.Gray)
            Spacer(modifier = Modifier.weight(1f))
            Text(value, color = White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminInventoryTab() {
    val stock = listOf("Pan de hamburguesa" to 45, "Carne de res" to 30, "Pollo" to 8, "Café grano" to 15, "Leche" to 22)
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Control de Insumos", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stock) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = if (item.second < 10) Color.Red.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f), shape = CircleShape, modifier = Modifier.size(10.dp)) { }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(item.first, color = White, fontWeight = FontWeight.Medium)
                        }
                        Text("${item.second} uds", color = if (item.second < 10) Color.Red else Color.Green, fontWeight = FontWeight.ExtraBold)
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
            Button(
                onClick = { 
                    Toast.makeText(context, "Platillo Guardado: $name ($ingredients)", Toast.LENGTH_SHORT).show()
                    onDismiss() 
                },
                colors = ButtonDefaults.buttonColors(containerColor = UniversityGreen),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Guardar", fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) } },
        containerColor = Color(0xFF1A1A1A),
        title = { Text("Nuevo Platillo", color = White, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = White.copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                            Text("Subir Foto", color = White.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                    } else {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }

                val fieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UniversityGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedLabelColor = UniversityGreen,
                    unfocusedLabelColor = Color.Gray
                )

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Ingredientes") }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), colors = fieldColors, shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
    )
}
