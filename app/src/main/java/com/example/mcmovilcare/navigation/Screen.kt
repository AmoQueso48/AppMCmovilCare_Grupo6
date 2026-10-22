package com.example.mcmovilcare.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen(route = "welcome_screen")
    data object Login : Screen(route = "login_screen")
    data object Registro : Screen(route = "registro_screen")
    data object Home : Screen(route = "home_screen")
    data object DetalleEquipo : Screen(route = "detalle_equipo_screen")
    data object Orden : Screen(route = "orden_screen")
    data object NuevaSolicitud : Screen(route = "nueva_solicitud_screen")
}