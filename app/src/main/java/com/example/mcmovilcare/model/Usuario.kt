package com.example.mcmovilcare.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true), Index(value = ["rut"], unique = true)]
)

data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val clave: String,
    val rol: Rol.CLIENTE,
    val empresa: String? = null,
    val fotoPath: String? = null,
)