dependencies {

    // KOTLIN & COROUTINES
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") 
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version") 

    // ROOM (Persistência Local)
    // No build.gradle.kts(App):
    // ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-runtime:$room_version")

    // LIFECYCLE E VIEWMODEL (Jetpack)
    val lifecycle_version = "2.9.4"
    // ViewModel - Suporte para KTX (Kotlin Extensions)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // ViewModel - Suporte para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    // LiveData - Suporte para KTX
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    // Lifecycle - Suporte para Runtime do Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")
    
    // NAVIGATION
    // Sugestão: Jetpack Navigation para Compose
    // implementation("androidx.navigation:navigation-compose:$nav_version")

    // MAPAS (OpenStreetMap - OSMDroid)
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    
    // REDE (Para API de Ecopontos, se houver)
    // implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // implementation("com.squareup.retrofit2:converter-gson:2.9.0") 

}
