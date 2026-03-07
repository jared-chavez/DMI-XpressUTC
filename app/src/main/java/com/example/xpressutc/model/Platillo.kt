package com.example.xpressutc.model

data class Platillo(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val categoria: String = "",
    val imagenUrl: String = "",
    val disponible: Boolean = true
)