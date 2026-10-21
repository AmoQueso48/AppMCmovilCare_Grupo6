package com.example.mcmovilcare.model

enum class Rol {CLIENTE, TECNICO, ADMIN}

enum class EstadoOrden(val etiqueta: String, val progreso: Float) {
    INGRESADO("Ingresado", 0.10f),
    DIAGNOSTICO("En diagnostico", 0.25f),
    COTIZADO("Cotizado", 0.40f),
    AUTORIZADO("Autorizado", 0.55f),
    EN_REPARACION("En reparacion", 0.75f),
    LISTO("Listo para retiro", 0.90f),
    ENTREGADO("Entregado", 1.00f)
}

enum class EstadoAutorizacion(val etiqueta: String) {
    NO_APLICA("Sin cotizacion"),
    PENDIENTE("Quedo pendiente de respuesta"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada")
}

enum class EstadoSolicitud(val etiqueta: String) {
    ENVIADA("Enviada"),
    EN_REVISION("Esta en estado de revision"),
    ATENDIDA("Atendida"),
    CANCELADA("Cancelada")
}