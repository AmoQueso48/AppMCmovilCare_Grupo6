package com.example.mcmovilcare

import android.app.Application
import com.example.mcmovilcare.data.remote.ApiService
import com.example.mcmovilcare.data.remote.SolicitudBody
import com.example.mcmovilcare.model.EstadoSolicitud
import com.example.mcmovilcare.model.SolicitudDeAtencion
import com.example.mcmovilcare.repository.SolicitudRepository
import com.example.mcmovilcare.viewmodels.SolicitudViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class EdicionSolicitudTest : StringSpec({
    val original = SolicitudDeAtencion(1, "cliente@correo.cl", 101, "Reparación", "Alta", "El equipo no enciende")

    "prepararEdicion carga los campos y prepararNueva limpia el modo edición" {
        val vm = SolicitudViewModel(mockk<Application>(relaxed = true), mockk())
        vm.prepararEdicion(original) shouldBe true
        vm.estado.value.editandoId shouldBe 1
        vm.estado.value.descripcion shouldBe original.descripcion
        vm.estado.value.equipoId shouldBe original.equipoId
        vm.validarFormulario() shouldBe true
        vm.prepararNueva() shouldBe true
        vm.estado.value.editandoId shouldBe null
        vm.estado.value.descripcion shouldBe ""
    }

    "una solicitud cancelada no se puede editar" {
        val vm = SolicitudViewModel(mockk<Application>(relaxed = true), mockk())
        vm.prepararEdicion(original.copy(estado = EstadoSolicitud.CANCELADA)) shouldBe false
        vm.estado.value.editandoId shouldBe null
    }

    "editar usa PUT a través de ApiService y actualiza el listado sin duplicar" {
        val mockApi = mockk<ApiService>()
        val cuerpo = SolicitudBody(101, "Reparación", "Media", "El equipo se reinicia solo")
        val actualizada = original.copy(prioridad = cuerpo.prioridad, descripcion = cuerpo.descripcion)
        coEvery { mockApi.solicitudes() } returns listOf(original)
        coEvery { mockApi.editarSolicitud(1, cuerpo) } returns actualizada
        val repo = SolicitudRepository(mockApi)
        runTest {
            repo.refrescar().isSuccess shouldBe true
            repo.editar(1, cuerpo).getOrThrow() shouldBe actualizada
            repo.solicitudesDe(original.clienteCorreo).first() shouldBe listOf(actualizada)
            coVerify(exactly = 1) { mockApi.editarSolicitud(1, cuerpo) }
        }
    }
})
