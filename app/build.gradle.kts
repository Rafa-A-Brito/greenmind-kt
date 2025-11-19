plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.serialization)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.github.rafaabrito.projectgreenmind"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.github.rafaabrito.projectgreenmind"
        minSdk = 24
        targetSdk = 36
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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Splash Screen API
    implementation(libs.androidx.core.splashscreen)

    // Room Configure
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Window Size Class
    implementation(libs.material3.window.size.class1)

    // Animated Bottom Bar
    implementation(libs.animated.navigation.bar)

    // Ícones padrão do Material (compatíveis com Material 3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.material3)

    // Testing Database
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // RecylerView
    implementation(libs.androidx.recyclerview)

    // For control over item selection of both touch and mouse driven selection
    implementation(libs.androidx.recyclerview.selection)

    //Navigation
    implementation(libs.androidx.navigation.compose)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Serializable
    implementation(libs.kotlinx.serialization.json)

    // ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Lifecycle Compose
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.auth)

    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // OSM Android
    implementation(libs.osmdroid.android)

    // GSON Google
    implementation(libs.gson)

    // GMS Play Services
    implementation(libs.play.services.location)

    // Encrypted password (BCrypt)
    implementation(libs.jbcrypt)
    implementation(libs.facebook.login)

    // Hilt core
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Hilt para Compose/ViewModel
    implementation(libs.androidx.hilt.navigation.compose)

    // Config Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Coroutines adapter
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // GMS Compose e Services
    implementation("com.google.maps.android:maps-compose:3.0.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Logging interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}