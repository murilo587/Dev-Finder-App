# 📱 Dev Finder

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-Studio-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVI%20+%20Clean-blue.svg?style=flat)]()

> ⚠️ **Nota de Escopo:** Este é um **projeto pessoal desenvolvido exclusivamente para fins de estudo, aprendizado e consolidação de boas práticas**. O objetivo principal foi aplicar os conceitos mais modernos de engenharia de software no ecossistema Android Nativo, simulando a arquitetura e os padrões de qualidade exigidos em produtos de alta escala.

---

## 🔍 O que o aplicativo faz?

O **Dev Finder** é uma ferramenta projetada para facilitar a busca, exploração e gerenciamento de perfis de desenvolvedores (integrado com APIs públicas como a do GitHub). 

- **Busca Avançada:** Permite pesquisar usuários por nome, tecnologia ou localização.
- **Visualização de Detalhes:** Exibe informações consolidadas do perfil, como repositórios públicos, bio, estatísticas de contribuição e tecnologias mais utilizadas.
- **Favoritos:** Opção de favoritar perfis, que são salvos localmente para acesso rápido posterior, permitindo a visualização dos dados mesmo quando o dispositivo está sem conexão com a internet..

---

## 🚀 Tecnologias e Ecossistema

O projeto foi construído utilizando as ferramentas e bibliotecas recomendadas pelo ecossistema Android moderno:

- **Linguagem:** [Kotlin](https://kotlinlang.org/) (assincronismo avançado e concorrência).
- **Interface Gráfica:** [Jetpack Compose](https://developer.android.com/jetpack/compose) para uma UI 100% declarativa, fluida e componentizada.
- **Injeção de Dependências:** [Dagger-Hilt](https://developer.android.com/training/dependency-injection/hilt-android) garantindo baixo acoplamento e facilidade na substituição de componentes (mocks/fakes para testes).
- **Rede / API:** [Retrofit](https://square.github.io/retrofit/) para consumo de serviços externos de forma otimizada.
- **Persistência de Dados:** [Room Database](https://developer.android.com/training/data-storage/room) para cache local, favoritos e suporte offline.
- **Programação Reativa:** Kotlin [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) para gerenciamento de estados assíncronos e pipelines de dados em tempo real.

---

## 🏗️ Arquitetura (MVI + Clean Architecture)

Para garantir escalabilidade e fácil manutenção, o app adota o padrão **MVI (Model-View-Intent)** combinado com os princípios da **Clean Architecture**, estruturando o fluxo de dados de forma estritamente Unidirecional (UDF):

1. **UI (View):** Renderizada via Jetpack Compose, observa um estado único (`ViewState`) e dispara intenções do usuário (`UserIntent`).
2. **ViewModel:** Captura as intenções, gerencia o ciclo de vida da tela e delega as regras de negócio para os Casos de Uso.
3. **Domain (Casos de Uso):** Contém as regras de negócio puras, totalmente independentes de frameworks ou detalhes de infraestrutura.
4. **Data (Repositórios):** Responsável por abstrair a origem dos dados (decidindo entre a API remota via Retrofit ou o cache local via Room).

---

## 🧪 Cultura de Testes e Qualidade

A testabilidade foi um pilar central no desenvolvimento do projeto. O código possui uma cobertura sólida de **testes unitários** estruturados para validar o comportamento de fluxos complexos de dados sem depender do emulador:

- **JUnit 5 & Kotest:** Frameworks utilizados para escrita de asserções limpas, legíveis e expressivas.
- **MockK:** Utilizado para a criação de mocks de dependências de forma rápida e segura.
- **Turbine:** Biblioteca especializada utilizada para testar e validar emissões assíncronas do `Kotlin Flow` e canais do Coroutines de ponta a ponta.

---

## 🛠️ Como Executar o Projeto

1. Faça o clone deste repositório:
   ```bash
   git clone https://github.com/murilo587/Dev-Finder-App.git
