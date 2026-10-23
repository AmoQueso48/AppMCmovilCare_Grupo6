package com.example.mcmovilcare.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatearFecha(millis: Long): String = SimpleDateFormat(
    "dd/MM/yyyy", Locale.forLanguageTag("es-CL")
).format(Date(millis))
