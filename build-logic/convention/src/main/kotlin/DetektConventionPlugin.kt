import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
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
                config = files("$projectDir/config/detekt/config.yml")
                // Specifying a baseline file.
                //baseline = file("$projectDir/config/baseline.xml")
                // If set to `true` the build does not fail when the maxIssues count was reached.
                ignoreFailures = true
            }
        }
    }
}
