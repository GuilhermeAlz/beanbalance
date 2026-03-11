# BeanBalance

API REST para gerenciamento de finanças pessoais. Permite controlar contas, transações, categorias e orçamentos mensais por usuário.

## Tecnologias

- Java 21
- Spring Boot 4.0.3
- Spring Security + JWT
- PostgreSQL 17
- Flyway
- Docker / Docker Compose

## Requisitos

- Docker e Docker Compose

## Como rodar

```bash
docker compose up --build
```

A API estará disponível em `http://localhost:8080`.

A documentação interativa (Swagger) estará em `http://localhost:8080/swagger-ui.html`.

## Autenticação

A API usa JWT. Para acessar endpoints protegidos:

1. Crie uma conta via `POST /api/auth/register`
2. Use o token retornado no header `Authorization: Bearer <token>`

O token expira em 24 horas.

## Endpoints principais

| Recurso | Base URL |
|---|---|
| Autenticação | `/api/auth` |
| Contas financeiras | `/api/accounts` |
| Transações | `/api/transactions` |
| Categorias | `/api/categories` |
| Orçamentos | `/api/budgets` |

A documentação completa de cada endpoint está disponível no Swagger.

## Variáveis de ambiente

As variáveis usadas pelo container da API estão definidas no `compose.yaml`. Para produção, substitua os valores padrão das variáveis abaixo:

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão com o banco |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `APP_JWT_SECRET` | Chave secreta para assinatura dos tokens JWT (Base64) |
| `APP_JWT_EXPIRATION_MS` | Tempo de expiração do token em milissegundos |