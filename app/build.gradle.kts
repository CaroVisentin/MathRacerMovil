plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kover)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.app.mathracer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.mathracer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Base URL for local backend running on your machine.
        // Use 10.0.2.2 for Android emulator -> maps to host localhost.
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:5153/api/\"")
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
        buildConfig = true
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    
    // Coil for image loading
    implementation(libs.coil.compose)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // SignalR
    implementation(libs.signalr)
    implementation(libs.okhttp)
    implementation(libs.gson)
    // Retrofit for backend calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Firebase Authentication
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.play.services.auth)
    implementation(libs.googleid)

    // Compose Material 3
    implementation(libs.material3)

    // Íconos Material
    implementation(libs.androidx.material.icons.extended)

    //Compose BOM
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))

    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.material.icons.extended)

    // Compose Animations
    implementation(libs.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.animation)
    
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}