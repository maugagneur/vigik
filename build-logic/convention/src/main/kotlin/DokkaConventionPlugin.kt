import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.dokka.gradle.DokkaExtension

class DokkaConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.dokka")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                "dokkaPlugin"(libs.findLibrary("dokka-android-plugin").get())
            }

            extensions.configure<DokkaExtension> {
                moduleName.set("Vigik")
                dokkaSourceSets.named("main") {
                    suppressGeneratedFiles.set(true)
                }
                dokkaPublications.named("html") {
                    outputDirectory.set(layout.buildDirectory.dir("reports/dokka"))
                }
            }
        }
    }
}
