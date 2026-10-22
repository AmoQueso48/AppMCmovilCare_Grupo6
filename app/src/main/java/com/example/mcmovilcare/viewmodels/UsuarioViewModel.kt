package com.example.mcmovilcare.viewmodels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcmovilcare.data.PreferenciasDataStore
import com.example.mcmovilcare.model.LoginErrores
import com.example.mcmovilcare.model.LoginUiState
import com.example.mcmovilcare.model.RegistroErrores
import com.example.mcmovilcare.model.RegistroUiState
import com.example.mcmovilcare.model.Usuario
import com.example.mcmovilcare.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UsuarioRepository(PreferenciasDataStore(application))

    // ───────── Sesión ─────────
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        viewModelScope.launch {
            delay(1000)
            repo.sesionGuardada()?.let { _usuarioActual.value = repo.usuarioPorCorreo(it) }
            _cargando.value = false
        }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            repo.guardarSesion(null)
            _usuarioActual.value = null
            onListo()
        }
    }

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    fun onLoginCorreoChange(valor: String) {
        _login.update { it.copy(correo = valor, errorGeneral = null, errores = it.errores.copy(correo = null)) }
    }

    fun onLoginClaveChange(valor: String) {
        _login.update { it.copy(clave = valor, errorGeneral = null, errores = it.errores.copy(clave = null)) }
    }

    fun validarLogin(): Boolean {
        val e = _login.value
        val errores = LoginErrores(
            correo = when {
                e.correo.isBlank() -> "Campo obligatorio"
                !e.correo.contains("@") -> "Correo inválido"
                else -> null
            },
            clave = if (e.clave.isBlank()) "Ingresa tu clave" else null
        )
        val hayErrores = listOfNotNull(errores.correo, errores.clave).isNotEmpty()
        _login.update { it.copy(errores = errores) }
        return !hayErrores
    }

    fun iniciarSesion(onExito: () -> Unit) {
        if (!validarLogin()) return
        viewModelScope.launch {
            val e = _login.value
            val usuario = repo.login(e.correo, e.clave)
            if (usuario == null) {
                _login.update { it.copy(errorGeneral = "Correo o clave incorrectos") }
            } else {
                repo.guardarSesion(usuario.correo)
                _usuarioActual.value = usuario
                _login.value = LoginUiState()
                onExito()
            }
        }
    }

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    fun onRegistroNombreChange(valor: String) {
        _registro.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }
    fun onRegistroCorreoChange(valor: String) {
        _registro.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }
    fun onRegistroTelefonoChange(valor: String) {
        _registro.update { it.copy(telefono = valor, errores = it.errores.copy(telefono = null)) }
    }
    fun onRegistroClaveChange(valor: String) {
        _registro.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }
    fun onRegistroRepetirClaveChange(valor: String) {
        _registro.update { it.copy(repetirClave = valor, errores = it.errores.copy(repetirClave = null)) }
    }
    fun onRegistroTerminosChange(valor: Boolean) {
        _registro.update { it.copy(aceptaTerminos = valor, errores = it.errores.copy(terminos = null)) }
    }

    fun validarRegistro(): Boolean {
        val e = _registro.value
        val digitos = e.telefono.trim().removePrefix("+")
        val errores = RegistroErrores(
            nombre = when {
                e.nombre.isBlank() -> "Campo obligatorio"
                e.nombre.trim().length < 3 -> "Ingresa al menos 3 caracteres"
                e.nombre.any { it.isDigit() } -> "El nombre no puede contener números"
                else -> null
            },
            correo = when {
                e.correo.isBlank() -> "Campo obligatorio"
                !e.correo.contains("@") || !e.correo.contains(".") -> "Correo inválido"
                else -> null
            },
            telefono = when {
                e.telefono.isBlank() -> "Campo obligatorio"
                !digitos.all { it.isDigit() } -> "Solo puede contener números"
                digitos.length !in 9..11 -> "Debe tener entre 9 y 11 dígitos"
                else -> null
            },
            clave = when {
                e.clave.isBlank() -> "Campo obligatorio"
                e.clave.length < 6 -> "Debe tener al menos 6 caracteres"
                e.clave.none { it.isDigit() } -> "Debe incluir al menos un numero"
                else -> null
            },
            repetirClave = when {
                e.repetirClave.isBlank() -> "Repite tu clave"
                e.repetirClave != e.clave -> "Las claves no coinciden"
                else -> null
            },
            terminos = if (!e.aceptaTerminos) "Debes aceptar los términos" else null
        )
        val hayErrores = listOfNotNull(
            errores.nombre, errores.correo, errores.telefono,
            errores.clave, errores.repetirClave, errores.terminos
        ).isNotEmpty()
        _registro.update { it.copy(errores = errores) }
        return !hayErrores
    }

    fun registrarse(onExito: () -> Unit) {
        if (_registroExitoso.value) return
        if (!validarRegistro()) return
        viewModelScope.launch {
            val e = _registro.value
            val ok = repo.registrar(Usuario(e.nombre.trim(), e.correo.trim().lowercase(), e.telefono.trim(), e.clave))
            if (ok) {
                _registroExitoso.value = true
                delay(1500)
                _registroExitoso.value = false
                _registro.value = RegistroUiState()
                onExito()
            } else {
                _registro.update { it.copy(errores = it.errores.copy(correo = "Este correo ya está registrado")) }
            }
        }
    }
}
