# Rodando Localmente

Existem dois caminhos para executar o projeto:

| | Local | Docker |
|---|---|---|
| Banco | H2 (arquivo) | PostgreSQL 16 |
| Perfil Spring | `local` | `dev` |
| Pré-requisitos | Java 21, Maven, Node 24 | Docker |
| Dados persistem? | Sim (`data/`) | Sim (`data/postgres/`) |

---

## Opção A — Local (H2)

Ideal para desenvolvimento sem dependências externas.

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Node.js 24 / npm 10+

### 1. API

```bash
# Instala o módulo EJB no repositório Maven local (obrigatório)
mvn -f ejb/pom.xml install -DskipTests

# Sobe a API com banco H2 em arquivo
mvn -f api/pom.xml spring-boot:run -Dspring-boot.run.profiles=local
```

O banco H2 é criado automaticamente em `data/beneficiodb.mv.db`.

### 2. Frontend

```bash
cd web
npm install
npm start        # ng serve com proxy para http://localhost:8080
```

### URLs

| Serviço | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console | http://localhost:8080/h2-console |

> **H2 Console:** JDBC URL = `jdbc:h2:file:./data/beneficiodb` · User = `sa` · Password = _(vazio)_

---

## Opção B — Docker Compose (PostgreSQL)

Sobe toda a stack (PostgreSQL + API + Web) com um único comando.

### Pré-requisitos

- Docker Engine 24+
- Docker Compose plugin (`docker compose`)

### Subir

```bash
docker compose up --build
```

Na primeira execução o build das imagens leva alguns minutos.
O PostgreSQL é inicializado automaticamente com `database/schema.sql` e `database/seed.sql`.

### Parar

```bash
docker compose down          # mantém os dados
docker compose down -v       # remove volumes também
```

### URLs

| Serviço | URL |
|---|---|
| Frontend | http://localhost:4200 |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5432 (user: postgres / pass: postgres) |

### Volumes

Todos os dados persistem na pasta `data/` do projeto:

| Dado | Caminho |
|---|---|
| PostgreSQL | `./data/postgres/` |
| H2 (modo local) | `./data/beneficiodb.mv.db` |

---

## Perfis Spring Boot

| Perfil | Banco | Uso |
|---|---|---|
| `local` | H2 arquivo (`./data/`) | desenvolvimento local |
| `dev` | PostgreSQL (Docker) | docker compose |
| `prod` | PostgreSQL (externo) | produção |
| `test` | H2 in-memory | testes automatizados |

Para sobrescrever a URL do banco no perfil `dev`:

```bash
DB_URL=jdbc:postgresql://host:5432/db DB_USER=usr DB_PASS=pwd docker compose up
```
