import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.kidor.vigik.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.secret.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "vigik.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "vigik.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplicationJacoco") {
            id = "vigik.android.application.jacoco"
            implementationClass = "AndroidApplicationJacocoConventionPlugin"
        }
        register("androidHilt") {
            id = "vigik.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "vigik.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("detekt") {
            id = "vigik.detekt"
            implementationClass = "DetektConventionPlugin"
        }
        register("kover") {
            id = "vigik.kover"
            implementationClass = "KoverConventionPlugin"
        }
        register("secret") {
            id = "vigik.secret"
            implementationClass = "SecretConventionPlugin"
        }
    }
}
