package com.example.mcmovilcare

import com.example.mcmovilcare.data.remote.IfixitGuia
import com.example.mcmovilcare.data.remote.IfixitRespuesta
import com.example.mcmovilcare.data.remote.IfixitService
import com.example.mcmovilcare.repository.IfixitRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class IfixitRepositoryTest : StringSpec({
    "retorna guías válidas sin duplicados y omite resultados ajenos a iFixit" {
        val api = mockk<IfixitService>()
        val guia = IfixitGuia(1, "guide", "Guía ficticia", "https://www.ifixit.com/Guide/Ejemplo/1", "Autor ficticio")
        coEvery { api.buscarGuias("Galaxy A54", "guide") } returns IfixitRespuesta(listOf(
            guia, guia, guia.copy(guideid = 2, dataType = "wiki"),
            guia.copy(guideid = 3, url = "https://example.org/guide")
        ))
        runTest { IfixitRepository(api).buscarGuias(" Galaxy A54 ") shouldBe listOf(guia) }
    }
    "una respuesta vacía retorna lista vacía" {
        val api = mockk<IfixitService>()
        coEvery { api.buscarGuias("Sin coincidencias", "guide") } returns IfixitRespuesta(emptyList())
        runTest { IfixitRepository(api).buscarGuias("Sin coincidencias") shouldBe emptyList<IfixitGuia>() }
    }
})
