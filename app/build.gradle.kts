plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.foodordring"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodordring"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0")) // Use the latest version

    // Firebase Authentication KTX
    implementation("com.google.firebase:firebase-auth-ktx")

    // Firebase Database KTX
    implementation("com.google.firebase:firebase-database-ktx")

    // Google Play Services Auth (for Google Sign-In)
    implementation("com.google.android.gms:play-services-auth:21.1.0") // Use the latest version
    // ... other dependencies ...
    implementation("androidx.compose.ui:ui:1.6.8") // Or the latest version
    implementation("androidx.compose.material:material:1.6.8") // Or the latest version
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.8") // Or the latest version
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8") // Or the latest version
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8") // Or the latest version
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8") // Or the latest version
    implementation("androidx.activity:activity-compose:1.9.0") // Or the latest version

    implementation (libs.glide) // or the latest version
    annotationProcessor (libs.compiler)
    // AndroidX dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.navigation.ui.ktx)
    //implementation(libs.firebase.auth) // This line is redundant, as it's already included in the Firebase BoM

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
}