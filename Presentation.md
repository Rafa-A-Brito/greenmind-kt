# 🧠 App GreenMind 🌳

## ✨ Visão Geral do Projeto

O **GreenMind** é um aplicativo móvel desenvolvido para atuar como uma ferramenta prática e educativa na promoção da **conscientização e sustentabilidade ambiental**.

Este projeto busca **gamificar** a adoção de hábitos sustentáveis e **facilitar** a ação direta do usuário.

**Recursos Principais:**

* 🗺️ **Localização Inteligente:** Integração de **Mapas** (via OSMDroid) para localização de **Ecopontos, Pontos de Coleta Seletiva ou ONGs ambientais** próximos, oferecendo rotas otimizadas.
* 📚 **Conhecimento Diário:** **Pop-ups** ou *cards* informativos diários com **dicas de sustentabilidade, curiosidades ambientais** e fatos relevantes.
* 🔥 **Gamificação e Engajamento:** Sistema de **Ofensivas (*Streaks*)** para incentivar a interação contínua e a manutenção de uma rotina de aprendizado e ação.
* 👤 **Rastreamento de Progresso:** Funcionalidade para o usuário registrar e monitorar suas ações ambientais (ex: descarte correto, redução de consumo, participação em desafios).

## 💡 Ideias para Aprimoramento

Para tornar o GreenMind mais completo e engajador, serão consideradas as seguintes funcionalidades:

1.  **Pontuação e Níveis:** Sistema de pontos para gamificar a experiência e aumentar a retenção.
2.  **Registro de Descarte:** Ferramenta para registrar o volume e tipo de resíduos descartados em Ecopontos.
3.  **Desafios Ambientais:** Metas semanais ou mensais para incentivar práticas sustentáveis.
4.  **Avaliação de Ecopontos:** Sistema de feedback da comunidade sobre a qualidade e acessibilidade dos pontos de coleta.

---

### ⚙️ Configuração e Arquitetura

O projeto segue a arquitetura **MVVM (Model-View-ViewModel)**, utilizando **Jetpack Compose** para o desenvolvimento da interface de usuário moderna e reativa.

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
