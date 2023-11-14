import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val COMPILE_SDK_VER = 34
private const val TARGET_SDK_VER = 34
private const val MIN_SDK_VER = 23

private const val APP_MAJOR_VERSION = 2
private const val APP_MINOR_VERSION = 9
private const val APP_FIX_VERSION = 0

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = COMPILE_SDK_VER
                defaultConfig.apply {
                    targetSdk = TARGET_SDK_VER
                    minSdk = MIN_SDK_VER
                    namespace = "com.kidor.vigik"
                    versionCode = getVersionCode()
                    versionName = getVersionName()
                }

                buildFeatures {
                    buildConfig = true
                }

                buildTypes {
                    getByName("debug") {
                        enableUnitTestCoverage = true
                        versionNameSuffix = "-DEV"
                    }
                    getByName("release") {
                        // Enables code shrinking, obfuscation, and optimization
                        isMinifyEnabled = true

                        // Enables resource shrinking, which is performed by the Android Gradle plugin.
                        isShrinkResources = true

                        // Includes the default ProGuard rules files that are packaged with the Android Gradle plugin
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            // By default, Android Studio creates and includes an empty rules file (located at the root directory
                            // of each module).
                            "proguard-rules.pro"
                        )
                    }
                }

                compileOptions {
                    // Up to Java 11+ APIs are available through desugaring
                    // https://developer.android.com/studio/write/java11-minimal-support-table
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                    isCoreLibraryDesugaringEnabled = true
                }

                lint {
                    warning.add("AutoboxingStateCreation")
                }

                packaging {
                    resources.excludes.add("META-INF/*")
                }

                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                        isReturnDefaultValues = true
                    }
                }
            }

            // Use withType to workaround https://youtrack.jetbrains.com/issue/KT-55947
            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    // Set JVM target to 17
                    jvmTarget = JavaVersion.VERSION_17.toString()
                    // Treat all Kotlin warnings as errors (disabled by default)
                    // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
                    val warningsAsErrors: String? by project
                    allWarningsAsErrors = warningsAsErrors.toBoolean()
                    freeCompilerArgs = freeCompilerArgs + listOf(
                        "-opt-in=kotlin.RequiresOptIn",
                        // Enable experimental coroutines APIs, including Flow
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=kotlinx.coroutines.FlowPreview",
                    )
                }
            }

            // Use Kotlin K2 compiler
            kotlinExtension.sourceSets.all {
                languageSettings {
                    languageVersion = "2.0"
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
            }
        }
    }

    /**
     * Computes then returns the version code of the application.
     */
    private fun getVersionCode(): Int = APP_MAJOR_VERSION * 10_000 + APP_MINOR_VERSION * 100 + APP_FIX_VERSION

    /**
     * Computes then returns the version name of the application.
     */
    private fun getVersionName(): String = "$APP_MAJOR_VERSION.$APP_MINOR_VERSION.$APP_FIX_VERSION"
}
