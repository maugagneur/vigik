import com.android.build.api.dsl.ApplicationExtension
import dagger.hilt.android.plugin.HiltExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
                apply("com.google.devtools.ksp")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig.apply {
                    testInstrumentationRunner = "com.kidor.vigik.utils.HiltTestRunner"
                }
            }

            configure<HiltExtension> {
                // Allows the Hilt annotation processors to be isolating so they are only invoked when necessary.
                enableAggregatingTask = true
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                add("implementation", (libs.findLibrary("dagger.hilt.android").get()))
                add("implementation", (libs.findLibrary("androidx.hilt.work").get()))
                add("ksp", (libs.findLibrary("androidx.hilt.compiler").get()))
                add("ksp", (libs.findLibrary("dagger.hilt.compiler").get()))
                add("kspAndroidTest", (libs.findLibrary("dagger.hilt.android.compiler").get()))
                add("kspAndroidTest", (libs.findLibrary("androidx.hilt.compiler").get()))
            }
        }
    }
}
