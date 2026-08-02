# API REST Reference

Endpoints completos do JavaBank. Todos os endpoints estão prefixados com `/accounts`.

## Base URL

```
http://localhost:8080/accounts
```

## Endpoints

### 1. Listar todas as contas

**GET** `/accounts`

Retorna uma lista de todas as contas no sistema.

**Resposta:** 200 OK

```json
[
  {
    "id": 1,
    "holderName": "João Silva",
    "accountNumber": "123456",
    "balance": 1000.50
  },
  {
    "id": 2,
    "holderName": "Maria Santos",
    "accountNumber": "789012",
    "balance": 500.00
  }
]
```

**Exemplo com curl:**

```bash
curl http://localhost:8080/accounts
```

---

### 2. Criar uma nova conta

**POST** `/accounts`

Cria uma nova conta bancária.

**Request body:**

```json
{
  "holderName": "João Silva",
  "accountNumber": "123456",
  "balance": 0.0
}
```

**Campo:** 
- `holderName` (string, required): Nome do titular (sem limite de caracteres)
- `accountNumber` (string, required, unique): Número da conta (deve ser único)
- `balance` (number, optional): Saldo inicial (padrão: 0.0)

**Resposta:** 200 OK

```json
{
  "id": 1,
  "holderName": "João Silva",
  "accountNumber": "123456",
  "balance": 0.0
}
```

**Erros:**
- `400 Bad Request`: accountNumber duplicado
- `500 Internal Server Error`: Falha no banco de dados

**Exemplo com curl:**

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "João Silva",
    "accountNumber": "123456",
    "balance": 0.0
  }'
```

---

### 3. Depositar dinheiro

**POST** `/accounts/{accountNumber}/deposit`

Adiciona fundos à conta.

**Path parameter:**
- `accountNumber` (string, required): Número da conta

**Query parameter:**
- `amount` (number, required): Valor a depositar (deve ser > 0)

**Resposta:** 200 OK

```json
{
  "id": 1,
  "holderName": "João Silva",
  "accountNumber": "123456",
  "balance": 100.50
}
```

**Erros:**
- `400 Bad Request`: `amount <= 0` ou conta não encontrada
- `404 Not Found`: Conta não existe

**Exemplo com curl:**

```bash
curl -X POST "http://localhost:8080/accounts/123456/deposit?amount=50.00"
```

---

### 4. Sacar dinheiro

**POST** `/accounts/{accountNumber}/withdraw`

Remove fundos da conta. O sistema valida saldo suficiente.

**Path parameter:**
- `accountNumber` (string, required): Número da conta

**Query parameter:**
- `amount` (number, required): Valor a sacar (deve ser > 0 e ≤ saldo)

**Resposta:** 200 OK

```json
{
  "id": 1,
  "holderName": "João Silva",
  "accountNumber": "123456",
  "balance": 49.50
}
```

**Erros:**
- `400 Bad Request`: 
  - `amount <= 0`
  - `amount > balance` (saldo insuficiente)
  - Conta não encontrada
- `404 Not Found`: Conta não existe

**Exemplo com curl:**

```bash
curl -X POST "http://localhost:8080/accounts/123456/withdraw?amount=50.00"
```

---

## Status Codes

| Code | Significado |
|------|-------------|
| 200 | OK — Requisição bem-sucedida |
| 400 | Bad Request — Parâmetros inválidos (amount <= 0, saldo insuficiente, etc) |
| 404 | Not Found — Conta não existe |
| 500 | Internal Server Error — Erro no servidor |

---

## Model: Account

```json
{
  "id": 1,
  "holderName": "string",
  "accountNumber": "string",
  "balance": 0.0
}
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | ID único (gerado automaticamente) |
| `holderName` | String | Nome do titular da conta (não-nulo) |
| `accountNumber` | String | Número da conta (único, não-nulo) |
| `balance` | Double | Saldo atual em reais (padrão: 0.0, não-nulo) |

---

## CORS

Todos os endpoints aceitam requisições Cross-Origin:

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: *
```

Isso permite que a interface web em `http://localhost:8080` chame a API.

---

## Exemplo: Workflow completo

```bash
# 1. Listar contas (vazio no início)
curl http://localhost:8080/accounts

# 2. Criar conta
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"holderName":"João","accountNumber":"123456"}'

# 3. Depositar 100 reais
curl -X POST "http://localhost:8080/accounts/123456/deposit?amount=100"

# 4. Sacar 25 reais
curl -X POST "http://localhost:8080/accounts/123456/withdraw?amount=25"

# 5. Verificar saldo (listar)
curl http://localhost:8080/accounts
# Esperado: balance = 75.0
```
