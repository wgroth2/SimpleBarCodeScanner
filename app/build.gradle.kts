plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.digiroth.simplebarcodescanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.digiroth.simplebarcodescanner"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    // These bring in the icon definitions. They might already be included transitively,
    // but you can declare them explicitly if needed or if you want to ensure you get them.
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended") // ContentCopy is in here

    implementation("androidx.preference:preference-ktx:1.2.1") // Or the latest version

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ML Kit Barcode Scanning (if you plan to process it in MainActivity later)
    implementation(libs.barcode.scanning) // Check for latest
    androidTestImplementation(libs.barcode.scanning) // Assuming you added this as per previous suggestion


        // Coil for Jetpack Compose
    //implementation(libs.coil.compose) // Check for the latest version
    implementation(libs.coil3.coil.compose) // Use the latest stable or suitable alpha/beta version

        // Coil needs a network client if you plan to load images from URLs.
        // OkHttp is a common choice. Add this if you load from network.
        // If you only load from local URIs (like from the camera), this might be optional
        // but it's often good to include it for general image loading capabilities.
    implementation(libs.coil3.coil.network.okhttp) // Match the version with coil-compose
    }
