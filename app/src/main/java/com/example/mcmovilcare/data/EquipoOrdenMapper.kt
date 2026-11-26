package com.example.mcmovilcare.data

import com.example.mcmovilcare.model.Equipo
import com.example.mcmovilcare.model.EstadoAutorizacion
import com.example.mcmovilcare.model.EstadoOrden
import com.example.mcmovilcare.model.OrdenDeTrabajo
import org.json.JSONArray
import org.json.JSONObject

fun equiposAJson(lista: List<Equipo>): String {
    val arreglo = JSONArray()
    lista.forEach { e ->
        arreglo.put(JSONObject().apply {
            put("id", e.id)
            put("clienteCorreo", e.clienteCorreo)
            put("tipo", e.tipo)
            put("marca", e.marca)
            put("modelo", e.modelo)
            put("serie", e.serie)
            put("accesorios", e.accesorios)
            put("garantiaHasta", e.garantiaHasta)
        })
    }
    return arreglo.toString()
}

fun jsonAEquipos(texto: String?): List<Equipo> {
    if (texto.isNullOrBlank()) return emptyList()
    return try {
        val arreglo = JSONArray(texto)
        List(arreglo.length()) { i ->
            val o = arreglo.getJSONObject(i)
            Equipo(
                id = o.getInt("id"),
                clienteCorreo = o.getString("clienteCorreo"),
                tipo = o.getString("tipo"),
                marca = o.getString("marca"),
                modelo = o.getString("modelo"),
                serie = o.getString("serie"),
                accesorios = o.getString("accesorios"),
                garantiaHasta = o.getLong("garantiaHasta")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun ordenesAJson(lista: List<OrdenDeTrabajo>): String {
    val arreglo = JSONArray()
    lista.forEach { o ->
        arreglo.put(JSONObject().apply {
            put("id", o.id)
            put("equipoId", o.equipoId)
            put("estado", o.estado.name)
            put("diagnostico", o.diagnostico)
            put("cotizacionMonto", o.cotizacionMonto)
            put("cotizacionDetalle", o.cotizacionDetalle)
            put("autorizacion", o.autorizacion.name)
            if (o.comentarioAutorizacion != null) put("comentarioAutorizacion", o.comentarioAutorizacion)
        })
    }
    return arreglo.toString()
}

fun jsonAOrdenes(texto: String?): List<OrdenDeTrabajo> {
    if (texto.isNullOrBlank()) return emptyList()
    return try {
        val arreglo = JSONArray(texto)
        List(arreglo.length()) { i ->
            val o = arreglo.getJSONObject(i)
            OrdenDeTrabajo(
                id = o.getInt("id"),
                equipoId = o.getInt("equipoId"),
                estado = EstadoOrden.valueOf(o.getString("estado")),
                diagnostico = o.getString("diagnostico"),
                cotizacionMonto = o.getInt("cotizacionMonto"),
                cotizacionDetalle = o.getString("cotizacionDetalle"),
                autorizacion = EstadoAutorizacion.valueOf(o.getString("autorizacion")),
                comentarioAutorizacion = if (o.has("comentarioAutorizacion")) o.getString("comentarioAutorizacion") else null
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}