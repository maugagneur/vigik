import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Locale

class AndroidApplicationJacocoConventionPlugin : Plugin<Project> {
    private val coverageExclusions = listOf(
        // Android
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        // Navigation Component generated classes
        "**/*Args*.*",
        "**/*Directions*.*",
        // Data Binding generated classes
        "**/databinding/**",
    )

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.gradle.jacoco")
            }
            val extension = extensions.getByType<ApplicationAndroidComponentsExtension>()

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            configure<JacocoPluginExtension> {
                toolVersion = libs.findVersion("jacoco").get().toString()
                reportsDirectory.set(file("${layout.buildDirectory}/reports/customJacocoReport"))
            }

            configurations.all {
                resolutionStrategy {
                    eachDependency {
                        if (requested.group == "org.jacoco") {
                            useVersion(libs.findVersion("jacoco").get().toString())
                        }
                    }
                }
            }

            val jacocoTestReport = tasks.create("jacocoTestReport")

            extension.onVariants { variant ->
                val testTaskName = "test${variant.name.capitalize()}UnitTest"

                val reportTask = tasks.register("jacoco${testTaskName.capitalize()}Report", JacocoReport::class.java) {
                    dependsOn(testTaskName)

                    reports {
                        html.required.set(true)
                    }

                    classDirectories.setFrom(
                        fileTree("${layout.buildDirectory}/tmp/kotlin-classes/${variant.name}") {
                            exclude(coverageExclusions)
                        }
                    )

                    sourceDirectories.setFrom(files("$projectDir/src/main/java", "$projectDir/src/main/kotlin"))

                    executionData.setFrom(file("${layout.buildDirectory}/jacoco/$testTaskName.exec"))
                }

                jacocoTestReport.dependsOn(reportTask)
            }

            tasks.withType<Test>().configureEach {
                configure<JacocoTaskExtension> {
                    // Required for JaCoCo + Robolectric
                    // https://github.com/robolectric/robolectric/issues/2230
                    isIncludeNoLocationClasses = true

                    // Required for JDK 11 with the above
                    // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }

    private fun String.capitalize() = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}
