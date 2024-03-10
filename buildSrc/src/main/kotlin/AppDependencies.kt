import modules.BaseModule
import org.gradle.api.artifacts.dsl.DependencyHandler

object AppDependencies {

    fun <T : BaseModule> DependencyHandler.moduleImplementation(module: T) {
        add(
            ConfigurationName.IMPLEMENTATION.configName,
            project(mapOf("path" to ":${module.name}"))
        )
    }

    fun DependencyHandler.moduleImplementation(module: String) {
        add(
            ConfigurationName.IMPLEMENTATION.configName,
            project(mapOf("path" to ":$module"))
        )
    }

    fun DependencyHandler.moduleTestImplementation(module: String) {
        add(ConfigurationName.TEST_IMPLEMENTATION.configName, project(mapOf("path" to ":$module")))
    }

    enum class ConfigurationName(val configName: String) {
        KAPT("kapt"),
        IMPLEMENTATION("implementation"),
        ANDROID_TEST_IMPLEMENTATION("androidTestImplementation"),
        TEST_IMPLEMENTATION("testImplementation"),
        KSP("ksp"),
        RUNTIME_ONLY("runtimeOnly"),
        API("api"),
        DEBUG_IMPLEMENTATION("debugImplementation"),
        KAPT_TEST("kaptTest"),
        KAPT_ANDROID_TEST("kaptAndroidTest"),
    }

}
