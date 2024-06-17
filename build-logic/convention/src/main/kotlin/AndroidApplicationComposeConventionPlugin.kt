import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            extensions.getByType<ApplicationExtension>().apply {
                buildFeatures {
                    compose = true
                }
            }

            dependencies {
                add("implementation", libs.findBundle("androidx.compose.navigation").get())
                add("implementation", libs.findBundle("androidx.compose.ui").get())
                add("androidTestImplementation", libs.findBundle("test.android").get())
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
                // Utils for Jetpack Compose
                add("implementation", libs.findLibrary("accompanist.permissions").get())
            }
        }
    }
}
