package com.example.mcmovilcare.data

import com.example.mcmovilcare.model.EstadoSolicitud
import com.example.mcmovilcare.model.SolicitudDeAtencion
import org.json.JSONArray
import org.json.JSONObject

fun solicitudesAJson(lista: List<SolicitudDeAtencion>): String {
    val arreglo = JSONArray()
    lista.forEach { s ->
        val obj = JSONObject()
        obj.put("id", s.id)
        obj.put("clienteCorreo", s.clienteCorreo)
        if (s.equipoId != null) obj.put("equipoId", s.equipoId)
        obj.put("tipoServicio", s.tipoServicio)
        obj.put("prioridad", s.prioridad)
        obj.put("descripcion", s.descripcion)
        obj.put("estado", s.estado.name)
        obj.put("fechaCreacion", s.fechaCreacion)
        arreglo.put(obj)
    }
    return arreglo.toString()
}

fun jsonASolicitudes(texto: String?): List<SolicitudDeAtencion> {
    if (texto.isNullOrBlank()) return emptyList()
    return try {
        val arreglo = JSONArray(texto)
        List(arreglo.length()) { i ->
            val o = arreglo.getJSONObject(i)
            SolicitudDeAtencion(
                id = o.getInt("id"),
                clienteCorreo = o.getString("clienteCorreo"),
                equipoId = if (o.has("equipoId")) o.getInt("equipoId") else null,
                tipoServicio = o.getString("tipoServicio"),
                prioridad = o.getString("prioridad"),
                descripcion = o.getString("descripcion"),
                estado = EstadoSolicitud.valueOf(o.getString("estado")),
                fechaCreacion = o.getLong("fechaCreacion")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}
