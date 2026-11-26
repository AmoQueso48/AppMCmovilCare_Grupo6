package com.example.mcmovilcare.data.remote

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

fun Throwable.mensajeApi(): String = when (this) {
    is HttpException -> {
        val cuerpo = response()?.errorBody()?.string().orEmpty()
        runCatching { JSONObject(cuerpo).getString("error") }.getOrNull()
            ?: "Error del servidor (${code()})"
    }
    is IOException -> "No se pudo conectar con el servidor. ¿Está encendida la API?"
    else -> message ?: "Error inesperado"
}
