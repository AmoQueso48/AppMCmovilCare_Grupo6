package com.example.mcmovilcare

import com.example.mcmovilcare.data.remote.ApiService
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.Mantencion
import com.example.mcmovilcare.model.OrdenDeTrabajo
import com.example.mcmovilcare.repository.EquipoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class EquipoRepositoryTest : StringSpec({

    "cargar() debe retornar los equipos y actualizar las ordenes y mantenciones" {
        // 1. Simulamos datos de Equipo, OrdenDeTrabajo y Mantencion
        val fakeEquipo = mockk<Equipo>(relaxed = true)
        val fakeOrden = mockk<OrdenDeTrabajo>(relaxed = true)
        val fakeMantencion = mockk<Mantencion>(relaxed = true) {
            every { equipoId } returns 10
            every { fecha } returns 1700000000000L
        }


        val mockApi = mockk<ApiService>()
        coEvery { mockApi.equipos() } returns listOf(fakeEquipo)
        coEvery { mockApi.ordenes() } returns listOf(fakeOrden)
        coEvery { mockApi.mantenciones() } returns listOf(fakeMantencion)

        // 3. Instanciamos EquipoRepository inyectando mockApi
        val repo = EquipoRepository(api = mockApi)

        // 4.  verificarr todos los resultados
        runTest {
            val resultado = repo.cargar()
            resultado.isSuccess shouldBe true
            resultado.getOrNull() shouldContainExactly listOf(fakeEquipo)
            repo.ordenes.first() shouldContainExactly listOf(fakeOrden)
            repo.mantencionesDe(10) shouldContainExactly listOf(fakeMantencion)
        }
    }

    "responderAutorizacion() debe actualizar la orden correspondiente en el Flow" {
        val ordenOriginal = mockk<OrdenDeTrabajo>(relaxed = true) {
            every { id } returns 5
        }
        val ordenActualizada = mockk<OrdenDeTrabajo>(relaxed = true) {
            every { id } returns 5
        }

        val mockApi = mockk<ApiService>()
        coEvery { mockApi.equipos() } returns emptyList()
        coEvery { mockApi.ordenes() } returns listOf(ordenOriginal)
        coEvery { mockApi.mantenciones() } returns emptyList()
        coEvery { mockApi.responderAutorizacion(5, any()) } returns ordenActualizada

        val repo = EquipoRepository(api = mockApi)

        runTest {
            repo.cargar()
            val respuesta = repo.responderAutorizacion(5, true, "Aprobado por cliente")

            respuesta.isSuccess shouldBe true
            respuesta.getOrNull() shouldBe ordenActualizada
            repo.ordenes.first() shouldContainExactly listOf(ordenActualizada)
        }
    }
})