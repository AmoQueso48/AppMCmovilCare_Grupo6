package com.example.mcmovilcare.data.remote

data class LoginBody(val correo: String, val clave: String)
data class RegistroBody(val nombre: String, val correo: String, val telefono: String, val clave: String)
data class UsuarioDto(val nombre: String, val correo: String, val telefono: String, val rol: String)
data class LoginRespuesta(val token: String, val usuario: UsuarioDto)
data class SolicitudBody(val equipoId: Int, val tipoServicio: String, val prioridad: String, val descripcion: String)
data class EstadoBody(val estado: String)
data class AutorizacionBody(val aprobar: Boolean, val comentario: String?)