package com.example.mcmovilcare.viewmodels


import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcmovilcare.data.PreferenciasDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenciasDataStore(application)
    private var correoActual: String? = null

    private val _imagen = MutableStateFlow<Uri?>(null)
    val imagen: StateFlow<Uri?> = _imagen

    fun cargarFoto(correo: String) {
        correoActual = correo
        viewModelScope.launch {
            _imagen.value = prefs.obtenerFoto(correo).first()?.let { Uri.parse(it) }
        }
    }

    fun limpiar() {
        correoActual = null
        _imagen.value = null
    }

    fun actualizarDesdeGaleria(uri: Uri) = guardarCopiaLocal(uri)

    fun actualizarDesdeCamara(uri: Uri) = guardarCopiaLocal(uri)

    private fun guardarCopiaLocal(origen: Uri) {
        val correo = correoActual ?: return
        viewModelScope.launch {
            val destino = withContext(Dispatchers.IO) { copiar(origen) } ?: return@launch
            _imagen.value = destino
            prefs.guardarFoto(correo, destino.toString())
        }
    }

    private fun copiar(origen: Uri): Uri? {
        return try {
            val app = getApplication<Application>()
            val archivo = File(app.filesDir, "perfil_${System.currentTimeMillis()}.jpg")
            val entrada = app.contentResolver.openInputStream(origen) ?: return null
            entrada.use { input ->
                archivo.outputStream().use { salida -> input.copyTo(salida) }
            }
            Uri.fromFile(archivo)
        } catch (e: Exception) {
            null
        }
    }
}
