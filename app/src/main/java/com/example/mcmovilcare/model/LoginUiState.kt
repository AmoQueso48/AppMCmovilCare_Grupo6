package com.example.mcmovilcare.model

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errorGeneral: String? = null,
    val errores: LoginErrores = LoginErrores()
)

data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null
)
