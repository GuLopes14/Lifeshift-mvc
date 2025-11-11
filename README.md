# 🧠 LifeShift - Plataforma Inteligente de Requalificação Profissional

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge)

</div>

---

## Integrantes
- Gabriel Santos Jablonski - RM: 555452
- Gustavo Lopes Santos da Silva - RM: 556859
- Renato de Freitas David Campiteli - RM: 555627

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Propósito e Objetivo](#propósito-e-objetivo)
- [Como Funciona](#como-funciona)
- [Recursos Técnicos](#recursos-técnicos)
- [Instalação e Setup](#instalação-e-setup)
- [Arquitetura](#arquitetura)
- [Fluxo de Uso](#fluxo-de-uso)
- [Internacionalização](#internacionalização)
- [Impacto Social](#impacto-social)
- [Diferenciais](#diferenciais)
- [Contribuindo](#contribuindo)
- [Licença](#licença)

---

## 🎯 Sobre o Projeto

**LifeShift** é uma plataforma web inteligente desenvolvida para apoiar pessoas na **requalificação profissional** e **transição de carreira**, utilizando **Inteligência Artificial (IA)** para criar trilhas personalizadas de aprendizado de forma **acessível e inclusiva**.

Criado como solução para a **Global Solution FIAP 2025/2** — com o tema *"O Futuro do Trabalho: Educação, Inclusão e Tecnologia"* — o LifeShift atende uma necessidade crescente no mercado contemporâneo: ajudar profissionais em diferentes estágios de suas carreiras a se adaptarem às novas demandas tecnológicas e sociais do mundo do trabalho.

---

## 🎯 Propósito e Objetivo

O propósito central do **LifeShift** é **democratizar o acesso à requalificação profissional**, permitindo que qualquer pessoa — independentemente de sua área de formação, idade ou condição social — possa planejar uma mudança de carreira de forma **estruturada**, **guiada** e **gratuita**.

### Objetivos Principais:

✅ **Personalização via IA**: Através da IA Groq (LLaMA 3.3), o sistema gera um plano de estudos de **6 meses totalmente personalizado** com base em:
- Profissão atual do usuário
- Profissão desejada
- Habilidades já existentes

✅ **Cursos Reais e Acessíveis**: O plano inclui sugestões de cursos gratuitos verificados de plataformas reconhecidas como Coursera, Udemy, edX, Khan Academy e YouTube

✅ **Experiência Segura**: Autenticação moderna e segura através de **OAuth2 com Google Login**

✅ **Inclusão Digital**: Suporte bilíngue (Português e Inglês) para alcance global

---

## ⚙️ Como Funciona

### 1️⃣ **Login Seguro com Google (OAuth2)**
O usuário acessa o sistema de forma simples e segura usando sua conta Google, garantindo autenticação moderna e padronizada.

```
Usuário → Login Google → OAuth2 → Sessão Autenticada
```

### 2️⃣ **Preenchimento do Perfil Profissional**
O usuário informa através de um formulário:
- 👔 Profissão atual
- 🎯 Profissão desejada
- 💡 Habilidades atuais

### 3️⃣ **Geração de Plano com IA (Groq API)**
O sistema envia essas informações para a IA Groq (modelo LLaMA 3.3), que:
- Analisa os dados fornecidos
- Retorna um plano personalizado de requalificação
- Estrutura mês a mês com:
  - 📚 Curso sugerido
  - 📝 Descrição do aprendizado
  - 🔗 Link de acesso direto

### 4️⃣ **Sugestões Reais e Verificáveis**
A IA é instruída para gerar apenas cursos reais e gratuitos, com links de busca válidos para cada plataforma — garantindo que todos os resultados sejam **acessíveis e atualizados**.

### 5️⃣ **Visualização e Histórico**
O usuário visualiza seu plano em uma interface amigável, podendo:
- 💾 Salvar planos
- 📖 Revisar planos anteriores
- 📑 Consultar histórico com paginação

### 6️⃣ **Otimização e Desempenho**
O sistema utiliza:
- 🚀 **Spring Cache** para armazenar planos repetidos
- 📨 **RabbitMQ** para processar tarefas de forma assíncrona
- ⚡ Melhorando desempenho e escalabilidade

---

## 🧩 Recursos Técnicos

### Backend e Framework
- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.5.7
- **Padrão Arquitetural**: MVC (Model-View-Controller)

### Banco de Dados
- **SGBD**: PostgreSQL 16
- **ORM**: Spring Data JPA
- **Migrations**: Flyway Database
- **Containerização**: Docker Compose

### Inteligência Artificial
- **Provider**: Groq API
- **Modelo**: LLaMA 3.3 (70B Versatile)
- **Integração**: Spring AI

### Autenticação e Segurança
- **OAuth2**: Google Login
- **Spring Security**: Controle de acesso
- **Validação**: Bean Validation (Jakarta)

### Mensageria e Cache
- **Message Broker**: RabbitMQ (AMQP)
- **Caching**: Spring Boot Starter Cache
- **Processamento Assíncrono**: Spring AMQP

### Frontend
- **Template Engine**: Thymeleaf
- **Segurança Frontend**: Thymeleaf Extras Spring Security 6
- **Internacionalização**: Resource Bundles (i18n)

### Linguagens Suportados
- 🇧🇷 **Português (Brasil)** — PT-BR
- 🇺🇸 **Inglês** — EN-US

---

## 🚀 Instalação e Setup

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 21** ou superior
- **Docker** e **Docker Compose**
- **Git**
- **Gradle** (ou use o `gradlew` incluído)

### 1. Clone o Repositório

```bash
git clone https://github.com/GuLopes14/Lifeshift-mvc.git
cd Lifeshift-mvc
```

### 2. Configure as Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/lifeshift
SPRING_DATASOURCE_USERNAME=lifeshift
SPRING_DATASOURCE_PASSWORD=lifeshift

# Google OAuth2
GOOGLE_CLIENT_ID=seu_client_id_aqui
GOOGLE_CLIENT_SECRET=seu_client_secret_aqui

# Groq API
GROQ_API_KEY=sua_groq_api_key_aqui
GROQ_API_URL=https://api.groq.com/openai/v1/chat/completions
GROQ_MODEL=llama-3.3-70b-versatile

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

### 3. Inicie os Serviços com Docker Compose

```bash
docker-compose up -d
```

Este comando inicia:
- 🐘 **PostgreSQL 16**
- 🐰 **RabbitMQ 3** (com Management UI em http://localhost:15672)

### 4. Execute a Aplicação

#### Usando Gradle

```bash
# Windows
./gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

#### Usando Maven (se disponível)

```bash
mvn spring-boot:run
```

### 5. Acesse a Aplicação

Abra seu navegador e acesse:

```
http://localhost:8080
```

---

## 🏗️ Arquitetura

### Estrutura de Diretórios

```
src/main/java/br/com/lifeshift/lifeshift/
├── LifeshiftApplication.java          # Classe principal
├── auth/                              # Autenticação e OAuth2
│   └── LoginListener.java
├── config/                            # Configurações do Spring
├── controller/                        # Controllers MVC
│   ├── AppController.java
│   ├── HomeController.java
│   ├── PerfilController.java
│   ├── PlanoController.java
│   ├── ProfileController.java
│   ├── RootController.java
│   └── CustomErrorController.java
├── dto/                               # Data Transfer Objects
├── exception/                         # Tratamento de exceções
├── model/                             # Entidades JPA
├── repository/                        # Spring Data JPA Repositories
└── service/                           # Lógica de negócio

src/main/resources/
├── application.properties              # Configurações
├── messages_pt_BR.properties          # i18n - Português
├── messages_en_US.properties          # i18n - Inglês
├── db/migration/                      # Scripts de migração (Flyway)
│   ├── V1__create_usuario_table.sql
│   ├── V2__create_perfil_table.sql
│   ├── V3__create_plano_table.sql
│   ├── V4__add_descricao_to_plano.sql
│   └── V5__alter_conteudo_gerado_to_text.sql
└── templates/                         # Thymeleaf Templates
    ├── index.html
    ├── login.html
    ├── conta.html
    ├── perfil.html
    ├── plano.html
    ├── planos-salvos.html
    ├── profile.html
    ├── intro.html
    ├── loading.html
    ├── logout.html
    └── error.html
```

### Diagrama de Fluxo

```
┌─────────────────────────────────────────────────────────┐
│                      USUÁRIO                            │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
        ┌─────────────────┐
        │  Google OAuth2  │
        │    Login        │
        └────────┬────────┘
                 │
                 ▼
        ┌────────────────────┐
        │  HomeController    │
        │  PerfilController  │
        └────────┬───────────┘
                 │
                 ▼
        ┌────────────────────┐
        │  PlanoService      │
        │  (Business Logic)  │
        └────────┬───────────┘
                 │
                 ▼
    ┌────────────────────────────┐
    │  Groq API (LLaMA 3.3)      │
    │  IA - Generate Plan        │
    └────────┬───────────────────┘
             │
    ┌────────▼──────────────┐
    │   Spring Cache        │
    │  (Cache Planos)       │
    └────────┬──────────────┘
             │
    ┌────────▼──────────────────┐
    │   RabbitMQ (Async Tasks)   │
    └────────┬──────────────────┘
             │
    ┌────────▼──────────────────┐
    │   PostgreSQL Database      │
    │   (Persist Data)           │
    └────────────────────────────┘
```

---

## 📊 Fluxo de Uso

### Cenário Exemplo: Transição para Análise de Dados

Um usuário que trabalha como **assistente administrativo** e deseja se tornar **analista de dados**:

1. **Acessa o LifeShift** e faz login com sua conta Google
2. **Preenche o formulário** informando:
   - Profissão atual: Assistente Administrativo
   - Profissão desejada: Analista de Dados
   - Habilidades: Excel básico, organização, gestão de documentos

3. **Recebe um plano de 6 meses** estruturado:
   - **Mês 1**: Python Basics (Coursera)
   - **Mês 2**: Introdução à Análise de Dados (edX)
   - **Mês 3**: Excel Avançado para Negócios (LinkedIn Learning)
   - **Mês 4**: Estatística Aplicada (Khan Academy)
   - **Mês 5**: SQL e Bancos de Dados (Udemy)
   - **Mês 6**: Ferramentas de BI (Google Analytics Academy)

4. **Acessa os cursos** através dos links verificados fornecidos
5. **Salva e revisita** seu plano a qualquer momento
6. **Acompanha seu progresso** com um histórico de planos personalizados

---

## 🌐 Internacionalização (i18n)

O LifeShift suporta múltiplos idiomas para alcance global:

### Idiomas Suportados:
- 🇧🇷 **Português (Brasil)** — `pt_BR`
- 🇺🇸 **Inglês** — `en_US`

### Configuração:
Os arquivos de mensagens estão localizados em:
- `src/main/resources/messages_pt_BR.properties`
- `src/main/resources/messages_en_US.properties`

### Uso em Templates:
```html
<p th:text="#{welcome.message}"></p>
<button th:text="#{button.start}"></button>
```

---

## 🌍 Impacto Social

O **LifeShift** busca impactar positivamente o cenário da educação e empregabilidade no Brasil e no mundo, oferecendo uma solução que une **tecnologia acessível**, **IA generativa** e **inclusão digital**.

A proposta reforça o compromisso com os **Objetivos de Desenvolvimento Sustentável (ODS)** da ONU:

| ODS | Descrição | Aplicação no LifeShift |
|-----|-----------|------------------------|
| 🎓 **ODS 4** | Educação de Qualidade | Garantir educação inclusiva e equitativa de qualidade | IA recomenda cursos gratuitos e acessíveis |
| 💼 **ODS 8** | Trabalho Decente e Crescimento Econômico | Promover emprego pleno e produtivo | Facilita requalificação profissional e inclusão no mercado |
| 🤝 **ODS 10** | Redução das Desigualdades | Promover inclusão social e econômica | Democratiza o acesso ao conhecimento tecnológico |

---

## 🧩 Diferenciais da Solução

✨ **IA Realmente Funcional**
- Integração completa com Groq API
- Modelo LLaMA 3.3 configurado e otimizado
- Prompts específicos para gerar planos verificáveis

🔗 **Geração de Links Verificados**
- Apenas cursos reais e acessíveis
- Links diretos sem URLs falsas
- Validação de disponibilidade

🔄 **Fluxo Completo**
- Login seguro via OAuth2
- Geração de plano personalizado
- Visualização e histórico de planos
- Salvamento persistente

🌍 **Internacionalização Aplicada**
- Suporte bilíngue (PT-BR / EN-US)
- Mensagens localizadas
- Interface responsiva

⚡ **Processamento Assíncrono**
- RabbitMQ para tarefas em background
- Geração de planos sem bloquear UI
- Escalabilidade garantida

💾 **Cache Inteligente**
- Spring Cache para performance
- Reutilização de planos similares
- Redução de chamadas à IA

☁️ **Arquitetura Moderna**
- Pronta para deploy em nuvem (Azure Web App / Render)
- Containerização com Docker
- Escalabilidade horizontal

---

## 📦 Dependências Principais

| Dependência | Versão | Propósito |
|-------------|--------|----------|
| Spring Boot | 3.5.7 | Framework web |
| PostgreSQL | 16 | Banco de dados |
| RabbitMQ | 3.x | Message broker |
| Spring Security | - | Autenticação OAuth2 |
| Spring Data JPA | - | Persistência de dados |
| Flyway | - | Versionamento de banco |
| Thymeleaf | - | Template engine |
| Groq API | - | IA Generativa |
| Lombok | - | Redução de boilerplate |

---

## 🎉 Conclusão

O **LifeShift** é mais do que uma aplicação — é uma **ferramenta de impacto social e educacional**.

A solução combina **Inteligência Artificial**, **arquitetura em nuvem** e **inclusão digital** para promover educação acessível, trabalho digno e crescimento pessoal.

Com sua abordagem centrada no usuário e seu compromisso com os valores da **Global Solution FIAP**, o LifeShift se destaca como um exemplo de como a tecnologia pode ser usada para **transformar vidas e construir o futuro do trabalho**.

---
