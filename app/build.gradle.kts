plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
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
    implementation("com.exyte:animated-navigation-bar:1.0.0")

    // Ícones padrão do Material (compatíveis com Material 3)
    implementation("androidx.compose.material:material-icons-extended")
    
    implementation("androidx.compose.material3:material3")

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

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation(libs.firebase.analytics)

}