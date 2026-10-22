package com.example.mcmovilcare.repository

import com.example.mcmovilcare.model.EstadoAutorizacion
import com.example.mcmovilcare.model.EstadoOrden
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.Mantencion
import com.example.mcmovilcare.model.OrdenDeTrabajo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EquipoRepository {

    private val dia = 24L * 60L * 60L * 1000L
    private val ahora = System.currentTimeMillis()

    private val equipos = listOf(
        Equipo(1, "cafeeee@mcmovil.com", "Notebook", "Lenovo", "ThinkPad E14", "LNV-0031", "Cargador", ahora + 90 * dia),
        Equipo(2, "cafeeee@mcmovil.com", "Smartphone", "Samsung", "Galaxy A54", "SMG-7782", "Funda", ahora - 30 * dia), // garantía vencida
        Equipo(3, "cafe@mcmovil.com", "Impresora", "HP", "LaserJet M404", "HP-5510", "Cable de poder", ahora + 200 * dia)
    )

    private val mantenciones = listOf(
        Mantencion(1, 1, ahora + 20 * dia, "Limpieza preventiva"),
        Mantencion(2, 1, ahora - 60 * dia, "Cambio de pasta termica"),
        Mantencion(3, 3, ahora + 45 * dia, "Revision de rodillos")
    )

    private val _ordenes = MutableStateFlow(
        listOf(
            OrdenDeTrabajo(1, 1, EstadoOrden.COTIZADO, "Disco con sectores dañados", 85000, "SSD 512 GB + instalación", EstadoAutorizacion.PENDIENTE),
            OrdenDeTrabajo(2, 2, EstadoOrden.ENTREGADO, "Pantalla rota", 60000, "Cambio de pantalla", EstadoAutorizacion.APROBADA),
            OrdenDeTrabajo(3, 3, EstadoOrden.EN_REPARACION, "Atasco de papel", 25000, "Limpieza y rodillos", EstadoAutorizacion.APROBADA)
        )
    )
    val ordenes: StateFlow<List<OrdenDeTrabajo>> = _ordenes

    fun equiposDe(correo: String): List<Equipo> = equipos.filter { it.clienteCorreo == correo }

    fun mantencionesDe(equipoId: Int): List<Mantencion> =
        mantenciones.filter { it.equipoId == equipoId }.sortedBy { it.fecha }

    fun responderAutorizacion(ordenId: Int, aprobar: Boolean, comentario: String): Boolean {
        val orden = _ordenes.value.find { it.id == ordenId } ?: return false
        val puede = orden.estado == EstadoOrden.COTIZADO && orden.autorizacion == EstadoAutorizacion.PENDIENTE
        if (!puede) return false

        val actualizada = if (aprobar) {
            orden.copy(
                estado = EstadoOrden.AUTORIZADO,
                autorizacion = EstadoAutorizacion.APROBADA,
                comentarioAutorizacion = comentario.trim().ifBlank { null }
            )
        } else {
            orden.copy(autorizacion = EstadoAutorizacion.RECHAZADA, comentarioAutorizacion = comentario.trim())
        }
        _ordenes.update { lista -> lista.map { if (it.id == ordenId) actualizada else it } }
        return true
    }
}
