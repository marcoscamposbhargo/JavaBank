# Como listar todas as contas

Este guia mostra como recuperar a lista de todas as contas no sistema.

## Pré-requisitos

- JavaBank rodando em `http://localhost:8080`

## Via Interface Web

### Passo a passo

1. Abra `http://localhost:8080`

2. A seção **"Minhas Contas"** no meio da página mostra automaticamente todas as contas criadas

3. Cada conta é exibida em um **card** com:
   - **Titular:** Nome do titular
   - **Conta:** Número da conta
   - **Saldo:** Valor atual em R$ (em verde)
   - **Botões:** "Depositar" e "Sacar" para fazer transações

### Comportamento

- A lista atualiza **automaticamente** quando você cria, deposita ou saca
- Se nenhuma conta foi criada ainda, mostra: "Nenhuma conta criada ainda. Crie uma acima! 👆"

---

## Via API (curl/Postman)

### Requisição

```bash
curl http://localhost:8080/accounts
```

Ou com mais detalhes:

```bash
curl -i http://localhost:8080/accounts
```

### Resposta (200 OK)

**Se houver contas:**

```json
[
  {
    "id": 1,
    "holderName": "João Silva",
    "accountNumber": "123456",
    "balance": 150.50
  },
  {
    "id": 2,
    "holderName": "Maria Santos",
    "accountNumber": "789012",
    "balance": 500.00
  },
  {
    "id": 3,
    "holderName": "Empresa XYZ",
    "accountNumber": "corp-001",
    "balance": 10000.00
  }
]
```

**Se não houver contas:**

```json
[]
```

Array vazio.

---

## Entendendo a Resposta

Cada conta retornada tem 4 campos:

| Campo | Tipo | Significado |
|-------|------|-------------|
| `id` | Número | ID único no banco (gerado automaticamente, aumenta a cada nova conta) |
| `holderName` | String | Nome do titular que você forneceu ao criar |
| `accountNumber` | String | Número da conta (único, fornecido ao criar) |
| `balance` | Número | Saldo atual em reais (atualizado a cada depósito/saque) |

---

## Exemplos

### Exemplo 1: Ver todas as contas (sem formatação)

```bash
curl http://localhost:8080/accounts
```

Output (compacto):
```
[{"id":1,"holderName":"João","accountNumber":"123456","balance":100.0},{"id":2,"holderName":"Maria","accountNumber":"789","balance":50.0}]
```

### Exemplo 2: Ver contas com formato legível

Use `jq` (JSON query tool):

```bash
curl http://localhost:8080/accounts | jq
```

Output (formatado):
```json
[
  {
    "id": 1,
    "holderName": "João",
    "accountNumber": "123456",
    "balance": 100.0
  },
  {
    "id": 2,
    "holderName": "Maria",
    "accountNumber": "789",
    "balance": 50.0
  }
]
```

### Exemplo 3: Contar quantas contas existem

```bash
curl http://localhost:8080/accounts | jq 'length'
```

Output:
```
2
```

### Exemplo 4: Ver apenas o saldo total

```bash
curl http://localhost:8080/accounts | jq '[.[].balance] | add'
```

Output (soma de todos os saldos):
```
150.0
```

### Exemplo 5: Ver nomes de todos os titulares

```bash
curl http://localhost:8080/accounts | jq '.[].holderName'
```

Output:
```
"João"
"Maria"
```

---

## HTTP Headers

A resposta inclui:

```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Content-Length: 234
```

**Significado:**
- `200 OK`: Requisição bem-sucedida
- `Content-Type: application/json`: Resposta é JSON
- `Content-Length`: Tamanho da resposta em bytes

---

## Filtrar/Buscar Contas

A API atual **não oferece filtros** (não pode buscar por nome, número, ou saldo mínimo). 

Para filtrar, você precisa:

1. Listar todas as contas (este endpoint)
2. Processar no seu código/script

**Exemplo — Contas com saldo > 100:**

```bash
curl http://localhost:8080/accounts | jq '.[] | select(.balance > 100)'
```

Output:
```json
{
  "id": 1,
  "holderName": "João",
  "accountNumber": "123456",
  "balance": 150.50
}
```

**Exemplo — Contas de um titular específico:**

```bash
curl http://localhost:8080/accounts | jq '.[] | select(.holderName == "João")'
```

---

## Performance

- Retorna **todas** as contas de uma vez (sem paginação)
- Rápido se <10K contas
- Em produção com muitas contas, seria bom adicionar paginação (limite/offset)

---

## Troubleshooting

### Resposta vazia (array vazio)

**Problema:** Nenhuma conta foi criada ainda.

**Solução:** [Crie uma conta](./howto-create-account.md) antes.

### Erro 500

**Problema:** Erro no servidor.

**Solução:** Verifique que o servidor está rodando: `java -jar target/javabank-0.0.1-SNAPSHOT.jar`

### Timeout (requisição demora)

**Problema:** Servidor lento ou offline.

**Solução:** Reinicie o servidor.

---

Agora que você sabe listar contas, tente [criar uma nova](./howto-create-account.md) ou [fazer transações](./howto-deposit-withdraw.md).
