plugins {
    id("vigik.android.application")
    id("vigik.android.application.compose")
    id("vigik.android.application.jacoco")
    id("vigik.android.hilt")
    id("vigik.android.room")
    id("vigik.detekt")
    id("vigik.diktat")
}

dependencies {
    // Annotation processor
    implementation(libs.ksp.api)

    // UI
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.emoji2.emojipicker)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.bundles.androidx.lifecycle)

    // Car
    implementation(libs.bundles.androidx.car)

    // Biometric
    implementation(libs.androidx.biometric.ktx)

    // Camera
    implementation(libs.bundles.androidx.camera)

    // Image loader
    implementation(libs.coil.compose)

    // Kotlin
    implementation(libs.bundles.kotlinx.coroutines)
    testImplementation(libs.kotlinx.coroutines.test)

    // Material
    implementation(libs.android.material)

    // Log
    implementation(libs.timber)

    // Network calls
    implementation(libs.bundles.retrofit)

    // Shared preferences
    implementation(libs.androidx.datastore.preferences)

    // Test
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)

    // Widget
    implementation(libs.androidx.glance.appwidget)

    // Worker
    implementation(libs.androidx.work.runtime.ktx)

    // Testing Navigation
    // debugImplementation is used here so that the empty activity that FragmentScenario relies on is accessible by the test target process.
    debugImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.androidx.navigation.testing)
}
