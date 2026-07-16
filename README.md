# financas-api

[![JDK]>=21](https://img.shields.io/badge/JDK-%3E%3D21-brightgreen)

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

## Como rodar os testes (local e CI)

### Visão geral

Os testes de integração usam um banco PostgreSQL. Para compatibilidade entre CI e desenvolvimento local adotamos uma estratégia híbrida:

- Modo local (padrão): os testes iniciam um Postgres via Testcontainers (requer Docker).
- Modo CI / externo: se as variáveis de ambiente `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` estiverem definidas, os testes usarão esse banco (o workflow do GitHub Actions fornece essas variáveis e um serviço Postgres).

### Pré-requisitos

- Java (o `pom.xml` define `<java.version>21` — ajuste se necessário)
- Docker (para Testcontainers local)
- Maven (`./mvnw` está disponível)

### Rodando testes localmente (Testcontainers)

Por padrão, execute:

```fish
./mvnw -Dspring.profiles.active=test test
```

Isso iniciará um container Postgres para os testes quando `SPRING_DATASOURCE_*` não estiverem definidas.

### Rodando testes usando um Postgres externo (simulando o CI)

Inicie um Postgres localmente e exporte as variáveis antes de executar os testes.

Exemplo (iniciar Postgres via Docker):

```bash
docker run -d --name pg-test \
  -e POSTGRES_DB=bd-api-financas-test \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 postgres:16
```

Exportando variáveis em fish:

```fish
set -x SPRING_DATASOURCE_URL jdbc:postgresql://localhost:5432/bd-api-financas-test
set -x SPRING_DATASOURCE_USERNAME postgres
set -x SPRING_DATASOURCE_PASSWORD postgres
./mvnw -Dspring.profiles.active=test test

# para desexportar quando terminar:
set -e SPRING_DATASOURCE_URL SPRING_DATASOURCE_USERNAME SPRING_DATASOURCE_PASSWORD
```

Observação: a classe de teste base `src/test/java/br/com/financasweb/configurations/AbstractIntegrationTest.java` detecta essas variáveis e usará o banco externo quando presentes.

### CI — GitHub Actions

Um workflow de exemplo está incluído em `.github/workflows/ci.yml`. Ele usa um serviço Postgres no job e define as variáveis `SPRING_DATASOURCE_*` que os testes consomem.

Para executar o workflow localmente ou inspecionar logs, consulte o arquivo: `.github/workflows/ci.yml`.

### Troubleshooting rápido

- Erro de serialização `LocalDate`: verifique se o `ObjectMapper` registra `JavaTimeModule` (o projeto já possui essa configuração).
- Timeouts Hikari ("Connection is not available"): verifique memória do Docker, configurações em `src/test/resources/application-test.yaml` e se as variáveis de conexão estão corretas.
- Se preferir outra versão do JDK no CI, edite `.github/workflows/ci.yml` e/ou `<java.version>` no `pom.xml`.

## Recomendação de JDK (mínimo)

Recomendamos usar JDK 21 ou superior para desenvolvimento e para execução no CI. O `pom.xml` está configurado para `--release 21` e o workflow do GitHub Actions usa JDK 21.

Verifique a versão atual instalada:

```bash
java -version
mvn -v
```

Opções rápidas para instalar/alternar JDK:

- SDKMAN (Linux/macOS — recomendado para desenvolvedores):

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk list java          # procure o identificador para Temurin/OpenJDK 21
sdk install java <identifier-for-temurin-21>
sdk use java <identifier-for-temurin-21>
```

- Debian/Ubuntu (exemplo com pacote Temurin se disponível):

```bash
sudo apt update
sudo apt install -y temurin-21-jdk
```

- macOS (Homebrew):

```bash
brew install temurin@21
brew link --force --overwrite temurin@21
```

Observação: o nome/identificador do pacote pode variar por plataforma — use `sdk list java`, o seu gerenciador de pacotes, ou a página do Adoptium/Temurin para encontrar a versão 21 adequada.

