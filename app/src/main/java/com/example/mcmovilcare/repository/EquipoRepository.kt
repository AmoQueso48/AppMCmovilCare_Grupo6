package com.example.mcmovilcare.repository

import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.EstadoAutorizacion
import com.example.mcmovilcare.model.EstadoOrden
import com.example.mcmovilcare.model.Mantencion
import com.example.mcmovilcare.model.OrdenDeTrabajo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EquipoRepository(private val prefs: PreferenciasDataStore) {

    private val dia = 24L * 60L * 60L * 1000L
    private val ahora = System.currentTimeMillis()

    private val equiposIniciales = listOf(
        Equipo(1, "cafeeee@mcmovil.com", "Notebook", "Lenovo", "ThinkPad E14", "LNV-0031", "Cargador", ahora + 90 * dia),
        Equipo(2, "cafeeee@mcmovil.com", "Smartphone", "Samsung", "Galaxy A54", "SMG-7782", "Funda", ahora - 30 * dia),
        Equipo(3, "cafe@mcmovil.com", "Impresora", "HP", "LaserJet M404", "HP-5510", "Cable de poder", ahora + 200 * dia)
    )

    private val ordenesIniciales = listOf(
        OrdenDeTrabajo(1, 1, EstadoOrden.COTIZADO, "Disco con sectores dañados", 85000, "SSD 512 GB + instalación", EstadoAutorizacion.PENDIENTE),
        OrdenDeTrabajo(2, 2, EstadoOrden.ENTREGADO, "Pantalla rota", 60000, "Cambio de pantalla", EstadoAutorizacion.APROBADA),
        OrdenDeTrabajo(3, 3, EstadoOrden.EN_REPARACION, "Atasco de papel", 25000, "Limpieza y rodillos", EstadoAutorizacion.APROBADA)
    )

    // Las mantenciones siguen fijas (la app no las modifica)
    private val mantenciones = listOf(
        Mantencion(1, 1, ahora + 20 * dia, "Limpieza preventiva"),
        Mantencion(2, 1, ahora - 60 * dia, "Cambio de pasta térmica"),
        Mantencion(3, 3, ahora + 45 * dia, "Revisión de rodillos")
    )

    val ordenes: Flow<List<OrdenDeTrabajo>> = prefs.observarOrdenes()

    suspend fun sembrarSiVacio() = prefs.sembrarSiVacio(equiposIniciales, ordenesIniciales)

    suspend fun equiposDe(correo: String): List<Equipo> =
        prefs.observarEquipos().first().filter { it.clienteCorreo == correo }

    fun mantencionesDe(equipoId: Int): List<Mantencion> =
        mantenciones.filter { it.equipoId == equipoId }.sortedBy { it.fecha }

    suspend fun responderAutorizacion(ordenId: Int, aprobar: Boolean, comentario: String): Boolean {
        val actuales = prefs.observarOrdenes().first()
        val orden = actuales.find { it.id == ordenId } ?: return false
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
        prefs.guardarOrdenes(actuales.map { if (it.id == ordenId) actualizada else it })
        return true
    }
}