package com.example.app_vehiculos.function

import java.security.MessageDigest

fun hashSHA256(text: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(text.toByteArray(Charsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
}
