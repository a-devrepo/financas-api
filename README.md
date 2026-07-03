````markdown
# financas-api

API REST desenvolvida com Spring Boot para gerenciamento de movimentações financeiras, permitindo o cadastro e a consulta de receitas e despesas organizadas por categorias.

Este projeto possui fins de estudo e será integrado ao projeto **web-financas**, desenvolvido com Angular 21.

## Tecnologias

- Java
- Spring Boot
- Spring Data JPA
- Lombok
- PostgreSQL
- Docker
- Maven

## Banco de Dados

A aplicação utiliza o PostgreSQL como banco de dados, executado em um container Docker.

## Integração

Frontend da aplicação:

https://github.com/a-devrepo/web-financas

## Estrutura do Projeto

O projeto segue uma arquitetura em camadas, separando as responsabilidades entre:

- Controllers
- Services
- Repositories
- Entities
- DTOs
- Configurations
- Exceptions

## Funcionalidades

Atualmente a API permite:

- Cadastro de movimentações financeiras
- Consulta de movimentações
- Organização das movimentações por categorias

## Próximas Implementações

- Integração com RabbitMQ para processamento assíncrono de eventos
- Novos endpoints
- Melhorias de validação
- Documentação da API
- Testes automatizados

## Como executar

### Clone o repositório

```bash
git clone https://github.com/a-devrepo/financas-api.git
````

### Inicie os containers

```bash
docker compose up -d
```

### Execute a aplicação

```bash
mvn spring-boot:run
```

Ou execute a classe principal da aplicação pela sua IDE.

## Licença

Projeto desenvolvido para fins de estudo.

```
```