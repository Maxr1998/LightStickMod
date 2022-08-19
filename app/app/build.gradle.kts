import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dependencyupdates)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/detekt.yml")
    autoCorrect = true
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "de.maxr1998.lightstick"
        minSdk = 30
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        setProperty("archivesBaseName", "Light Stick Controller_v$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
    lint {
        lintConfig = file("$rootDir/android-lint.xml")
        abortOnError = false
        sarifReport = true
    }
    packagingOptions {
        with(resources.excludes) {
            // Remove .kotlin_module files that'd only be necessary for Kotlin reflection
            add("META-INF/*.kotlin_module")

            // Remove unnecessary .version and .properties files
            add("META-INF/*.version")
            add("/*.properties")

            // Remove unnecessary licenses
            add("/META-INF/{AL2.0,LGPL2.1}")

            // Remove kotlinx.coroutines debug infrastructure
            add("DebugProbesKt.bin")
        }
    }
}

dependencies {
    // Core
    implementation(libs.bundles.coroutines)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.bundles.androidx.lifecycle)

    // UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.uitoolingpreview)
    implementation(libs.accompanist.systemuicontroller)

    debugImplementation(libs.androidx.compose.uitooling)
    debugImplementation(libs.androidx.compose.testmanifest)

    // Monitoring
    implementation(libs.timber)
    debugImplementation(libs.leakcanary)
    debugImplementation(libs.redscreenofdeath.impl)
    releaseImplementation(libs.redscreenofdeath.noop)

    // Formatting rules for detekt
    detektPlugins(libs.detekt.formatting)
}

tasks {
    withType<Detekt> {
        jvmTarget = JavaVersion.VERSION_1_8.toString()

        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(true)
            sarif.required.set(true)
        }
    }
}