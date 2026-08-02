# Como depositar e sacar dinheiro

Este guia mostra como depositar e sacar fundos de uma conta usando a web ou API.

## Pré-requisitos

- JavaBank rodando em `http://localhost:8080`
- Pelo menos uma conta criada (veja [Tutorial Getting Started](./tutorial-getting-started.md))

## Via Interface Web

### Depositar

1. Abra `http://localhost:8080`
2. Localize a conta desejada na seção "Minhas Contas"
3. Clique no botão **"💰 Depositar"**
4. Uma janela de prompt aparecerá pedindo o valor
5. Digite o valor (ex: `50`) — sem R$, apenas números e ponto se decimal
6. Pressione Enter ou OK

**Esperado:** Uma mensagem verde "✓ Depósito de R$ X,XX realizado!" e o saldo atualiza.

### Sacar

1. Localize a conta desejada em "Minhas Contas"
2. Clique no botão **"💸 Sacar"**
3. Uma janela de prompt aparecerá
4. Digite o valor a sacar

**Esperado:** Se o saldo for suficiente, a mensagem "✓ Saque de R$ X,XX realizado!" aparece e o saldo atualiza.

**Se saque falhar:**
```
Saldo insuficiente ou conta não encontrada!
```

Verifique se:
- O valor é menor ou igual ao saldo atual
- A conta existe

---

## Via API (curl/Postman)

### Depositar via API

```bash
curl -X POST "http://localhost:8080/accounts/{accountNumber}/deposit?amount={valor}"
```

**Exemplo — Depositar R$ 100 na conta 123456:**

```bash
curl -X POST "http://localhost:8080/accounts/123456/deposit?amount=100"
```

**Resposta esperada (200 OK):**

```json
{
  "id": 1,
  "holderName": "João Silva",
  "accountNumber": "123456",
  "balance": 100.0
}
```

### Sacar via API

```bash
curl -X POST "http://localhost:8080/accounts/{accountNumber}/withdraw?amount={valor}"
```

**Exemplo — Sacar R$ 25 da conta 123456:**

```bash
curl -X POST "http://localhost:8080/accounts/123456/withdraw?amount=25"
```

**Resposta esperada (200 OK):**

```json
{
  "id": 1,
  "holderName": "João Silva",
  "accountNumber": "123456",
  "balance": 75.0
}
```

---

## Regras de Validação

### Depósito

✅ **Permitido:**
- Valor > 0
- Qualquer valor decimal (ex: 0.01, 99.99)
- Conta deve existir

❌ **Rejeitado:**
- Valor ≤ 0 → "O valor do depósito deve ser maior que zero."
- Conta não existe → "Conta não encontrada!"

### Saque

✅ **Permitido:**
- Valor > 0
- Valor ≤ saldo atual
- Conta deve existir

❌ **Rejeitado:**
- Valor ≤ 0 → "Saldo insuficiente ou valor inválido."
- Valor > saldo → "Saldo insuficiente ou valor inválido."
- Conta não existe → "Saldo insuficiente ou valor inválido."

---

## Troubleshooting

### "Saldo insuficiente"

**Problema:** Tentou sacar mais do que tem na conta.

**Solução:** 
1. Clique "Listar Contas" (se necessário, recarregue a página)
2. Verifique o saldo atual
3. Tente novamente com um valor menor

### "Conta não encontrada!"

**Problema:** O número da conta está incorreto ou foi digitado errado.

**Solução:**
1. Verifique o número da conta na seção "Minhas Contas"
2. Copie e cole o número exato
3. Tente novamente

### Timeout (requisição demora muito)

**Problema:** O servidor pode estar lento ou offline.

**Solução:**
1. Verifique que o servidor está rodando: `java -jar target/javabank-0.0.1-SNAPSHOT.jar`
2. Abra `http://localhost:8080` e veja se carrega
3. Se não carregar, reinicie o servidor

---

## Exemplo: Workflow de transações

```bash
# 1. Criar uma conta
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"holderName":"Maria","accountNumber":"999999"}'

# Resultado: id=2, accountNumber=999999, balance=0.0

# 2. Depositar 500
curl -X POST "http://localhost:8080/accounts/999999/deposit?amount=500"
# balance = 500.0

# 3. Depositar mais 250
curl -X POST "http://localhost:8080/accounts/999999/deposit?amount=250"
# balance = 750.0

# 4. Sacar 100
curl -X POST "http://localhost:8080/accounts/999999/withdraw?amount=100"
# balance = 650.0

# 5. Listar contas (verificar saldo)
curl http://localhost:8080/accounts
# Esperado: balance = 650.0
```

---

Veja [API REST Reference](./reference-api.md) para documentação completa dos endpoints.
