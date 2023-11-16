// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

// Lists all plugins used throughout the project without applying them.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.diktat) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
