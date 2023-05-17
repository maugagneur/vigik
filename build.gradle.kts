// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.43.2")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.22.0")
        classpath("org.cqfn.diktat:diktat-gradle-plugin:1.2.5")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

// Lists all plugins used throughout the project without applying them.
plugins {

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
