package com.example.mcmovilcare.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "equipos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario:: class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = @ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)

data class Equipo (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteID: Long,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val serie: String,
    val accesorios: String,
    val garentiaHasta: Long
) {
    fun garantiaVigente (ahorita: Long = System.currentTimeMillis()): Boolean = garentiaHasta >= ahorita
}