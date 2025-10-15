import java.util.Properties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Read properties from local.properties
val properties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.digiroth.simplebarcodescanner"
    //noinspection GradleDependency
    compileSdk = 36
    
    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = properties.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = properties.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = properties.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = "com.digiroth.simplebarcodescanner"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "GEMINI_API_KEY", properties.getProperty("geminiApiKey"))
        buildConfigField("String", "BUILD_TIME", "\"${SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(Date())}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            // No signing config needed for debug, it uses a debug key by default
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.play.services.code.scanner)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended) // ContentCopy is in here

    implementation(libs.androidx.preference.ktx) // Or the latest version
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coroutines testing
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")


        // Coil for Jetpack Compose
    //implementation(libs.coil.compose) // Check for the latest version
    implementation(libs.coil3.coil.compose) // Use the latest stable or suitable alpha/beta version

        // Coil needs a network client if you plan to load images from URLs.
        // OkHttp is a common choice. Add this if you load from network.
        // If you only load from local URIs (like from the camera), this might be optional
        // but it's often good to include it for general image loading capabilities.
    implementation(libs.coil3.coil.network.okhttp) // Match the version with coil-compose
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}
