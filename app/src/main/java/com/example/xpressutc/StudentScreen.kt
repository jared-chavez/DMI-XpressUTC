package com.example.xpressutc

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val titles = listOf("Dashboard", "Menú Cafetería", "Mis Pedidos", "Configuración")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        titles[selectedTab], 
                        fontWeight = FontWeight.ExtraBold, 
                        color = Color.White,
                        fontSize = 20.sp
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                actions = {
                    IconButton(onClick = { /* Lógica Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Black, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.GridView, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = "Menú") },
                    label = { Text("Menú") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Pedidos") },
                    label = { Text("Pedidos") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.ManageAccounts, contentDescription = "Cuenta") },
                    label = { Text("Cuenta") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> StudentHomeTab(navController)
                1 -> StudentMenuTab()
                2 -> OrdersTab()
                3 -> ProfileTab(navController)
            }
        }
    }
}

@Composable
fun StudentHomeTab(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val name = user?.displayName ?: "Estudiante UTC"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Bienvenido,", color = Color.Gray, fontSize = 16.sp)
                    Text(name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.DarkGray,
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        item {
            WalletCard()
        }
        
        item {
            SectionHeader("Promociones del Día")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PromotionCard("Lunch Especial", "Torta + Jugo x $65", Color(0xFFE91E63))
                PromotionCard("Coffee Break", "2x1 en Americano", Color(0xFF795548))
                PromotionCard("Snack Time", "Papas preparadas $25", Color(0xFFFF9800))
            }
        }
        
        item {
            SectionHeader("Acciones Rápidas")
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard("Escanear QR", Icons.Default.QrCodeScanner, Modifier.weight(1f)) { }
                QuickActionCard("Ubicación", Icons.Default.LocationOn, Modifier.weight(1f)) { }
            }
        }

        item {
            SectionHeader("Tu Pedido Activo")
            Spacer(modifier = Modifier.height(12.dp))
            OrderCard(Order("#1234", "Hace 5 mins", 55.0, "En preparación"))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Recuerda que el tiempo de entrega estimado es de 10-15 minutos.", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun WalletCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Saldo en Monedero", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Text("$250.00", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Recargar */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(12.dp)
            ) {
                Text("RECARGAR AHORA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun PromotionCard(title: String, subtitle: String, color: Color) {
    Card(
        modifier = Modifier.width(220.dp).height(110.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            }
            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(48.dp).align(Alignment.BottomEnd))
        }
    }
}

@Composable
fun StudentMenuTab() {
    val items = listOf("Hamburguesa", "Torta de Pollo", "Ensalada César", "Tacos de Guisado", "Agua de Sabor", "Café", "Molletes", "Sincronizadas")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { product ->
            StudentCatalogItemCard(product)
        }
    }
}

@Composable
fun StudentCatalogItemCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.DarkGray))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                Text("$55.00", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Agregar */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Agregar", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfileTab(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    
    var name by remember { mutableStateOf(user?.displayName ?: "Estudiante UTC") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1).uppercase(), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Modificar Datos", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Mantén tu información al día", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))

        ProfileTextField(value = name, onValueChange = { name = it }, label = "Nombre Completo", icon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(16.dp))
        ProfileTextField(value = email, onValueChange = { email = it }, label = "Correo Institucional", icon = Icons.Default.Email)
        Spacer(modifier = Modifier.height(16.dp))
        ProfileTextField(value = password, onValueChange = { password = it }, label = "Cambiar Contraseña", icon = Icons.Default.Lock, isPassword = true)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { /* Lógica para actualizar perfil */ },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { 
                auth.signOut()
                navController.navigate("home") { popUpTo(0) }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("CERRAR SESIÓN", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, isPassword: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.DarkGray,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

data class Order(val id: String, val fecha: String, val total: Double, val estado: String)

@Composable
fun OrdersTab() {
    val orders = listOf(
        Order("#1234", "Hoy, 10:30 AM", 55.0, "En preparación"),
        Order("#1233", "Ayer, 2:15 PM", 120.0, "Entregado"),
        Order("#1230", "15 Oct, 9:00 AM", 45.0, "Entregado")
    )

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        SectionHeader("Historial de Pedidos")
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orders) { order ->
                OrderCard(order)
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(order.id, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(order.fecha, color = Color.Gray, fontSize = 12.sp)
                Text("$${order.total}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            
            Surface(
                color = if (order.estado == "En preparación") MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = order.estado,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = if (order.estado == "En preparación") MaterialTheme.colorScheme.primary else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
