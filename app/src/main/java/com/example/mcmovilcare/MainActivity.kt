package com.example.mcmovilcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mcmovilcare.navigation.NavigationEvent
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.screens.DetalleEquipoScreen
import com.example.mcmovilcare.screens.HomeScreen
import com.example.mcmovilcare.screens.LoginScreen
import com.example.mcmovilcare.screens.NuevaSolicitudScreen
import com.example.mcmovilcare.screens.OrdenScreen
import com.example.mcmovilcare.screens.RegistroScreen
import com.example.mcmovilcare.screens.WelcomeScreen
import com.example.mcmovilcare.viewmodels.EquiposViewModel
import com.example.mcmovilcare.viewmodels.MainViewModel
import com.example.mcmovilcare.viewmodels.PerfilViewModel
import com.example.mcmovilcare.viewmodels.SolicitudViewModel
import com.example.mcmovilcare.viewmodels.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val mainViewModel: MainViewModel = viewModel()
                    val usuarioViewModel: UsuarioViewModel = viewModel()
                    val equiposViewModel: EquiposViewModel = viewModel()
                    val solicitudViewModel: SolicitudViewModel = viewModel()
                    val perfilViewModel: PerfilViewModel = viewModel()
                    val navController = rememberNavController()

                    LaunchedEffect(Unit) {
                        mainViewModel.navigationEvents.collectLatest { event ->
                            when (event) {
                                is NavigationEvent.NavigateTo -> {
                                    navController.navigate(event.route.route) {
                                        event.popupToRoute?.let { popRoute ->
                                            popUpTo(popRoute.route) { inclusive = event.inclusive }
                                        }
                                        launchSingleTop = event.singleTop
                                        restoreState = true
                                    }
                                }
                                is NavigationEvent.PopBackStack -> navController.popBackStack()
                                is NavigationEvent.NavigateUp -> navController.navigateUp()
                            }
                        }
                    }

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Welcome.route,
                            modifier = Modifier.padding(innerPadding),
                            enterTransition = {
                                slideInHorizontally(animationSpec = tween(350)) { ancho -> ancho / 3 } +
                                        fadeIn(animationSpec = tween(350))
                            },
                            exitTransition = {
                                slideOutHorizontally(animationSpec = tween(350)) { ancho -> -ancho / 3 } +
                                        fadeOut(animationSpec = tween(250))
                            },
                            // Al volver atrás se hace el movimiento inverso
                            popEnterTransition = {
                                slideInHorizontally(animationSpec = tween(350)) { ancho -> -ancho / 3 } +
                                        fadeIn(animationSpec = tween(350))
                            },
                            popExitTransition = {
                                slideOutHorizontally(animationSpec = tween(350)) { ancho -> ancho / 3 } +
                                        fadeOut(animationSpec = tween(250))
                            }
                        ) {
                            composable(Screen.Welcome.route) { WelcomeScreen(mainViewModel) }
                            composable(Screen.Login.route) { LoginScreen(mainViewModel, usuarioViewModel) }
                            composable(Screen.Registro.route) { RegistroScreen(mainViewModel, usuarioViewModel) }
                            composable(Screen.Home.route) {
                                HomeScreen(mainViewModel, usuarioViewModel, equiposViewModel, solicitudViewModel, perfilViewModel)
                            }
                            composable(Screen.DetalleEquipo.route) { DetalleEquipoScreen(mainViewModel, equiposViewModel) }
                            composable(Screen.Orden.route) { OrdenScreen(mainViewModel, equiposViewModel) }
                            composable(Screen.NuevaSolicitud.route) {
                                NuevaSolicitudScreen(mainViewModel, usuarioViewModel, equiposViewModel, solicitudViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}