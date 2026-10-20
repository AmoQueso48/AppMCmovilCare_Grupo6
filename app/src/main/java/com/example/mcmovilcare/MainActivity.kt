package com.example.mcmovilcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.mcmovilcare.screens.DetailScreen
import com.example.mcmovilcare.screens.FormScreen
import com.example.mcmovilcare.screens.HomeScreen
import com.example.mcmovilcare.screens.LoginScreen
import com.example.mcmovilcare.screens.WelcomeScreen
import com.example.mcmovilcare.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: MainViewModel = viewModel()
                    val navController = rememberNavController()

                    // Escucha los eventos de navegación emitidos por el ViewModel
                    LaunchedEffect(Unit) {
                        viewModel.navigationEvents.collectLatest { event ->
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
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Welcome.route) { WelcomeScreen(viewModel) }
                            composable(Screen.Login.route) { LoginScreen(viewModel) }
                            composable(Screen.Home.route) { HomeScreen(viewModel) }
                            composable(Screen.Detail.route) { DetailScreen(viewModel) }
                            composable(Screen.Form.route) { FormScreen(viewModel) }
                        }
                    }
                }
            }
        }
    }
}