package com.example.xpressutc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Login
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.xpressutc.ui.theme.XpressUTCTheme
import com.example.xpressutc.ui.theme.UniversityGreen
import com.example.xpressutc.ui.theme.AccentGreen
import com.example.xpressutc.ui.theme.DeepBlack
import com.example.xpressutc.ui.theme.CardBlack
import com.example.xpressutc.ui.theme.LightGray
import com.example.xpressutc.ui.theme.White
import com.example.xpressutc.ui.SnackScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XpressUTCTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.Black,
                drawerContentColor = Color.White
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "XpressUTC",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = UniversityGreen
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = { 
                        navController.navigate("home")
                        scope.launch { drawerState.close() } 
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = Color.White, unselectedIconColor = Color.White)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = null) },
                    label = { Text("Menú Completo") },
                    selected = false,
                    onClick = { 
                        navController.navigate("catalog")
                        scope.launch { drawerState.close() } 
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = Color.White, unselectedIconColor = Color.White)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Fastfood, contentDescription = null) },
                    label = { Text("Info Snacks") },
                    selected = false,
                    onClick = { 
                        navController.navigate("snacks")
                        scope.launch { drawerState.close() } 
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = Color.White, unselectedIconColor = Color.White)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                    label = { Text("Registrarse") },
                    selected = false,
                    onClick = { 
                        navController.navigate("register")
                        scope.launch { drawerState.close() } 
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = Color.White, unselectedIconColor = Color.White)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null) },
                    label = { Text("Iniciar Sesión") },
                    selected = false,
                    onClick = { 
                        navController.navigate("login")
                        scope.launch { drawerState.close() } 
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = Color.White, unselectedIconColor = Color.White)
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = { PremiumBottomNavigation(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController, 
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen(navController, drawerState, scope) }
                composable("catalog") { CatalogScreen() }
                composable("snacks") { SnackScreen() }
                composable("login") { LoginScreen(navController, auth) }
                composable("register") { RegisterScreen(navController, auth) }
                composable("student") { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Panel Estudiante", color = Color.Black) } }
                composable("admin") { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Panel Admin", color = Color.Black) } }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController, drawerState: DrawerState, scope: kotlinx.coroutines.CoroutineScope) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        item { PremiumTopSection(drawerState, scope) }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(White)
                    .padding(20.dp)
            ) {
                PremiumSearchBar()
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Menú", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    CategoryFilters()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Column(modifier = Modifier.background(White).padding(horizontal = 20.dp)) {
                val products = listOf(
                    Product("Tacos de Guisado", "$45.00", "https://images.unsplash.com/photo-1562059390-a76e3c433a8a?q=80&w=600"),
                    Product("Torta de Pollo", "$55.00", "https://images.unsplash.com/photo-1509722747041-619f392e83ce?q=80&w=600"),
                    Product("Ensalada Fresh", "$60.00", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=600"),
                    Product("Café Americano", "$25.00", "https://images.unsplash.com/photo-1541167760496-162955ed8a9f?q=80&w=600")
                )
                
                products.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        pair.forEach { product ->
                            PremiumProductCard(product, modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        
        item {
            Box(modifier = Modifier.background(White).padding(20.dp)) {
                PremiumUserCard(navController)
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp).background(White)) }
    }
}

@Composable
fun PremiumTopSection(drawerState: DrawerState, scope: kotlinx.coroutines.CoroutineScope) {
    Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=1000",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent, DeepBlack.copy(alpha = 0.9f))
                )
            )
        )
        Column(modifier = Modifier.padding(top = 40.dp, start = 20.dp, end = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Default.Menu, contentDescription = "Abrir Menú", tint = White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Surface(modifier = Modifier.weight(1f).height(45.dp), shape = RoundedCornerShape(25.dp), color = White.copy(alpha = 0.9f)) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Text("  Buscar en XpressUTC", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text("UNIVERSITARIO", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Xpress\nCafetería", color = White, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 46.sp)
            Text("Calidad y sabor en cada bocado\npara tu jornada académica.", color = White.copy(alpha = 0.7f), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
            Surface(shape = CircleShape, color = UniversityGreen, modifier = Modifier.size(45.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = White, modifier = Modifier.padding(10.dp))
            }
        }
    }
}

@Composable
fun PremiumSearchBar() {
    Surface(modifier = Modifier.fillMaxWidth().height(55.dp), shape = RoundedCornerShape(30.dp), color = LightGray) {
        Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("¿Qué se te antoja hoy?", color = Color.Gray, modifier = Modifier.weight(1f))
            Icon(Icons.Default.Tune, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Black)
        }
    }
}

@Composable
fun CategoryFilters() {
    val categories = listOf("Todos", "Comida", "Snacks", "Bebidas")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(categories) { category ->
            Text(
                category,
                color = if (category == "Todos") UniversityGreen else Color.Gray,
                fontWeight = if (category == "Todos") FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun PremiumProductCard(product: Product, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(260.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(shape = CircleShape, color = White.copy(alpha = 0.2f), modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(30.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(product.name, color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Cafetería UTC", color = Color.Gray, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(product.price, color = AccentGreen, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PremiumUserCard(navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlack)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?q=80&w=200",
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(CircleShape).background(UniversityGreen)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Mi Cuenta UTC", color = White, fontWeight = FontWeight.Bold)
                Text("Ver mis pedidos y saldo", color = Color.Gray, fontSize = 10.sp)
            }
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(containerColor = UniversityGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Ingresar", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PremiumBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(modifier = Modifier.fillMaxWidth().height(80.dp), color = White, shadowElevation = 16.dp) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), 
            horizontalArrangement = Arrangement.SpaceAround, 
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, "Inicio", currentRoute == "home") { 
                if (currentRoute != "home") navController.navigate("home") 
            }
            BottomNavItem(Icons.Default.Search, "Catálogo", currentRoute == "catalog") { 
                if (currentRoute != "catalog") navController.navigate("catalog") 
            }
            
            Surface(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-15).dp)
                    .clickable { navController.navigate("catalog") },
                shape = CircleShape,
                color = UniversityGreen,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = White, modifier = Modifier.size(30.dp))
                }
            }
            
            BottomNavItem(Icons.Default.RestaurantMenu, "Snacks", currentRoute == "snacks") { 
                if (currentRoute != "snacks") navController.navigate("snacks") 
            }
            BottomNavItem(Icons.Default.Person, "Perfil", currentRoute == "login") { 
                if (currentRoute != "login") navController.navigate("login") 
            }
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = if (isSelected) UniversityGreen else Color.LightGray, 
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label, 
            fontSize = 11.sp, 
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) UniversityGreen else Color.LightGray
        )
    }
}

@Composable
fun LoginScreen(navController: NavHostController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Restaurant, contentDescription = null, tint = UniversityGreen, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("¡Bienvenido!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Inicia sesión para continuar", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UniversityGreen,
            unfocusedBorderColor = Color.DarkGray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = UniversityGreen,
            unfocusedLabelColor = Color.Gray,
            focusedContainerColor = CardBlack.copy(alpha = 0.5f),
            unfocusedContainerColor = CardBlack.copy(alpha = 0.5f)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Universitario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = UniversityGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = UniversityGreen) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { 
                val cleanEmail = email.trim()
                val cleanPass = password.trim()
                
                if (cleanEmail.isEmpty() || cleanPass.isEmpty()) {
                    Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (cleanEmail == "cafeadmin@utc.edu.mx" && cleanPass == "admincafe") {
                    Toast.makeText(context, "¡Bienvenido Administrador!", Toast.LENGTH_SHORT).show()
                    navController.navigate("admin")
                } else {
                    auth.signInWithEmailAndPassword(cleanEmail, cleanPass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                navController.navigate("student")
                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UniversityGreen)
        ) {
            Text("Ingresar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("register") }) {
            val annotatedString = buildAnnotatedString {
                append("¿No tienes cuenta? ")
                withStyle(style = SpanStyle(color = UniversityGreen, fontWeight = FontWeight.Bold)) {
                    append("Regístrate aquí")
                }
            }
            Text(annotatedString, color = Color.White)
        }
    }
}

@Composable
fun RegisterScreen(navController: NavHostController, auth: FirebaseAuth) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Únete a la comunidad XpressUTC", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UniversityGreen,
            unfocusedBorderColor = Color.DarkGray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = UniversityGreen,
            unfocusedLabelColor = Color.Gray,
            focusedContainerColor = CardBlack.copy(alpha = 0.5f),
            unfocusedContainerColor = CardBlack.copy(alpha = 0.5f)
        )

        OutlinedTextField(
            value = name, 
            onValueChange = { name = it }, 
            label = { Text("Nombre Completo") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = UniversityGreen) },
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email, 
            onValueChange = { email = it }, 
            label = { Text("Correo Institucional") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = UniversityGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Contraseña") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = UniversityGreen) },
            visualTransformation = PasswordVisualTransformation(), 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword, 
            onValueChange = { confirmPassword = it }, 
            label = { Text("Confirmar Contraseña") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = UniversityGreen) },
            visualTransformation = PasswordVisualTransformation(), 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = textFieldColors
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { 
                val cleanEmail = email.trim()
                if (name.isEmpty() || cleanEmail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Por favor, completa todos los datos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (password != confirmPassword) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                
                auth.createUserWithEmailAndPassword(cleanEmail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        } else {
                            Toast.makeText(context, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UniversityGreen)
        ) {
            Text("Registrarme", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            val annotatedString = buildAnnotatedString {
                append("¿Ya tienes cuenta? ")
                withStyle(style = SpanStyle(color = UniversityGreen, fontWeight = FontWeight.Bold)) {
                    append("Inicia sesión")
                }
            }
            Text(annotatedString, color = Color.White)
        }
    }
}

data class Product(val name: String, val price: String, val imageUrl: String)

@Composable
fun CatalogScreen() {
    Box(modifier = Modifier.fillMaxSize().background(White), contentAlignment = Alignment.Center) {
        Text("Pantalla de Catálogo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
