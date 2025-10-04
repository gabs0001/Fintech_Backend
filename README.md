# 🏦 Fintech - Backend

## 🎯 Sobre o Projeto

Este repositório contém o código-fonte do *backend* do projeto **Fintech** — um sistema acadêmico individual desenvolvido para a **FIAP** com o objetivo de fornecer aos usuários uma ferramenta completa de **gerenciamento de finanças pessoais**.

O sistema permitirá um controle detalhado da vida financeira do usuário, abrangendo:

  * **Gestão de Transações:** Registro de **gastos** e **recebimentos**.
  * **Controle de Investimentos:** Acompanhamento de aplicações financeiras.
  * **Definição de Objetivos:** Criação de metas financeiras e monitoramento do progresso.

O *backend* está em **fase de construção**, focado na implementação das regras de negócio e na estrutura de persistência de dados.

## ⚙️ Tecnologias Utilizadas

| Tecnologia | Descrição |
| :--- | :--- |
| **Java** | Linguagem principal de desenvolvimento. |
| **Maven** | Ferramenta de gerenciamento de dependências e *build*. |
| **Oracle Database** | Sistema de Gerenciamento de Banco de Dados (SGBD) utilizado para persistência dos dados. |
| **IntelliJ IDEA** | IDE utilizada para o desenvolvimento. |

## 🚀 Status do Projeto

O projeto encontra-se em **fase de construção/desenvolvimento**.

  * **Front-end:** A fase de desenvolvimento do *frontend* está em reta final.
  * **Backend:** Focado na criação da estrutura de classes, lógica de negócio e na integração inicial com o banco de dados Oracle.

### 🔜 Próximas Implementações

  * Implementação de uma **API (Application Programming Interface)** completa para comunicação com o *frontend*.
  * Mapeamento completo das entidades do banco de dados (DAO/Service).
  * Criação dos *endpoints* RESTful para as operações CRUD (Criar, Ler, Atualizar, Deletar).

## 🔗 Projetos Relacionados

  * **Front-end do Projeto:** Para acompanhar o desenvolvimento da interface do usuário, acesse o repositório do *frontend*:
      * [**Fintech - Frontend**](https://github.com/gabs0001/Fintech)

## 🛠️ Como Executar o Projeto (Em Desenvolvimento)

Como o projeto está em desenvolvimento e a API ainda será implementada, os passos abaixo são para configurar o ambiente de desenvolvimento:

1.  **Clone o Repositório:**
    ```bash
    git clone https://github.com/SEU_USUARIO/SEU_REPO_FINTECH.git
    cd SEU_REPO_FINTECH
    ```
2.  **Configuração do Banco de Dados:**
      * Garanta que você tenha uma instância do **Oracle Database** em execução.
      * Atualize as credenciais de conexão no arquivo de configuração (dependendo de como você está fazendo a conexão JDBC/Hibernate) com os dados do seu ambiente local.
3.  **Abra no IntelliJ IDEA:**
      * Abra o projeto (`File` \> `Open...`) e selecione a pasta raiz.
      * O Maven deve baixar automaticamente todas as dependências listadas no arquivo `pom.xml`.

## 🧑‍💻 Contato

Para dúvidas, sugestões ou mais informações sobre o projeto, você pode me encontrar no GitHub:

  * **Criador:** [@gabs0001]
