# 🏦 Fintech - Backend (API RESTful)

## 🎯 Sobre o Projeto

Este repositório contém o código-fonte do *backend* do projeto **Fintech** — uma aplicação robusta para **gerenciamento de finanças pessoais**.

O sistema oferece uma API RESTful segura, responsável por implementar toda a lógica de negócio, garantir a persistência dos dados e gerenciar a autenticação dos usuários via JSON Web Tokens (JWT).

### Funcionalidades Chave:

* **Autenticação Segura:** Cadastro e Login de usuários com criptografia de senha (BCrypt) e Tokens JWT.
* **Gestão de Entidades:** CRUD completo para Instituições Financeiras, Tipos de:  Investimento, Gasto, Recebimento com escopo por usuário.
* **Controle Financeiro:** Estrutura para registrar Transações, Investimentos, Gastos, Recebimentos e Objetivos Financeiros.

## ⚙️ Tecnologias Utilizadas

| Categoria | Tecnologia | Descrição |
| :--- | :--- | :--- |
| **Linguagem** | **Java 17+** | Linguagem principal de desenvolvimento. |
| **Framework** | **Spring Boot 3.x** | Facilita a criação de aplicações Spring independentes. |
| **Persistência** | **Spring Data JPA / Hibernate** | Mapeamento Objeto-Relacional (ORM). |
| **Segurança** | **Spring Security / JWT** | Autenticação, Autorização e geração de Tokens JWT. |
| **Banco de Dados**| **Oracle Database** | SGBD utilizado para persistência de dados. |
| **Build Tool** | **Maven** | Gerenciamento de dependências e automação de *build*. |
| **Dev Tools** | **IntelliJ IDEA / Postman** | IDE e ferramenta para testes de API. |

## 🛠️ Como Executar o Projeto

Estes passos guiam você para configurar e rodar a API localmente:

1.  **Clone o Repositório:**
    ```bash
    git clone [https://github.com/SEU_USUARIO/SEU_REPO_FINTECH.git](https://github.com/SEU_USUARIO/SEU_REPO_FINTECH.git)
    cd SEU_REPO_FINTECH
    ```

2.  **Configuração do Banco de Dados Oracle:**
    * Garanta que você tenha uma instância do Oracle Database em execução.
    * Crie as tabelas e sequences necessárias (Ex: `T_SIF_USUARIO`, `SEQ_SIF_USUARIO`).

3.  **Configuração da Conexão:**
    * Abra o arquivo `src/main/resources/application.properties` (ou `.yml`).
    * Defina as credenciais e configurações de conexão JDBC:
        ```properties
        # Exemplo de configuração JDBC para Oracle
        spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/XE 
        spring.datasource.username=seu_usuario_oracle
        spring.datasource.password=sua_senha_oracle
        # Garanta que o Hibernate não altere seu esquema:
        spring.jpa.hibernate.ddl-auto=none 
        ```

4.  **Build do Projeto (Maven):**
    ```bash
    mvn clean install
    ```

5.  **Execução da Aplicação:**
    * Execute a classe principal que contém o `@SpringBootApplication` ou use o Maven:
        ```bash
        mvn spring-boot:run
        ```
    * O servidor deve iniciar na porta 8080 (ou na porta configurada).

## 🔗 Rotas da API (Endpoints)

Todas as rotas exigem o cabeçalho `Authorization: Bearer <TOKEN_JWT>` (exceto as rotas de autenticação).

### Autenticação (`/api/auth`)

| Rota | Método | Descrição | Corpo da Requisição |
| :--- | :--- | :--- | :--- |
| `/api/auth/cadastro` | `POST` | Cria um novo usuário. | `{ "nome": "...", "email": "...", "senha": "..." }` |
| `/api/auth/login` | `POST` | Autentica o usuário e retorna o Token JWT. | `{ "email": "...", "senha": "..." }` |

### Instituições Financeiras (`/api/instituicoes`)

| Rota | Método | Descrição | Requer Token |
| :--- | :--- | :--- | :--- |
| `/api/instituicoes` | `POST` | Cria uma nova instituição e a vincula ao usuário logado. | Sim |
| `/api/instituicoes` | `GET` | Lista todas as instituições do usuário logado. | Sim |
| `/api/instituicoes/{id}` | `GET` | Busca detalhes de uma instituição pelo ID. | Sim |
| `/api/instituicoes/{id}` | `PUT` | Atualiza uma instituição existente. | Sim |
| `/api/instituicoes/{id}` | `DELETE` | Remove uma instituição. | Sim |

### Tipos de Recebimento (`/api/tipos-recebimentos`)

| Rota | Método | Descrição | Requer Token |
| :--- | :--- | :--- | :--- |
| `/api/tipos-recebimento` | `POST` | Cria um novo tipo de recebimento. | Sim |
| `/api/tipos-recebimento` | `GET` | Lista todos os tipos de recebimento. | Sim |
| ... | `PUT` / `DELETE` | (Outras operações CRUD) | Sim |

### Tipos de Investimento (`/api/tipos-investimentos`)

| Rota | Método | Descrição | Requer Token |
| :--- | :--- | :--- | :--- |
| `/api/tipos-investimento` | `POST` | Cria um novo tipo de investimento. | Sim |
| `/api/tipos-investimento` | `GET` | Lista todos os tipos de investimento. | Sim |
| ... | `PUT` / `DELETE` | (Outras operações CRUD) | Sim |
