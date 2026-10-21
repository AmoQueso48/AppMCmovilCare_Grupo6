package com.example.mcmovilcare.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mantenciones",
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
data class Mantencion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipoId: Long,
    val fecha: Long,
    val tipo: String
)