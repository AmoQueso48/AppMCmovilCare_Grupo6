package com.example.mcmovilcare.model

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val clave: String = "",
    val repetirClave: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: RegistroErrores = RegistroErrores()
)

data class RegistroErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val clave: String? = null,
    val repetirClave: String? = null,
    val terminos: String? = null
)
