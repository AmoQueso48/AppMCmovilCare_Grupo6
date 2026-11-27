package com.example.mcmovilcare

import com.example.mcmovilcare.data.remote.ApiService
import com.example.mcmovilcare.model.EstadoSolicitud
import com.example.mcmovilcare.model.SolicitudDeAtencion
import com.example.mcmovilcare.repository.SolicitudRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class SolicitudRepositoryTest : StringSpec({

    "refrescar() debe cargar las solicitudes simuladas y filtrarlas por correo" {
        val fakeSolicitudes = listOf(
            SolicitudDeAtencion(
                id = 1,
                clienteCorreo = "cliente1@correo.cl",
                equipoId = 101,
                tipoServicio = "Mantenimiento Preventivo",
                prioridad = "Alta",
                descripcion = "Equipo no enciende",
                estado = EstadoSolicitud.ENVIADA,
                fechaCreacion = 1700000000000L
            ),
            SolicitudDeAtencion(
                id = 2,
                clienteCorreo = "cliente2@correo.cl",
                equipoId = null,
                tipoServicio = "Soporte Técnico",
                prioridad = "Media",
                descripcion = "Configuración de red",
                estado = EstadoSolicitud.ENVIADA,
                fechaCreacion = 1700000000000L
            )
        )

        val mockApi = mockk<ApiService>()
        coEvery { mockApi.solicitudes() } returns fakeSolicitudes

        val repo = SolicitudRepository(api = mockApi)

        runTest {
            repo.refrescar()
            val resultadoCliente1 = repo.solicitudesDe("cliente1@correo.cl").first()
            resultadoCliente1 shouldContainExactly listOf(fakeSolicitudes[0])
        }
    }
})