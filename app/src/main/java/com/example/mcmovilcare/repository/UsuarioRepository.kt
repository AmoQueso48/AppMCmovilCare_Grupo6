package com.example.mcmovilcare.repository

import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.data.remote.LoginBody
import com.example.mcmovilcare.data.remote.RegistroBody
import com.example.mcmovilcare.data.remote.RetrofitInstance
import com.example.mcmovilcare.data.remote.SesionApi
import com.example.mcmovilcare.model.Usuario
import kotlinx.coroutines.flow.first

class UsuarioRepository(private val prefs: PreferenciasDataStore) {
    private val api = RetrofitInstance.api
    suspend fun login(correo: String, clave: String): Result<Usuario> = runCatching {
        val respuesta = api.login(LoginBody(correo.trim(), clave))
        SesionApi.token = respuesta.token
        prefs.guardarToken(respuesta.token)
        val u = respuesta.usuario
        val usuario = Usuario(u.nombre, u.correo, u.telefono, clave = "")
        prefs.guardarUsuario(usuario)
        usuario
    }
    suspend fun registrar(usuario: Usuario): Result<Unit> = runCatching {
        api.registrar(RegistroBody(usuario.nombre, usuario.correo, usuario.telefono, usuario.clave))
        Unit
    }
    suspend fun usuarioPorCorreo(correo: String): Usuario? =
        prefs.obtenerUsuarioRegistrado().first()
            ?.takeIf { it.correo.equals(correo, ignoreCase = true) }
    suspend fun restaurarSesion(): Usuario? {
        val correo = prefs.obtenerSesion().first() ?: return null
        SesionApi.token = prefs.obtenerToken().first()
        return usuarioPorCorreo(correo)
    }
    suspend fun guardarSesion(correo: String?) = prefs.guardarSesion(correo)
    suspend fun cerrarSesion() {
        prefs.guardarSesion(null)
        prefs.guardarToken(null)
        SesionApi.token = null
    }
}