# 🔥 FireWatch - Monitoramento Global de Focos de Incêndio

**Projeto desenvolvido para a Global Solution - FIAP**

O **FireWatch** é um aplicativo Android nativo focado em sustentabilidade e segurança ambiental. Ele realiza o monitoramento em tempo real de focos de incêndio (utilizando dados do INPE) e cruza essas informações com a localização atual do usuário, fornecendo uma análise preditiva de risco e acesso rápido a serviços de emergência.

---

## 🚀 Principais Funcionalidades

* **Mapa de Calor e Risco:** Visualização geoespacial de focos de incêndio categorizados por severidade (Baixo, Médio e Crítico).
* **Geolocalização em Tempo Real:** Rastreamento da localização do usuário com requisição nativa de permissão do Android.
* **Análise de Distância:** Cálculo automático da distância (em KM) entre a propriedade do usuário e o foco de incêndio selecionado.
* **Ação Rápida de Emergência:** Botão fixo de SOS para acionamento direto do Corpo de Bombeiros (193).
* **Modo de Simulação:** Inserção manual de coordenadas (Latitude/Longitude) para prever riscos em outras regiões.

---

## 🛠️ Tecnologias e Arquitetura

Este projeto foi construído seguindo as melhores práticas de desenvolvimento mobile moderno:

* **Linguagem:** Kotlin
* **Interface Gráfica:** Jetpack Compose
* **Arquitetura:** MVVM (Model-View-ViewModel) para separação clara de responsabilidades.
* **Mapas e Geolocalização:** Google Maps SDK & Google Play Services Location.
* **Consumo de API:** Integração com os dados de satélite do INPE.
* **Controle de Versão:** Git & GitHub.

---

## 📱 Fluxo de Telas (User Flow)

Abaixo está o fluxo de navegação e a explicação visual de cada tela do aplicativo.

### 1. Tela de Autenticação / Login
A porta de entrada do aplicativo, onde o usuário insere suas credenciais de acesso para visualizar sua área monitorada.

### 2. Tela de cadastro
Novos usuários fazem o cadastro com todas suas informações e no final permitem o acesso do aplicativo à sua localização

### 3. Dashboard: Monitoramento Global
A tela principal do sistema. Exibe o mapa centrado na localização do usuário (pino azul) e renderiza os focos de incêndio ao redor do Brasil com cores baseadas no nível de criticidade. No topo, há o painel para ajuste manual de coordenadas.

### 4. Análise de Risco (Card Dinâmico)
Ao clicar em qualquer foco de incêndio no mapa, os demais pinos perdem opacidade (destaque visual) e um Card de Análise de Risco surge na base da tela, informando a severidade e a distância exata em quilômetros até a ameaça.

### 5. Gerenciamento de Perfil
Ao clicar na foto de perfil, terá as opções para alterar a foto, nome, email e senha.