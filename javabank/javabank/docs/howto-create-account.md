# Como criar uma conta

Este guia mostra como criar uma nova conta bancária no JavaBank.

## Pré-requisitos

- JavaBank rodando em `http://localhost:8080`

## Via Interface Web

### Passo a passo

1. Abra seu navegador em `http://localhost:8080`

2. Você verá um formulário na parte superior intitulado **"Criar Nova Conta"**

3. Preencha os campos:
   - **Nome do Titular:** Digite o nome da pessoa (ex: `João Silva`)
   - **Número da Conta:** Digite um número único (ex: `123456` ou `999-999-999`)

4. Clique no botão **"Criar Conta"**

### Esperado

Uma mensagem verde aparecerá:
```
✓ Conta criada com sucesso!
```

A nova conta aparecerá na seção **"Minhas Contas"** abaixo com:
- Nome do titular
- Número da conta
- Saldo inicial: R$ 0.00
- Botões para depositar e sacar

---

## Via API (curl/Postman)

### Requisição

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "João Silva",
    "accountNumber": "123456",
    "balance": 0.0
  }'
```

### Parâmetros

**Body (JSON):**

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `holderName` | string | ✅ | Nome do titular (qualquer tamanho) |
| `accountNumber` | string | ✅ | Número único da conta (sem espaços, alfanumérico) |
| `balance` | number | ❌ | Saldo inicial (padrão: 0.0) |

### Resposta (201 Created ou 200 OK)

```json
{
  "id": 1,
  "holderName": "João Silva",
  "accountNumber": "123456",
  "balance": 0.0
}
```

**O que cada campo significa:**
- `id`: ID gerado automaticamente (único no banco de dados)
- `holderName`: Nome que você forneceu
- `accountNumber`: Número que você forneceu
- `balance`: Saldo inicial (0.0 se não fornecido)

---

## Regras e Limitações

✅ **Permitido:**
- Qualquer nome (ex: `João da Silva`, `MARIA`, `123 Corporation`)
- Números de conta com dígitos e hífens (ex: `123-456-789`)
- Múltiplas contas com o mesmo titular (mesmos nomes, números diferentes)
- Saldo inicial zero ou positivo

❌ **Rejeitado:**
- Número de conta duplicado (cada número é único)
  - Erro: `400 Bad Request` — "Constraint violation"
- Sem nome ou número (ambos obrigatórios)
- Saldo negativo (se fornecido)

---

## Exemplos

### Exemplo 1: Conta pessoal

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "Maria Santos",
    "accountNumber": "555-888-2024"
  }'
```

**Resultado:**
```json
{
  "id": 2,
  "holderName": "Maria Santos",
  "accountNumber": "555-888-2024",
  "balance": 0.0
}
```

### Exemplo 2: Conta com saldo inicial

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "Empresa XYZ Ltda",
    "accountNumber": "corp-001",
    "balance": 10000.00
  }'
```

**Resultado:**
```json
{
  "id": 3,
  "holderName": "Empresa XYZ Ltda",
  "accountNumber": "corp-001",
  "balance": 10000.00
}
```

### Exemplo 3: Duplicação (erro)

```bash
# Primeira chamada — sucesso
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"holderName":"João","accountNumber":"123"}'

# Segunda chamada com mesmo número — erro!
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"holderName":"Outro João","accountNumber":"123"}'

# Resposta:
# 400 Bad Request
# "Constraint violation: unique constraint on accountNumber"
```

**Solução:** Use um número de conta diferente.

---

## Troubleshooting

### "Constraint violation"

**Problema:** Tentou criar uma conta com um número que já existe.

**Solução:** Use um número de conta único. Exemplos:
- `123456`
- `123457`
- `abc-001`
- `maria-2024`

### Erro 500

**Problema:** Erro no servidor.

**Solução:**
1. Verifique que o servidor está rodando
2. Verifique que o JSON está válido (use `curl -v` pra ver erro exato)
3. Reinicie o servidor: `Ctrl+C` e `java -jar target/...`

### JSON inválido

**Problema:** Campo com formato errado.

**Solução:** Certifique-se que:
- Nomes estão entre aspas: `"holderName": "João"` (não `holderName: João`)
- Balancé está sem aspas (número): `"balance": 100.0` (não `"balance": "100.0"`)
- Sem vírgula extra no final do objeto JSON

---

Depois de criar uma conta, você pode [depositar e sacar dinheiro](./howto-deposit-withdraw.md).
