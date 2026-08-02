# Arquitetura em Camadas do JavaBank

Este documento explica por que o JavaBank usa uma arquitetura em camadas (controller → service → repository) e quais benefícios isso traz.

## O Problema

Imagine você escrevendo toda a lógica do banco diretamente no controller REST:

```java
@PostMapping("/withdraw")
public ResponseEntity<Account> withdraw(@PathVariable String number, @RequestParam Double amount) {
  // Validação: amount > 0?
  // Validação: conta existe?
  // Validação: saldo suficiente?
  // Atualizar banco de dados
  // Retornar resposta
  // Lançar exceção se erro
  // Fazer logging
}
```

**Problemas:**

1. **Responsabilidades misturadas:** O controller maneja HTTP, validação, negócio, banco de dados — tudo junto
2. **Difícil testar:** Pra testar a lógica de saque, você precisa simular HTTP e banco de dados
3. **Código duplicado:** Se você quer usar saque em outro lugar (agendado, em lote, via gRPC), reescreve tudo
4. **Difícil manter:** Mudança em uma validação afeta HTTP, testes, documentação — tudo

## A Solução: Camadas

JavaBank separa responsabilidades em 4 camadas:

```
┌─────────────────────────────┐
│   REST Controller           │  ← HTTP requests/responses
│   (HTTP, routing)           │
└──────────┬──────────────────┘
           │
┌──────────▼──────────────────┐
│   Service                   │  ← Lógica de negócio
│   (validação, transações)   │
└──────────┬──────────────────┘
           │
┌──────────▼──────────────────┐
│   Repository (DAO)          │  ← Acesso a dados
│   (queries, persistência)   │
└──────────┬──────────────────┘
           │
┌──────────▼──────────────────┐
│   Database                  │  ← H2 ou PostgreSQL
│   (dados armazenados)       │
└─────────────────────────────┘
```

### Camada 1: Controller

**Arquivo:** `controller/AccountController.java`

**Responsabilidade:** Traduzir requisições HTTP em chamadas de negócio.

```java
@RestController
@RequestMapping("/accounts")
public class AccountController {
  @PostMapping("/{accountNumber}/withdraw")
  public ResponseEntity<Account> withdraw(
      @PathVariable String accountNumber,
      @RequestParam Double amount) {
    Account result = accountService.withdraw(accountNumber, amount);
    return ResponseEntity.ok(result);
  }
}
```

**O que ele faz:**
- Recebe parâmetros HTTP (`@PathVariable`, `@RequestParam`)
- Chama o service
- Retorna JSON (ResponseEntity)
- Não conhece banco de dados
- Não valida negócio

**O que ele não faz:**
- Validação de saldo ❌
- Queries SQL ❌
- Transações de banco ❌

---

### Camada 2: Service

**Arquivo:** `service/AccountService.java`

**Responsabilidade:** Aplicar regras de negócio.

```java
@Service
public class AccountService {
  @Transactional
  public Account withdraw(String accountNumber, Double amount) {
    // 1. Validar: amount > 0
    if (amount <= 0) {
      throw new RuntimeException("Valor inválido");
    }
    
    // 2. Validar: conta existe
    Account account = accountRepository.findByAccountNumber(accountNumber)
      .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    
    // 3. Validar: saldo suficiente
    if (account.getBalance() < amount) {
      throw new RuntimeException("Saldo insuficiente");
    }
    
    // 4. Executar transação
    account.setBalance(account.getBalance() - amount);
    return accountRepository.save(account);
  }
}
```

**O que ele faz:**
- Valida regras de negócio (saldo, amount > 0)
- Orquestra passos (buscar conta, validar, atualizar)
- Marca com `@Transactional` pra garantir consistência
- Lança exceções significativas
- Não conhece HTTP

**O que ele não faz:**
- Processar HTTP ❌
- Executar queries SQL diretamente ❌

---

### Camada 3: Repository

**Arquivo:** `repository/AccountRepository.java`

**Responsabilidade:** Acesso a dados via JPA.

```java
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByAccountNumber(String accountNumber);
}
```

**O que ele faz:**
- Define queries (Spring Data JPA gera SQL automaticamente)
- CRUD: Create (save), Read (findByAccountNumber), Update (save), Delete (delete)
- Abstrai o banco de dados (funciona com H2, PostgreSQL, MySQL, etc)
- Não escreve SQL manualmente

**O que ele não faz:**
- Validação de negócio ❌
- HTTP ❌

---

### Camada 4: Model

**Arquivo:** `model/Account.java`

**Responsabilidade:** Estrutura de dados.

```java
@Entity
@Table(name = "accounts")
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String holderName;
  
  @Column(nullable = false, unique = true)
  private String accountNumber;
  
  @Column(nullable = false)
  private Double balance = 0.0;
}
```

