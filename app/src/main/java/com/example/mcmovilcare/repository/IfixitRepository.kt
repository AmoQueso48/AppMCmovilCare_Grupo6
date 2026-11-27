package com.example.mcmovilcare.repository

import com.example.mcmovilcare.data.remote.IfixitGuia
import com.example.mcmovilcare.data.remote.IfixitService
import com.example.mcmovilcare.data.remote.RetrofitIfixit
import java.net.URI

class IfixitRepository(private val api: IfixitService = RetrofitIfixit.api) {
    suspend fun buscarGuias(consulta: String): List<IfixitGuia> =
        api.buscarGuias(consulta.trim()).results.orEmpty()
            .filter { guia ->
                guia.dataType == "guide" && !guia.title.isNullOrBlank() &&
                    guia.guideid != null && enlaceValido(guia.url)
            }
            .distinctBy { it.guideid }
            .take(5)

    private fun enlaceValido(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return runCatching {
            val uri = URI(url)
            val host = uri.host.orEmpty().lowercase()
            uri.scheme.equals("https", ignoreCase = true) &&
                (host == "ifixit.com" || host.endsWith(".ifixit.com")) &&
                uri.userInfo == null
        }.getOrDefault(false)
    }
}
