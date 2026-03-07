package com.example.xpressutc.api

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// --- Data classes for SEARCH BY NAME ---
data class SearchResponse(
    val products: List<ProductData>,
    val count: Int
)

// --- Data classes for GET BY BARCODE ---
data class FoodResponse(
    val status: Int,
    val product: ProductData?
)

// --- Common Data Classes ---
data class ProductData(
    @SerializedName("product_name_es") val product_name_es: String?,
    @SerializedName("product_name") val product_name: String?,
    @SerializedName("image_url") val image_url: String?,
    val nutriments: Nutriments?
)

data class Nutriments(
    @SerializedName("energy-kcal_100g") val energyKcal: Double?,
    @SerializedName("sugars_100g") val sugars: Double?
)

interface OpenFoodApiService {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): FoodResponse

    @GET("cgi/search.pl")
    suspend fun searchProductByName(
        @Query("search_terms") searchTerm: String,
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 1 // Traemos solo el primer resultado
    ): SearchResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    val foodInstance: OpenFoodApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodApiService::class.java)
    }
}