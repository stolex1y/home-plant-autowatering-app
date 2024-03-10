import AppDependencies.moduleImplementation
import modules.CommonTestModule
import modules.DataSourceLocalModule
import modules.DomainModelModule
import modules.DomainRepositoryModule

plugins {
    alias(libs.plugins.android.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    val moduleConfig = DataSourceLocalModule
    namespace = moduleConfig.namespace
    compileSdk = moduleConfig.compileSdk

    defaultConfig {
        minSdk = moduleConfig.minSdk
        version = moduleConfig.versionCode

        testInstrumentationRunner = moduleConfig.testInstrumentationRunner

        testProguardFiles(
            moduleConfig.testProguardRules
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                moduleConfig.proguardRules
            )
        }

        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = moduleConfig.sourceJdk
        targetCompatibility = moduleConfig.targetJdk
    }
    kotlinOptions {
        jvmTarget = moduleConfig.targetJdk.majorVersion
    }
}

dependencies {
    moduleImplementation(DomainModelModule)
    moduleImplementation(DomainRepositoryModule)
    moduleImplementation(CommonTestModule)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.timber)

    kapt(libs.hilt.ext.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)
    kaptTest(libs.hilt.compiler)

    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.conditionwatcher)
    androidTestImplementation(libs.androidx.work.testing)
    kaptAndroidTest(libs.hilt.android.testing)
}
