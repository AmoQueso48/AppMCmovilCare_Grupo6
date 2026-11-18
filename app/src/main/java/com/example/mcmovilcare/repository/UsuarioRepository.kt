package com.example.mcmovilcare.repository

import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.model.Usuario
import kotlinx.coroutines.flow.first
import com.example.mcmovilcare.utils.ClaveSegura

class UsuarioRepository(private val prefs: PreferenciasDataStore) {

    private val usuariosDemo = listOf(
        Usuario("Agnes Tachyon", "cafeeee@mcmovil.com", "+56910102013","AQIDBAUGBwgJCgsMDQ4PEA==:/PwWj26yKQBMwVFYN4+ysOB8RN+EATPfdEu6GkJnmpU="),
        Usuario("Cafe Manhattan", "cafe@mcmovil.com", "+56919832018", "ZWZnaGlqa2xtbm9wcXJzdA==:Qc7KWkQliwz9grt7MG5QVAZz68lFoRuyqOMhFVKyKFo=")
    )

    private suspend fun todos(): List<Usuario> =
        usuariosDemo + listOfNotNull(prefs.obtenerUsuarioRegistrado().first())

    suspend fun usuarioPorCorreo(correo: String): Usuario? =
        todos().firstOrNull { it.correo.equals(correo, ignoreCase = true) }

    suspend fun login(correo: String, clave: String): Usuario? =
        todos().firstOrNull {
            it.correo.equals(correo.trim(), ignoreCase = true) && ClaveSegura.verificar(clave, it.clave)
        }

    suspend fun registrar(usuario: Usuario): Boolean {
        if (usuarioPorCorreo(usuario.correo) != null) return false
        prefs.guardarUsuario(usuario.copy(clave = ClaveSegura.crear(usuario.clave)))   // se guarda el hash
        return true
    }
    suspend fun guardarSesion(correo: String?) = prefs.guardarSesion(correo)

    suspend fun sesionGuardada(): String? = prefs.obtenerSesion().first()
}
