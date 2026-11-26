package com.example.mcmovilcare.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SesionApi {
    @Volatile
    var token: String? = null
}
object RetrofitInstance {
    // Emulador: 10.0.2.2 es el PC. Celular real: IP del PC, ej. "http://192.168.1.82:3000/"
    private const val USAR_EMULADOR = true

    private const val HOST_PC = "localhost"

    private val BASE_URL = if (USAR_EMULADOR) "http://10.0.2.2:3000/" else "http://$HOST_PC:3000/"

    private val cliente = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            val peticion = cadena.request().newBuilder().apply {
                SesionApi.token?.let { addHeader("Authorization", "Bearer $it") }
            }.build()
            cadena.proceed(peticion)
        }
        .build()
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}