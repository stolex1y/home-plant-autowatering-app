import AppDependencies.moduleImplementation
import modules.CommonModule
import modules.DomainModelModule
import modules.DomainRepositoryModule

plugins {
    alias(libs.plugins.kotlin.jvm)
}

val moduleConfig = DomainRepositoryModule
group = moduleConfig.namespace
version = moduleConfig.versionCode

java {
    toolchain {
        sourceCompatibility = moduleConfig.sourceJdk
        targetCompatibility = moduleConfig.targetJdk
    }
}

kotlin {
    jvmToolchain(moduleConfig.targetJdk.majorVersion.toInt())
}

dependencies {
    moduleImplementation(DomainModelModule)
    moduleImplementation(CommonModule)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.dagger)
    testImplementation(libs.junit)
}
