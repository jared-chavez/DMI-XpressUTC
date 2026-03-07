package com.example.xpressutc.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Modelos para la transacción
data class PaymentRequest(
    val amount: Double,
    val currency: String = "MXN",
    val description: String,
    val paymentMethod: String = "card"
)

data class PaymentResponse(
    val status: String, // "success", "declined", "error"
    val transactionId: String?,
    val message: String
)

interface PaymentApiService {
    // Simulamos un endpoint de una pasarela de pagos real
    @POST("api/v1/charge")
    suspend fun processPayment(@Body request: PaymentRequest): PaymentResponse
}

object PaymentRetrofitClient {
    // Usaremos un Mock API o una URL base para la demostración
    private const val BASE_URL = "https://run.mocky.io/" 

    val instance: PaymentApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymentApiService::class.java)
    }
}
