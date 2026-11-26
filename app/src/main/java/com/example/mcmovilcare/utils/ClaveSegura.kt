package com.example.mcmovilcare.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object ClaveSegura {
    private const val ITERACIONES = 10_000
    private const val LARGO_BITS = 256
    private const val ALGORITMO = "PBKDF2WithHmacSHA1"

    fun crear(clave: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return codificar(sal) + ":" + codificar(derivar(clave, sal))
    }

    fun verificar(clave: String, almacenada: String): Boolean {
        return try {
            val partes = almacenada.split(":")
            if (partes.size != 2) return false
            val sal = Base64.decode(partes[0], Base64.NO_WRAP)
            val esperado = Base64.decode(partes[1], Base64.NO_WRAP)
            MessageDigest.isEqual(derivar(clave, sal), esperado)
        } catch (e: Exception) {
            false
        }
    }

    private fun derivar(clave: String, sal: ByteArray): ByteArray {
        val spec = PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, LARGO_BITS)
        return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
    }

    private fun codificar(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
}
