# 🧠 App GreenMind 🌳

### ✨ Visão Geral do Projeto

O **GreenMind** é um aplicativo móvel desenvolvido para atuar como uma ferramenta prática e educativa na promoção da conscientização e sustentabilidade ambiental.

Este projeto busca gamificar a adoção de hábitos sustentáveis e facilitar a ação direta do usuário.

**Recursos Principais:**

* 🗺️ **Localização:** Integração de Mapas (via OSMDroid) para localização de Ecopontos, Pontos de Coleta Seletiva ou ONGs ambientais próximos, oferecendo rotas otimizadas.
* 📚 **Conhecimento Diário:** Pop-ups ou cards informativos diários com dicas de sustentabilidade, curiosidades ambientais e fatos relevantes.
* 🔥 **Gamificação e Engajamento:** Sistema de Ofensivas (_streaks_) para incentivar a interação contínua e a manutenção de uma rotina de aprendizado e ação.
* 👤 **rogresso Procedural:** Funcionalidade para o usuário registrar e monitorar suas ações ambientais (ex: descarte correto, redução de consumo, participação em desafios).

###  💡 Ideias para Aprimoramento

Para tornar o GreenMind mais completo e engajador, serão consideradas as seguintes funcionalidades:

1.  **Pontuação e Nivelamento:** Sistema de pontos para gamificar a experiência e aumentar a retenção.
2.  **Registro de Descarte:** Ferramenta para registrar o volume e tipo de resíduos descartados em Ecopontos.
3.  **Desafios Ambientais:** Metas semanais ou mensais para incentivar práticas sustentáveis.
4.  **Avaliação de Ecopontos:** Sistema de feedback da comunidade sobre a qualidade e acessibilidade dos pontos de coleta.

---

### ⚙️ Configuração e Arquitetura
![KSP](https://img.shields.io/badge/KSP-2.2.1-7f5ab8?style=flat&logo=kotlin)
![AGP](https://img.shields.io/badge/AGP-8.13.0-3ddc8e?style=flat&logo=android)
![Compose](https://img.shields.io/badge/Compose-1.7.0-4d94f3?style=flat&logo=kotlin)

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
<p> As seguintes dependências são utilizadas no projeto, focando em persistência (Room), UI (Compose/Lifecycle) e mapas (OSM Android): </p>

```bash
dependencies {
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

### 🗺️ RoadMap — App de Conscientização Ambiental Gamificado

#### 🌱 Fase 1: Concepção e MVP(_Minimum Viable Product_)
**Objetivo:** Validar a ideia central e entregar uma versão funcional com o essencial para melhor atingir o público.  

**Principais entregas:**
- 💬 **Pesquisa e definição de público-alvo.**  
- 🌍 **Proposta de valor:** engajar o usuário em práticas sustentáveis.  
- 🧩 **Protótipo e design inicial (UI/UX)** com visual ecológico e acessível.  
  - 🎨 **Cores (ARGB):**
    - `#FFF1F5F9` – **LightBlueWhite** → fundo neutro e suave.  
    - `#DB151C24` – **DarkGrayBlue** → tom azulado-acinzentado.  
    - `#FF303338` – **DarkGray** → cor base para textos e ícones escuros.  
    - `#BA787575` – **LightGray** → elementos secundários e bordas sutis.  
    - `#59000000` – **MinimumBlack** → sombras e sobreposições transparentes (profundidade).  
    - `#FF03FF0B` – **Green** → destaque principal, representa ações sustentáveis e sucesso.  
    - `#A67FFFA5` – **MintLightGreen** → cor suave para _backgrounds_.  
    - `#3335C230` – **MediumLightGreen** → elementos interativos.  
    - `#FF0C885B` – **ForestGreen** → cor referente a ações ecológicas.  
    - `#FF34C759` – **LimeGreen** → indicadores de progresso e status ativo.  
    
- ⚙️ **Funcionalidades básicas:**
  - 👤 Cadastro e login de usuário.  
  - 🏆 Sistema de pontos e conquistas.  
  - 🌿 Missões sustentáveis que possibilitam o progresso e aumento de nível/XP.  
  - 📊 Dashboard de progresso ambiental.  

---

#### 🌍 Fase 2: Expansão e Engajamento
**Objetivo:** Aumentar retenção e consolidar a mecânica de gamificação.  

**Principais entregas:**
- 🎯 Sistema de níveis e ranking entre amigos ou comunidade.  
- 📚 Biblioteca de conteúdos educativos (artigos, vídeos curtos, curiosidades).  
- 🤝 Desafios colaborativos (ex.: “Mutirão de limpeza local”).  
- 🔔 Notificações e lembretes inteligentes.  
- 🧠 Feedback e ajustes baseados em dados reais de uso.  
- 📱 Integração com redes sociais para compartilhamento de conquistas.  
- 🌐 Site para hospedagem e apresentação do app.  

---

#### 🌿 Fase 3: Impacto Real 
**Objetivo:** Gerar impacto ambiental mensurável e atrair parceiros estratégicos.  

**Principais entregas:**
- 🏫 Parcerias com ONGs, escolas e prefeituras.  
- 📣 _Merchandising_ através de mídias sociais de recompensas.  
- 📈 Sistema de métricas ambientais.  
- 💚 Campanhas de marketing verde e conscientização.  

---

#### 🌎 Fase 4: Escalabilidade e Comunidade 
**Objetivo:** Consolidar a comunidade e expandir o alcance do app.  

**Principais entregas:**
- 🏅 Gamificação avançada: badges raros, desafios diários e ranking global.  
- 💬 Recursos sociais: fóruns, grupos locais e feed de ações.  
- 💻 Versão Web e integração com IoT.  
- 🌏 Internacionalização (traduções e campanhas globais).  

---

#### 🌟 Fase 5: Sustentabilidade e Aprimoramento 
**Objetivo:** Manter o engajamento e aprimorar continuamente o app e suas funcionalidades.  

**Principais entregas:**
- 🤖 IA para recomendações sustentáveis personalizadas.  
- 🔁 Gamificação dinâmica.  
- 🪴 Programas de certificação de impacto positivo.  
- 🧭 Avaliação contínua de impacto social e ambiental.  

---

`💡 Dica:` Este roadmap é flexível, ajustado conforme métricas de adoção, *feedbacks* e novas oportunidades de impacto sustentável.

---

### 🙏 Agradecimentos

Desde já, sou grato pelo apoio e suporte das plataformas e ferramentas que possibilitaram a construção deste projeto:

* **OpenStreetMap:** Pela API de mapas de código aberto, essencial para a funcionalidade de localização e rotas.
* **Android Developers** Por fornecer a plataforma e o ecossistema de bibliotecas (Jetpack) para o desenvolvimento móvel.
* **Kotlin:** A linguagem de programação moderna e concisa utilizada no projeto.

---
<div align=right>
    <p>
        Feito com 💚 e ☕ por Rafael Brito
    </p>
</div>


