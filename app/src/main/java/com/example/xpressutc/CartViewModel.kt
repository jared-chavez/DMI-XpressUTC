package com.example.xpressutc

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.xpressutc.model.Platillo

class CartViewModel : ViewModel() {
    // Lista reactiva de productos en el carrito
    private val _items = mutableStateListOf<Platillo>()
    val items: List<Platillo> get() = _items

    fun agregarProducto(platillo: Platillo) {
        _items.add(platillo)
    }

    fun eliminarProducto(platillo: Platillo) {
        _items.remove(platillo)
    }

    fun calcularTotal(): Double {
        return _items.sumOf { it.precio }
    }

    fun limpiarCarrito() {
        _items.clear()
    }
}
