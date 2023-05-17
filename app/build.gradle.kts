plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp").version("1.8.21-1.0.11")
    id("jacoco")
    id("io.gitlab.arturbosch.detekt")
    id("org.cqfn.diktat.diktat-gradle-plugin")
}

android {
    compileSdk = 33

    defaultConfig {
        namespace = "com.kidor.vigik"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0-alpha.1"

        testInstrumentationRunner = "com.kidor.vigik.utils.HiltTestRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

    buildFeatures {
        compose = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.jacoco") {
                    useVersion("0.8.8")
                }
            }
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

jacoco {
    toolVersion = "0.8.8"
    reportsDirectory.set(file("$buildDir/reports/customJacocoReport"))
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        // Related issue https://github.com/gradle/gradle/issues/5184#issuecomment-457865951
        excludes = listOf("jdk.internal.*")
    }
}

detekt {
    toolVersion = "1.22.0"
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config = files("$projectDir/config/detekt/config.yml") // point to your custom config defining rules to run, overwriting default behavior
    //baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
    ignoreFailures = true // If set to `true` the build does not fail when the maxIssues count was reached.
}

diktat {
    reporter = "html" // "html", "json", "plain" (default), "sarif"
    output = "$project.projectDir/build/reports/diktat/diktat.html"
    ignoreFailures = true
}

// Custom task to merge code coverage reports from unit and instrumentation tests
//task jacocoTestReport(type: JacocoReport, dependsOn: ['testDebugUnitTest', 'createDebugCoverageReport']) {
//    reports {
//        html.required = true
//    }
//
//    def fileFilter = [
//            '**/R.class',
//            '**/R$*.class',
//            '**/BuildConfig.*',
//            '**/Manifest*.*',
//            '**/*Test*.*',
//            '**/*Args*.*',          // filtering Navigation Component generated classes
//            '**/*Directions*.*',    // filtering Navigation Component generated classes
//            '**/databinding/**',    // filtering Data Binding generated classes
//            'android/**/*.*'
//    ]
//    def debugTree = fileTree(dir: "$project.buildDir/intermediates/javac/debug", excludes: fileFilter)
//    def mainSrc = "$project.projectDir/src/main/java"
//
//    getSourceDirectories().setFrom(files([mainSrc]))
//    getClassDirectories().setFrom(files([debugTree]))
//    getExecutionData().setFrom(
//            fileTree(dir: project.buildDir, includes: [
//                    'outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec',
//                    'outputs/code_coverage/debugAndroidTest/connected/**/*.ec'
//            ])
//    )
//}

dependencies {
    // Annotation processor
    implementation(libs.ksp.api)

    // UI
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.compose.navigation)
    implementation(libs.bundles.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Database
    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)

    // Kotlin
    implementation(libs.bundles.kotlinx.coroutines)
    testImplementation(libs.kotlinx.coroutines.test)

    // Material
    implementation(libs.android.material)

    // Dependency Injection
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)

    // Log
    implementation(libs.timber)

    // Test
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.android)
    kaptAndroidTest(libs.dagger.hilt.android.compiler)
    kaptAndroidTest(libs.androidx.hilt.compiler)

    // Testing Navigation
    // debugImplementation is used here so that the empty activity that FragmentScenario relies on is accessible by the test target process.
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.androidx.navigation.testing)

    // Static code analysis
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.22.0")
}
