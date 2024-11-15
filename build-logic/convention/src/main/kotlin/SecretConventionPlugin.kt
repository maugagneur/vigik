
import com.google.android.libraries.mapsplatform.secrets_gradle_plugin.SecretsPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class SecretConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

            configure<SecretsPluginExtension> {
                propertiesFileName = "secret.properties"
            }
        }
    }
}
