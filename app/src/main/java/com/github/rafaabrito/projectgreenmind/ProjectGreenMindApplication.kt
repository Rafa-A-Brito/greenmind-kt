package com.github.rafaabrito.projectgreenmind

import android.app.Application
import dagger.hilt.android.HiltAndroidApp // Import necessário

@HiltAndroidApp // ⬅️ A anotação crucial
class ProjectGreenMindApplication : Application() {
    // Opcionalmente, você pode inicializar coisas aqui, mas o Hilt faz o trabalho pesado
}