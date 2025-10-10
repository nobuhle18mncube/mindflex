import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()){
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.mindflex"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mindflex"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // inject into BuildConfig
        buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${localProperties.getProperty("SUPABASE_KEY")}\"")
        buildConfigField("String", "NEWSAPI_KEY", "\"${localProperties.getProperty("NEWSAPI_KEY")}\"")
        buildConfigField("String", "GNEWS_API_KEY", "\"${localProperties.getProperty("GNEWS_API_KEY")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.gms:play-services-auth:21.4.0")
    implementation("com.google.code.gson:gson:2.13.2")

    // Networking: pick versions compatible with Kotlin 1.8/1.9-era toolchain
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.okio:okio:3.4.0")

    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.firebase:firebase-auth:22.3.0")

    // JSON (if you still need it)
    implementation("org.json:json:20230227")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
}
