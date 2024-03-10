import modules.CommonTestModule

plugins {
    alias(libs.plugins.android.lib)
    alias(libs.plugins.kotlin.android)
}

android {
    val moduleConfig = CommonTestModule
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
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.annotation)
    implementation(libs.timber)

    implementation(libs.google.android.material)

    implementation(libs.junit)
    implementation(libs.androidx.room.testing)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.ext.junit.ktx)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.hilt.android.testing)
    implementation(libs.conditionwatcher)
    implementation(libs.androidx.work.testing)
}
