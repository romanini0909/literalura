# Literalura 📚

## Descrição do Projeto

O Literalura é uma aplicação de linha de comando desenvolvida em Java e Spring Boot, que interage com a API do Gutendex para buscar e armazenar informações sobre livros e seus autores em um banco de dados PostgreSQL. O objetivo principal é catalogar obras literárias, permitindo consultas e análises básicas sobre o acervo.

Este projeto faz parte do programa ONE - Oracle Next Education em parceria com a Alura, focado no desenvolvimento backend com Java.

---

## Funcionalidades ✨

O aplicativo oferece as seguintes opções no menu principal:

* **1. Buscar livro pelo título:** Realiza uma busca na API do Gutendex pelo título do livro, exibe os detalhes e, se o livro ainda não estiver cadastrado, o salva no banco de dados, incluindo seus autores.
* **2. Listar livros registrados:** Exibe todos os livros que foram previamente salvos no banco de dados, mostrando título, autores, idiomas e número de downloads.
* **3. Listar autores registrados:** Apresenta uma lista de todos os autores salvos, incluindo seus anos de nascimento e falecimento, e os títulos dos livros associados a eles.
* **4. Listar autores vivos em um determinado ano:** Permite consultar autores que estavam vivos em um ano específico fornecido pelo usuário.
* **5. Listar livros em um determinado idioma:** Filtra e exibe livros com base no idioma especificado (ex: "en" para inglês, "pt" para português), mostrando um total de livros encontrados.

---

## Tecnologias Utilizadas 🛠️

As seguintes tecnologias foram empregadas no desenvolvimento deste projeto:

* **Java 17:** Linguagem de programação principal.
* **Spring Boot 3.x:** Framework para simplificar o desenvolvimento de aplicações Java.
* **Spring Data JPA:** Para interação com o banco de dados e mapeamento objeto-relacional.
* **Hibernate:** Implementação padrão do JPA utilizada.
* **PostgreSQL:** Sistema de gerenciamento de banco de dados relacional.
* **Maven:** Ferramenta de automação de build e gerenciamento de dependências.
* **Jackson (Jackson-databind):** Para serialização e desserialização de JSON.
* **Gutendex API:** API pública utilizada para buscar dados de livros.

---

## Como Rodar o Projeto 🚀

Siga os passos abaixo para configurar e executar a aplicação em sua máquina local.

### Pré-requisitos

Certifique-se de ter instalado:

* **JDK 17** ou superior
* **Maven**
* **PostgreSQL** (e um cliente como pgAdmin, DBeaver ou psql para gerenciamento)

### Configuração do Banco de Dados

1.  Crie um banco de dados PostgreSQL. Por exemplo, `literalura_db`.
2.  Crie um usuário PostgreSQL e defina uma senha. Por exemplo, `literalura_user` e `sua_senha_secreta`.
3.  **Configure as variáveis de ambiente** no seu sistema operacional (ou no ambiente de execução do IDE) para as credenciais do banco de dados.
    * `NOME_BANCO_APLICACAO = literalura_db`
    * `BANCO_APLICACAO = literalura_user`
    * `MINHA_SENHA = sua_senha_secreta`
    **(Lembre-se de reiniciar seu terminal/IDE após definir as variáveis de ambiente persistentes.)**

### Clonando o Repositório