Apenas descreve: "uma conta tem id, nome, número e saldo".

---

## Benefícios da Arquitetura em Camadas

### 1. Testes Simples

Testar saque sem HTTP:

```java
@Test
public void testWithdrawValidation() {
  AccountService service = new AccountService(repository);
  
  // Mockar repository
  Account account = new Account("João", "123", 100.0);
  when(repository.findByAccountNumber("123"))
    .thenReturn(Optional.of(account));
  
  // Testar lógica pura
  Account result = service.withdraw("123", 50.0);
  
  assertEquals(50.0, result.getBalance());
}
```

Não precisa:
- Subir servidor HTTP
- Fazer curl
- Mockar responses

### 2. Reutilização

Se você quer sacar em uma **fila de processamento em lote**:

```java
@Scheduled(fixedRate = 60000)
public void processBatchWithdrawals() {
  for (BatchWithdrawalJob job : queue.getAll()) {
    // Reusar a MESMA lógica do service
    Account account = accountService.withdraw(
      job.getAccountNumber(), 
      job.getAmount()
    );
    job.markAsComplete(account);
  }
}
```

Sem camadas, você reescreveria toda a validação.

### 3. Mudanças Localizadas

Precisa mudar a regra de saque (ex: cobrar 2% de taxa)?

```java
// Muda só no service
public Account withdraw(String accountNumber, Double amount) {
  // ... validações ...
  Double fee = amount * 0.02; // NOVA TAXA
  account.setBalance(account.getBalance() - amount - fee);
  return accountRepository.save(account);
}

// Controller, repository, HTTP — tudo funciona igual ✅
```

### 4. Trocar Banco de Dados

Mudar de H2 pra PostgreSQL? Só mude `application.properties`:

```properties
# Antes
spring.datasource.url=jdbc:h2:mem:testdb

# Depois
spring.datasource.url=jdbc:postgresql://localhost:5432/javabank_db
```

Service e controller não sabem da mudança — repository abstrai tudo via JPA.

### 5. Fácil de Entender

Um dev novo:
- Quer usar API? → Lê `AccountController`
- Quer entender validação? → Lê `AccountService`
- Quer adicionar um filtro? → Vai em `AccountRepository`

Cada camada tem uma responsabilidade clara.

---

## Fluxo de uma Requisição de Saque

Passo a passo o que acontece quando você clica "💸 Sacar":

```
1. Browser faz request HTTP
   POST /accounts/123456/withdraw?amount=50

2. Controller recebe
   @PostMapping("/{accountNumber}/withdraw")
   accountNumber = "123456"
   amount = 50.0

3. Controller chama service
   accountService.withdraw("123456", 50.0)

4. Service valida
   ✓ amount > 0? Sim
   ✓ conta existe? Busca no repository
   ✓ saldo >= 50? Sim

5. Service executa transação
   account.setBalance(100 - 50)
   repository.save(account)

6. Repository salva no banco
   UPDATE accounts SET balance = 50 WHERE account_number = '123456'

7. Response volta ao controller
   Return ResponseEntity.ok(account)

8. Controller serializa JSON
   {
     "id": 1,
     "holderName": "João",
     "accountNumber": "123456",
     "balance": 50.0
   }

9. Browser recebe JSON
   Atualiza o saldo na tela
```

Cada camada faz seu trabalho, passa pra próxima.

---

## Princípio: Separação de Responsabilidades (SoC)

Cada camada tem UMA razão pra mudar:

- **Controller muda** se: protocolo HTTP mudar (ex: passar de REST pra gRPC)
- **Service muda** se: regras de negócio mudam (ex: nova taxa, novo critério de saque)
- **Repository muda** se: banco de dados muda (ex: H2 pra MongoDB)
- **Model muda** se: estrutura de dados muda (ex: adicionar "data de abertura")

Se você precisar fazer 4 mudanças de negócio diferentes, você mexe em 4 arquivos, não em 1 arquivo gigante.

---

## Alternativas: Por que não outras arquiteturas?

### ❌ Tudo no controller

```java
@PostMapping("/withdraw")
public Account withdraw(...) {
  // 200 linhas: HTTP, validação, queries, transações
}
```

**Problema:** Impossível testar, reusar, manter.

### ❌ Service + Controller, sem Repository

```java
@Service
public class AccountService {
  public Account withdraw(...) {
    // Service faz queries SQL direto
    jdbcTemplate.update("UPDATE accounts...");
  }
}
```

**Problema:** Difícil trocar banco de dados, sem abstração ORM.

### ✅ Controller + Service + Repository (JavaBank)

**Vantagem:** Clean, testável, escalável, profissional.

---

Essa é a arquitetura padrão em aplicações Java Spring. Outras linguagens usam padrões similares (camadas, MVC, etc) pelo mesmo motivo: **separar responsabilidades torna código melhor.**
