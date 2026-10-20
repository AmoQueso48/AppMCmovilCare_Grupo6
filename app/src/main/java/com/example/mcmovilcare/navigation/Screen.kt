package com.example.mcmovilcare.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen(route = "welcome_screen")
    data object Login : Screen(route = "login_screen")
    data object Home : Screen(route = "home_screen")
    data object Detail : Screen(route = "detail_screen")
    data object Form : Screen(route = "form_screen")
}