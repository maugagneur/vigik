import org.cqfn.diktat.plugin.gradle.DiktatExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class DiktatConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.cqfn.diktat.diktat-gradle-plugin")

            configure<DiktatExtension> {
                // Available report types: "html", "json", "plain" (default), "sarif"
                reporter = "html"
                output = "$projectDir/build/reports/diktat/diktat.html"
                ignoreFailures = true
                debug = true
            }
        }
    }
}
