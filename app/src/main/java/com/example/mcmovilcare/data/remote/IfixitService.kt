package com.example.mcmovilcare.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class IfixitRespuesta(val results: List<IfixitGuia>? = null)
data class IfixitGuia(
    val guideid: Int? = null,
    val dataType: String? = null,
    val title: String? = null,
    val url: String? = null,
    val username: String? = null
)

interface IfixitService {
    @GET("suggest/{consulta}")
    suspend fun buscarGuias(
        @Path("consulta") consulta: String,
        @Query("doctypes") tipo: String = "guide"
    ): IfixitRespuesta
}
