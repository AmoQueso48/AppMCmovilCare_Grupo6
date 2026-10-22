package com.example.mcmovilcare.repository

import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.model.Usuario
import kotlinx.coroutines.flow.first

class UsuarioRepository(private val prefs: PreferenciasDataStore) {

    private val usuariosDemo = listOf(
        Usuario("Agnes Tachyon", "cafeeee@mcmovil.com", "+56910102013", "cafeee1234"),
        Usuario("Cafe Manhattan", "cafe@mcmovil.com", "+56919832018", "cream-coffee")
    )

    private suspend fun todos(): List<Usuario> =
        usuariosDemo + listOfNotNull(prefs.obtenerUsuarioRegistrado().first())

    suspend fun login(correo: String, clave: String): Usuario? =
        todos().firstOrNull { it.correo.equals(correo.trim(), ignoreCase = true) && it.clave == clave }

    suspend fun usuarioPorCorreo(correo: String): Usuario? =
        todos().firstOrNull { it.correo.equals(correo, ignoreCase = true) }

    suspend fun registrar(usuario: Usuario): Boolean {
        if (usuarioPorCorreo(usuario.correo) != null) return false
        prefs.guardarUsuario(usuario)
        return true
    }

    suspend fun guardarSesion(correo: String?) = prefs.guardarSesion(correo)

    suspend fun sesionGuardada(): String? = prefs.obtenerSesion().first()
}
