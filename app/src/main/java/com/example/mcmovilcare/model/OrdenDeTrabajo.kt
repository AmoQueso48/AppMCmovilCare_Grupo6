package com.example.mcmovilcare.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "ordenes",
    foreignKeys = [
        ForeignKey(
            entity = Equipo::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("equipoId")]
)

data class OrdenDeTrabajo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipoId: Long,
    val estado: EstadoOrden,
    val diagnostico: String,
    val fechaIngreso: Long,
    val cotizacionMonto: Int,
    val cotizacionDetalle: String,
    val autorizacion: EstadoAutorizacion,
    val comentarioAutorizacion: String? = null
)

data class OrdenConEquipo(
    @Embedded val orden: OrdenDeTrabajo,
    @Relation(parentColumn = "equipoId", entityColumn = "id")
    val equipo: Equipo
)