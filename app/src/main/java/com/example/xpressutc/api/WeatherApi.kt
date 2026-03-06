package com.example.xpressutc.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Modelos de datos para la API de clima
data class WeatherResponse(
    val main: Main,
    val name: String
)

data class Main(
    val temp: Double
)

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

object WeatherRetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val service: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}