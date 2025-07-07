// Copia y pega esto en tu archivo build.gradle.kts a nivel de proyecto

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // El plugin de KSP se aplica usando el alias que definiremos en libs.versions.toml
    alias(libs.plugins.ksp) apply false
}
