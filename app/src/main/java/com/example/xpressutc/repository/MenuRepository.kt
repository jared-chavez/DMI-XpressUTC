package com.example.xpressutc.repository

import android.util.Log
import com.example.xpressutc.model.Platillo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MenuRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun obtenerPlatillos(): List<Platillo> {
        return try {
            val resultado = db.collection("platillos").get().await()
            resultado.toObjects(Platillo::class.java)
        } catch (e: Exception) {
            Log.e("MenuRepository", "Error al descargar el menú: ${e.message}")
            emptyList()
        }
    }


    suspend fun guardarPedido(correoAlumno: String, total: Double, platillos: List<String>): Boolean {
        return try {
            val nuevoPedido = hashMapOf(
                "alumno" to correoAlumno,
                "total" to total,
                "platillos" to platillos,
                "fecha" to System.currentTimeMillis()
            )

            db.collection("pedidos").add(nuevoPedido).await()
            true
        } catch (e: Exception) {
            Log.e("MenuRepository", "Error al guardar el pedido: ${e.message}")
            false
        }
    }
}