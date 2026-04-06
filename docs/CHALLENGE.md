# Desafio Fullstack Integrado

> **Instrução:** NÃO faça fork deste repositório. Clone-o e crie um novo repositório público na sua conta GitHub.

## Objetivo

Criar solução completa em camadas (DB → EJB → API → Frontend), corrigindo bug no serviço EJB e entregando aplicação funcional.

## Tarefas

1. Executar `database/schema.sql` e `database/seed.sql`
2. Corrigir bug no `BeneficioEjbService` (transferência sem validação de saldo e sem locking)
3. Implementar API REST (CRUD + transferência) integrada ao EJB
4. Desenvolver frontend Angular consumindo a API
5. Implementar testes (unitários + integração)
6. Documentar com Swagger e README
7. Enviar link do repositório para análise

## Bug no EJB

`BeneficioEjbService.transferir` não valida saldo, não usa locking pessimista e não faz rollback em falha — pode gerar inconsistência de dados em cenários concorrentes.

Correção esperada: validações de negócio, `LockModeType.PESSIMISTIC_WRITE`, `@Version` para optimistic locking de fallback e rollback automático via `@Transactional`.

## Critérios de Avaliação

| Critério | Peso |
|---|---|
| Arquitetura em camadas | 20% |
| Correção do EJB | 20% |
| CRUD + Transferência | 15% |
| Qualidade de código | 10% |
| Testes | 15% |
| Documentação | 10% |
| Frontend | 10% |
