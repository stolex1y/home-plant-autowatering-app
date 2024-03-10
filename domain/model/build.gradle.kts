import AppDependencies.moduleImplementation
import modules.CommonModule
import modules.DomainModelModule

plugins {
    alias(libs.plugins.kotlin.jvm)
}

val moduleConfig = DomainModelModule
group = moduleConfig.namespace
version = moduleConfig.versionCode

kotlin {
    jvmToolchain(moduleConfig.targetJdk.majorVersion.toInt())
}

dependencies {
    moduleImplementation(CommonModule)

    implementation(libs.androidx.annotation)
}
