package com.example.mcmovilcare.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mcmovilcare.model.SolicitudDeAtencion
import com.example.mcmovilcare.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "preferencias_Mcmovil")

class PreferenciasDataStore(private val context: Context) {
    private val NOMBRE = stringPreferencesKey("usuario_nombre")
    private val CORREO = stringPreferencesKey("usuario_correo")
    private val TELEFONO = stringPreferencesKey("usuario_telefono")
    private val CLAVE = stringPreferencesKey("usuario_clave")
    private val SESION = stringPreferencesKey("sesion_correo")
    private val SOLICITUDES = stringPreferencesKey("solicitudes_json")
    private fun claveFoto(correo: String) = stringPreferencesKey("foto_" + correo.trim().lowercase())

    suspend fun guardarUsuario(usuario: Usuario) {
        context.dataStore.edit { p ->
            p[NOMBRE] = usuario.nombre
            p[CORREO] = usuario.correo
            p[TELEFONO] = usuario.telefono
            p[CLAVE] = usuario.clave
        }
    }

    fun obtenerUsuarioRegistrado(): Flow<Usuario?> =
        context.dataStore.data.map { p ->
            val correo = p[CORREO] ?: return@map null
            Usuario(p[NOMBRE] ?: "", correo, p[TELEFONO] ?: "", p[CLAVE] ?: "")
        }

    suspend fun guardarSesion(correo: String?) {
        context.dataStore.edit { p ->
            if (correo == null) p.remove(SESION) else p[SESION] = correo
        }
    }

    fun obtenerSesion(): Flow<String?> = context.dataStore.data.map { it[SESION] }

    suspend fun guardarFoto(correo: String, uri: String?) {
        val clave = claveFoto(correo)
        context.dataStore.edit { p ->
            if (uri == null) p.remove(clave) else p[clave] = uri
        }
    }

    fun obtenerFoto(correo: String): Flow<String?> {
        val clave = claveFoto(correo)
        return context.dataStore.data.map { it[clave] }
    }

    fun observarSolicitudes(): Flow<List<SolicitudDeAtencion>> =
        context.dataStore.data.map { jsonASolicitudes(it[SOLICITUDES]) }.distinctUntilChanged()

    suspend fun guardarSolicitudes(lista: List<SolicitudDeAtencion>) {
        context.dataStore.edit { p -> p[SOLICITUDES] = solicitudesAJson(lista) }
    }
}