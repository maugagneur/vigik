import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.gitlab.arturbosch.detekt")
                // FIXME: Detekt compiler plugin 1.23.6 does not works with Kotlin 2.1 but only with Kotlin 2.0 and language version 1.9
                //apply("io.github.detekt.gradle.compiler-plugin")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                "detektPlugins"(libs.findLibrary("detekt-formatting").get())
                "detektPlugins"(libs.findLibrary("detekt-rules-libraries").get())
            }

            configure<DetektExtension> {
                // Version of Detekt that will be used. When unspecified the latest detekt version found will be used.
                toolVersion = libs.findVersion("detekt").get().toString()
                // Applies the config files on top of detekt's default config file.
                buildUponDefaultConfig = true
                // Turns on all the rules (even unstable).
                allRules = false
                // Define the detekt configuration(s) you want to use.
                config.setFrom("$projectDir/config/detekt/config.yml")
                // Specifying a baseline file.
                //baseline = file("$projectDir/config/baseline.xml")
                // If set to `true` the build does not fail when the maxIssues count was reached.
                ignoreFailures = true
                // Builds the AST in parallel (rules are always executed in parallel).
                parallel = true
            }

            tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                reports {
                    // Enable/Disable XML report (default: true)
                    xml.required.set(true)
                    // Enable/Disable HTML report (default: true)
                    html.required.set(true)
                    // Enable/Disable TXT report (default: true)
                    txt.required.set(true)
                    // Enable/Disable SARIF report (default: false)
                    sarif.required.set(true)
                    // Enable/Disable MD report (default: false)
                    md.required.set(true)
                }
            }
        }
    }
}
