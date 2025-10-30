# 🧠 App GreenMind 🌳

### 🔍 Visão Geral do Projeto

O **GreenMind** é um aplicativo _mobile_ desenvolvido para atuar como uma ferramenta prática e educativa na promoção da conscientização e sustentabilidade ambiental.

Este projeto busca utilizar um estilo _gamificado_ aliado à adoção de hábitos sustentáveis a fim de facilitar a ação direta do usuário.

**🧩 Recursos Principais:**

* 🗺️ **Localização Inteligente:** Integração de Mapas (via OSMDroid) para localização de Ecopontos, Pontos de Coleta Seletiva ou ONGs ambientais próximos, oferecendo rotas otimizadas.
* 📚 **Conhecimento Diário:** Pop-ups ou cards informativos diários com dicas de sustentabilidade, curiosidades ambientais e fatos relevantes.
* 🔥 **Gamificação e Engajamento:** Sistema de Ofensivas (_streaks_) para incentivar a interação contínua e a manutenção de uma rotina de aprendizado e ação.
* 👤 **Rastreamento de Progresso:** Funcionalidade para o usuário registrar e monitorar suas ações ambientais (ex: descarte correto, redução de consumo, participação em desafios).

### 💡 Ideias para Aprimoramento

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
```

### 🚀 Navegação

O fluxo de navegação será intuitivo, cobrindo as seguintes telas principais:

1.  **Autenticação:** Login e Cadastro.
2.  **Dashboard:** Visão geral do progresso (Streak, Pontos, Conhecimento Diário).
3.  **Mapas:** Localização de Ecopontos e rotas.
4.  **Desafios/Metas:** Área para participação em atividades de sustentabilidade.
5.  **Perfil:** Configurações e histórico de atividades.

---

### ⚖️ LICENSE
Este projeto mobile está licenciado sob a _MIT LICENSE_. Assim, é permitido o livre conhecimento e uso em demais projetos, contanto que a atribuição original seja mantida.

---
### 🙏 Agradecimentos

Desde já, sou grato pelo apoio e suporte das plataformas e ferramentas que possibilitaram a construção deste projeto:

* **OpenStreetMap (OSM):** Uso da API de mapas de código aberto, essencial para a funcionalidade de localização e rotas.
* **Android:** Forcencimento de uma base de código para a plataforma para o desenvolvimento móvel.
  * **Interface Gráfica (UI):** Framework de interface de usuário baseado na linguagem Kotlin e _open source_. É um kit de biblioteca fundamental para uma cosntrução intuitiva e 
atraente ao público.
* **Kotlin:** A linguagem de programação moderna e concisa utilizada no projeto.

---
<div style="text-align: right;">>
    <p>
        Feito com 💚 e ☕ por Rafael Brito
    </p>
</div>


