# 🧠 App GreenMind 🌳

### ⚙️ Configuração 

### 📚 _Libraries_

```cmd

dependencies {
    
    // Usando Kotlin Symbol Processing (KSP)
    // Adicione no build.gradle.kts(App)
    ksp("androidx.room:room-compiler:$room_version")

    // Extensão de suporte ao Room
    // Coroutines (Assyncron)
    implementation("androidx.room:room-ktx:$room_version")

    // RecylerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.recyclerview:recyclerview-selection:1.2.0")

    // Lifecycle e Navigation
     val lifecycle_version = "2.9.4"
    val arch_version = "2.2.0"

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

     // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")


    // Lifecycle utilities for Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")

}

```


### 🚀 Navegação


### 🙏 Agradecimentos
Desde já, sou grato pelo apoio e suporte das plataformas que possibilitaram a construção do projeto:
- OSM(OpenStreet Mao) 
    - Visualização e integração de Mapas
- Android



