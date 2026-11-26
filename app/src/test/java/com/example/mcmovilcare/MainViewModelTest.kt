package com.example.mcmovilcare

import com.example.mcmovilcare.navigation.NavigationEvent
import com.example.mcmovilcare.navigation.Screen
import com.example.mcmovilcare.viewmodels.MainViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : StringSpec({

    val testDispatcher = UnconfinedTestDispatcher()

    beforeTest {
        Dispatchers.setMain(testDispatcher)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "navigateTo() debe emitir un evento NavigationEvent.NavigateTo con los parámetros indicados" {
        val viewModel = MainViewModel()
        val destino = mockk<Screen>(relaxed = true)
        val eventosRecibidos = mutableListOf<NavigationEvent>()

        runTest(testDispatcher) {
            val job = backgroundScope.launch(testDispatcher) {
                viewModel.navigationEvents.collect { eventosRecibidos.add(it) }
            }

            viewModel.navigateTo(screen = destino, singleTop = true)

            eventosRecibidos.size shouldBe 1
            val evento = eventosRecibidos.first() as NavigationEvent.NavigateTo
            evento.route shouldBe destino
            evento.singleTop shouldBe true
            evento.inclusive shouldBe false

            job.cancel()
        }
    }

    "navigateBack() debe emitir un evento NavigationEvent.PopBackStack" {
        val viewModel = MainViewModel()
        val eventosRecibidos = mutableListOf<NavigationEvent>()

        runTest(testDispatcher) {
            val job = backgroundScope.launch(testDispatcher) {
                viewModel.navigationEvents.collect { eventosRecibidos.add(it) }
            }

            viewModel.navigateBack()

            eventosRecibidos.size shouldBe 1
            eventosRecibidos.first() shouldBe NavigationEvent.PopBackStack

            job.cancel()
        }
    }
})