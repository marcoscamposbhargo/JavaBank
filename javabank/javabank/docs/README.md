# JavaBank Documentation

JavaBank é uma API REST de gerenciamento de contas bancárias construída com Spring Boot e banco de dados H2 (ou PostgreSQL em produção).

## Documentação por Tipo

### 📚 Tutoriais
- [Começando com JavaBank](./tutorial-getting-started.md) — Instale, rode e crie sua primeira conta em 5 minutos

### 🎯 Como Fazer
- [Como criar uma conta](./howto-create-account.md)
- [Como depositar e sacar dinheiro](./howto-deposit-withdraw.md)
- [Como listar todas as contas](./howto-list-accounts.md)

### 📖 Referência
- [API REST Reference](./reference-api.md) — Endpoints, request/response, status codes
- [Model Account](./reference-model.md) — Estrutura de dados de conta
- [AccountService](./reference-service.md) — Camada de negócio
- [Configuração](./reference-config.md) — Propriedades da aplicação

### 💡 Explicação
- [Arquitetura em Camadas](./explanation-architecture.md) — Por que controller → service → repository
- [Validação de Saldo](./explanation-balance-validation.md) — Regras de negócio de saque

---

## Quick Start

```bash
# Clone ou abra o projeto
cd javabank

# Build
mvn clean package

# Rode
java -jar target/javabank-0.0.1-SNAPSHOT.jar

# Acesse
http://localhost:8080
```

## Estrutura do Projeto

```
javabank/
├── src/main/java/com/javabank/javabank/
│   ├── controller/          # REST endpoints
│   ├── service/             # Lógica de negócio
│   ├── repository/          # Acesso a dados (JPA)
│   ├── model/               # Entidades (@Entity)
│   ├── config/              # Configurações (CORS, etc)
│   └── JavabankApplication  # Entry point
├── src/main/resources/
│   ├── static/              # HTML/JS/CSS (index.html)
│   └── application.properties
├── pom.xml                  # Dependências Maven
└── docs/                    # Esta documentação
```

## Stack Tecnológico

- **Framework:** Spring Boot 4.1.0
- **Java:** 21
- **Database:** H2 (dev), PostgreSQL (produção)
- **Build:** Maven
- **API:** REST com CORS habilitado

---

Veja a documentação específica de cada quadrante acima pra aprender, fazer tarefas, ou consultar referências.
