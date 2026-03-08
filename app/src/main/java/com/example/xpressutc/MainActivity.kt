package com.example.xpressutc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.xpressutc.ui.theme.XpressUTCTheme
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

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerContainerColor = Color.Black,
                        drawerContentColor = Color.White
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "XpressUTC",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider(color = Color.Gray)
                        NavigationDrawerItem(
                            label = { Text("Inicio", color = Color.White) },
                            selected = false,
                            onClick = { 
                                navController.navigate("home")
                                scope.launch { drawerState.close() } 
                            },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        NavigationDrawerItem(
                            label = { Text("Menú", color = Color.White) },
                            selected = false,
                            onClick = { 
                                navController.navigate("catalog")
                                scope.launch { drawerState.close() } 
                            },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        NavigationDrawerItem(
                            label = { Text("Info Snacks", color = Color.White) },
                            selected = false,
                            onClick = { 
                                navController.navigate("snacks")
                                scope.launch { drawerState.close() } 
                            },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        NavigationDrawerItem(
                            label = { Text("Registro", color = Color.White) },
                            selected = false,
                            onClick = { 
                                navController.navigate("register")
                                scope.launch { drawerState.close() } 
                            },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        NavigationDrawerItem(
                            label = { Text("Iniciar sesión", color = Color.White) },
                            selected = false,
                            onClick = { 
                                navController.navigate("login")
                                scope.launch { drawerState.close() } 
                            },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(drawerState, scope, navController) }
                    composable("login") { LoginScreen(navController, auth) }
                    composable("register") { RegisterScreen(navController, auth) }
                    composable("catalog") { CatalogScreen(drawerState, scope) }
                    composable("student") { StudentScreen(navController) }
                    composable("admin") { AdminScreen(navController) }
                    composable("snacks") { SnackScreen() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(drawerState: DrawerState, scope: kotlinx.coroutines.CoroutineScope, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("XpressUTC", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            item { ImageCarousel() }
            item { MenuSection(navController) }
            item { SpacesSection() }
            item { FooterSection() }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Universitario", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { /* Lógica recuperar */ }, modifier = Modifier.align(Alignment.End)) {
            Text("¿Olvidaste tu contraseña?", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { 
                val cleanEmail = email.trim()
                val cleanPass = password.trim()
                
                if (cleanEmail == "cafeadmin@utc.edu.mx" && cleanPass == "admincafe") {
                    navController.navigate("admin")
                } else if (cleanEmail.isNotEmpty() && cleanPass.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(cleanEmail, cleanPass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate("student")
                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Ingresar", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("register") }) {
            val annotatedString = buildAnnotatedString {
                append("¿No tienes cuenta? ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
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
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))
        
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.White
        )

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Institucional") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), colors = textFieldColors)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), colors = textFieldColors)
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { 
                val cleanEmail = email.trim()
                
                if (cleanEmail.isNotEmpty() && password.isNotEmpty()) {
                    auth.createUserWithEmailAndPassword(cleanEmail, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "¡Cuenta creada!", Toast.LENGTH_SHORT).show()
                                navController.navigate("login")
                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword
        ) {
            Text("Crear cuenta", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            val annotatedString = buildAnnotatedString {
                append("¿Ya tienes cuenta? ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                    append("Inicia sesión")
                }
            }
            Text(annotatedString, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(drawerState: DrawerState, scope: kotlinx.coroutines.CoroutineScope) {
    val items = listOf("Hamburguesa", "Torta de Pollo", "Ensalada César", "Tacos de Guisado", "Agua de Sabor", "Café")
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).fillMaxSize().padding(8.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { product ->
                CatalogItemCard(product)
            }
        }
    }
}

@Composable
fun CatalogItemCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$55.00", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Agregar al carrito */ },
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

@Composable
fun ImageCarousel() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(200.dp)) { page ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().background(if (page == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary))
            Text(
                text = if (page == 0) "Bienvenidos a la Cafetería" else "Calidad en cada bocado",
                color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        }
    }
}

@Composable
fun MenuSection(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuestro Menú", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("SNACKS", "COMIDA", "BEBIDAS").forEach { category ->
                Card(modifier = Modifier.weight(1f).height(100.dp).padding(2.dp), onClick = { navController.navigate("catalog") }) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(category, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SpacesSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nuestros Espacios", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        SpaceCard("Zona de Estudio Individual", "Espacios diseñados para concentración máxima con iluminación perfecta y comodidad.", "Ver disponibilidad")
    }
}

@Composable
fun SpaceCard(title: String, description: String, buttonText: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(12.dp)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.DarkGray))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Button(onClick = { }, modifier = Modifier.align(Alignment.End)) { Text(buttonText) }
            }
        }
    }
}

@Composable
fun FooterSection() {
    Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("UTC", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
            Text("© 2024 Cafetería Universitaria", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
