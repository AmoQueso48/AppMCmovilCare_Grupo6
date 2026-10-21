package com.example.mcmovilcare.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "solicitudes",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)

data class SolicitudDeAtencion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val equipoId: Long?,
    val tipoServicio: String,
    val descripcion: String,
    val prioridad: String,
    val fechaPreferida: Long,
    val fotoPath: String? = null,
    val estado: EstadoSolicitud = EstadoSolicitud.ENVIADA,
    val fechaCreacion: Long = System.currentTimeMillis()
)