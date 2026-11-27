package com.example.mcmovilcare.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitIfixit {
    val api: IfixitService by lazy {
        val cliente = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(20, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl("https://www.ifixit.com/api/2.0/")
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IfixitService::class.java)
    }
}
