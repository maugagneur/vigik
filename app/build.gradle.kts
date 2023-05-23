plugins {
    id("vigik.android.application")
    id("vigik.android.application.compose")
    id("vigik.android.application.jacoco")
    id("vigik.android.hilt")
    id("vigik.android.room")
    id("vigik.detekt")
    id("vigik.diktat")
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.kidor.vigik.utils.HiltTestRunner"
    }

    buildTypes {
        getByName("debug") {
            enableUnitTestCoverage = true
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // Annotation processor
    implementation(libs.ksp.api)

    // UI
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.bundles.androidx.lifecycle)

    // Kotlin
    implementation(libs.bundles.kotlinx.coroutines)
    testImplementation(libs.kotlinx.coroutines.test)

    // Material
    implementation(libs.android.material)

    // Log
    implementation(libs.timber)

    // Test
    testImplementation(libs.bundles.test.unit)

    // Testing Navigation
    // debugImplementation is used here so that the empty activity that FragmentScenario relies on is accessible by the test target process.
    debugImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.androidx.navigation.testing)
}
