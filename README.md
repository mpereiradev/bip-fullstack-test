# BIP — Benefício Integrado Platform

Aplicação fullstack de gestão de benefícios com arquitetura em camadas: Angular → Spring Boot → Jakarta EJB → PostgreSQL.

## Arquitetura

```
Browser
  └─ Angular 21 (porta 4200)
        │ HTTP /api/v1/**  (proxy → 8080)
Spring Boot 4 (porta 8080)
  └─ BeneficioController   REST endpoints
  └─ BeneficioService      orquestração
  └─ GlobalExceptionHandler HTTP 400 / 404 / 422 / 500
        │ chama
Jakarta EJB (JAR embutido)
  └─ BeneficioEjbService   @Stateless + @Transactional
        │ @PersistenceContext + PESSIMISTIC_WRITE + @Version
PostgreSQL / H2
  └─ Tabela BENEFICIO
```

## Stack

| Camada | Tecnologia | Versão |
|---|---|---|
| Frontend | Angular + TailwindCSS | 21 / 4.2 |
| API | Spring Boot | 4.0 |
| EJB | Jakarta EJB | 4.0 |
| ORM | Spring Data JPA / Hibernate | 7 |
| Banco (dev/prod) | PostgreSQL | 16 |
| Banco (local/test) | H2 | — |
| Runtime | Java / JVM | 21 (virtual threads) |
| Build | Maven / npm | 3.9 / 10 |
| Infra | Docker / Docker Compose | — |

## Estrutura do Projeto

```
├── database/          scripts SQL (schema + seed)
├── ejb/               serviço de negócio (Jakarta EJB)
├── api/               REST API (Spring Boot)
│   └── Dockerfile
├── web/               SPA (Angular)
│   ├── Dockerfile
│   └── nginx.conf
├── docs/              documentação e challenge original
├── data/              volumes locais (H2 e PostgreSQL — gitignored)
├── docker-compose.yml ambiente dev completo
└── .github/workflows/ CI (build + testes + docker)
```

## Quick Start

### Opção 1 — Local (H2, sem Docker)

**Pré-requisitos:** Java 21, Maven 3.9+, Node.js 24

```bash
# 1. API
mvn -f ejb/pom.xml install -DskipTests
mvn -f api/pom.xml spring-boot:run -Dspring-boot.run.profiles=local

# 2. Frontend (outro terminal)
cd web && npm install && npm start
```

- App: http://localhost:4200
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/beneficiodb`)

### Opção 2 — Docker Compose (PostgreSQL)

**Pré-requisitos:** Docker com Compose plugin

```bash
docker compose up --build
```

- App: http://localhost:4200
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

Os dados do PostgreSQL persistem em `./data/postgres/`.

## API — Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/v1/beneficios` | listar todos |
| GET | `/api/v1/beneficios/{id}` | buscar por ID |
| POST | `/api/v1/beneficios` | criar |
| PUT | `/api/v1/beneficios/{id}` | atualizar |
| DELETE | `/api/v1/beneficios/{id}` | excluir |
| POST | `/api/v1/beneficios/transfer` | transferir saldo |

Documentação interativa: `http://localhost:8080/swagger-ui.html`

## Testes

```bash
# EJB
mvn -f ejb/pom.xml test

# API
mvn -f ejb/pom.xml install -DskipTests
mvn -f api/pom.xml test

# Frontend
cd web && npm test -- --watch=false --browsers=ChromeHeadless
```

## Decisões de Arquitetura

- **EJB embutido no Spring Boot** — sem servidor de aplicação externo; o Spring gerencia o ciclo de vida via proxy.
- **Locking pessimista** — `PESSIMISTIC_WRITE` + `@Version` na transferência para evitar lost-update em operações concorrentes.
- **Virtual Threads** — `spring.threads.virtual.enabled=true` habilita Project Loom no Tomcat (Java 21), maximizando throughput sem configuração extra.
- **Perfis separados** — `local` (H2 file), `dev` (PostgreSQL via Docker), `prod` (PostgreSQL externo), `test` (H2 in-memory).
