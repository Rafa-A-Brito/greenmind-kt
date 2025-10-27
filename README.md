# 🧠 App GreenMind 🌳

### ✨ Visão Geral do Projeto

O **GreenMind** é um aplicativo móvel desenvolvido para atuar como uma ferramenta prática e educativa na promoção da conscientização e sustentabilidade ambiental.

Este projeto busca gamificar a adoção de hábitos sustentáveis e facilitar a ação direta do usuário.

**Recursos Principais:**

* 🗺️ **Localização Inteligente:** Integração de Mapas (via OSMDroid) para localização de Ecopontos, Pontos de Coleta Seletiva ou ONGs ambientais próximos, oferecendo rotas otimizadas.
* 📚 **Conhecimento Diário:** Pop-ups ou cards informativos diários com dicas de sustentabilidade, curiosidades ambientais e fatos relevantes.
* 🔥 **Gamificação e Engajamento:** Sistema de Ofensivas (_streaks_) para incentivar a interação contínua e a manutenção de uma rotina de aprendizado e ação.
* 👤 **Rastreamento de Progresso:** Funcionalidade para o usuário registrar e monitorar suas ações ambientais (ex: descarte correto, redução de consumo, participação em desafios).

###  💡 Ideias para Aprimoramento

Para tornar o GreenMind mais completo e engajador, serão consideradas as seguintes funcionalidades:

1.  **Pontuação e Níveis:** Sistema de pontos para gamificar a experiência e aumentar a retenção.
2.  **Registro de Descarte:** Ferramenta para registrar o volume e tipo de resíduos descartados em Ecopontos.
3.  **Desafios Ambientais:** Metas semanais ou mensais para incentivar práticas sustentáveis.
4.  **Avaliação de Ecopontos:** Sistema de feedback da comunidade sobre a qualidade e acessibilidade dos pontos de coleta.

---

### ⚙️ Configuração e Arquitetura

O projeto segue a arquitetura **MVVM (Model-View-ViewModel)**, utilizando Jetpack Compose para o desenvolvimento da interface de usuário moderna e reativa.

```bash
GreenMindProject/
└── java/
    └── com/
        └── github/
            └── rafaabrito/
                └── projectgreenmind/
                    ├── data/              # Camada de Dados (Persistência)
                    │   ├── db/            # Base de Dados (Room)
                    │   │   └── GreenMindDatabase.kt
                    │   ├── dao/           # Data Access Objects
                    │   │   └── UserDao.kt
                    │   ├── repository/    # Repositórios (Abstração da fonte de dados)
                    │   │   └── UserRepository.kt
                    │   └── model/         # Entidades do Banco de Dados
                    │       └── UserEntity.kt
                    ├── model/             # Modelos de Domínio (Objetos de Negócio)
                    │   ├── User.kt
                    │   └── ... (EcoPoint.kt, DailyTip.kt)
                    └── ui/                # Camada de Apresentação (UI)
                        ├── components/    # Componentes reutilizáveis do Compose
                        │   ├── LoginTextField.kt
                        │   └── SocialMediaLogin.kt
                        ├── viewmodel/     # ViewModels (Lógica de Apresentação)
                        │   └── MainViewModel.kt
                        ├── screens/       # Telas principais (e.g., MapScreen.kt, HomeScreen.kt)
                        └── MainActivity.kt
```
###  📚 Dependências (Libraries)
<p> As seguintes dependências são utilizadas no projeto, focando em persistência (Room), UI (Compose/Lifecycle) e mapas (OSMDroid): </p>

```bash
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
```

### 🚀 Navegação

O fluxo de navegação será intuitivo, cobrindo as seguintes telas principais:

1.  **Autenticação:** Login e Cadastro.
2.  **Dashboard:** Visão geral do progresso (Streak, Pontos, Conhecimento Diário).
3.  **Mapas:** Localização de Ecopontos e rotas.
4.  **Desafios/Metas:** Área para participação em atividades de sustentabilidade.
5.  **Perfil:** Configurações e histórico de atividades.

---

### 📃 LICENSE
Este projeto mobile está licenciado sob a _MIT LICENSE_. Assim, é permitido o livre conhecimento e uso em demais projetos, contanto que a atribuição original seja mantida.

---
### 🙏 Agradecimentos

Desde já, sou grato pelo apoio e suporte das plataformas e ferramentas que possibilitaram a construção deste projeto:

* **OpenStreetMap (OSM:** Pela API de mapas de código aberto, essencial para a funcionalidade de localização e rotas.
* **Android / Google:** Por fornecer a plataforma e o ecossistema de bibliotecas (Jetpack) para o desenvolvimento móvel.
* **Kotlin:** A linguagem de programação moderna e concisa utilizada no projeto.

---
<div align=right>
    <p>
        Feito com 💚 e ☕ por Rafael Brito
    </p>
</div>


