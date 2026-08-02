# Tutorial: Começando com JavaBank

Neste tutorial você vai instalar o JavaBank, iniciá-lo e criar sua primeira conta bancária via interface web. Ao final, você terá um sistema funcional de gerenciamento de contas rodando localmente.

**Tempo estimado:** 5 minutos

## O que você vai precisar

- Java 21+ instalado (`java -version`)
- Maven 3.8+ instalado (`mvn -v`)
- Git (opcional, para clonar o repositório)
- Um navegador web (Chrome, Firefox, Safari, Edge)

## Passo 1: Prepare o ambiente

Abra seu terminal e navegue até a pasta do projeto:

```bash
cd c:\Users\User\Desktop\ESTUDOS - PROGRAMACAO\JAVA\.metadata\javabank\javabank
```

Verifique que você tem Java 21:

```bash
java -version
```

Esperado: `openjdk version "21.0.X"` ou similar.

## Passo 2: Compile o projeto

Execute o build completo:

```bash
mvn clean package
```

Isso vai:
1. Limpar builds anteriores
2. Baixar dependências (primeira vez leva mais tempo)
3. Compilar o código
4. Gerar o arquivo JAR

Esperado: termina com `BUILD SUCCESS` e cria `target/javabank-0.0.1-SNAPSHOT.jar`.

## Passo 3: Inicie o servidor

Execute:

```bash
java -jar target/javabank-0.0.1-SNAPSHOT.jar
```

Esperado: após alguns segundos, você vê:

```
o.s.b.w.e.tomcat.TomcatWebServer      : Tomcat started on port 8080
c.javabank.javabank.JavabankApplication : Started JavabankApplication in X.XXX seconds
```

**O servidor está rodando.** Deixe esse terminal aberto.

## Passo 4: Acesse a interface web

Abra seu navegador e visite:

```
http://localhost:8080
```

Você vai ver uma página com:
- Um formulário "Criar Nova Conta" (nome + número da conta)
- Uma seção "Minhas Contas" (vazia no início)

## Passo 5: Crie sua primeira conta

No formulário:
1. **Nome do Titular:** Digite seu nome, ex. `João Silva`
2. **Número da Conta:** Digite um número, ex. `123456`
3. Clique no botão **"Criar Conta"**

Esperado: uma mensagem verde "✓ Conta criada com sucesso!" e a conta aparece na seção "Minhas Contas" com:
- **Titular:** João Silva
- **Conta:** 123456
- **Saldo:** R$ 0.00
- Dois botões: **Depositar** e **Sacar**

## Passo 6: Faça um depósito

Clique no botão **"💰 Depositar"** da sua conta.

Uma janela de prompt vai pedir quanto depositar. Digite `100` (sem R$).

Esperado:
- Mensagem: "✓ Depósito de R$ 100.00 realizado!"
- Saldo muda pra **R$ 100.00**

## Passo 7: Faça um saque

Clique no botão **"💸 Sacar"** da mesma conta.

Digite `25`.

Esperado:
- Mensagem: "✓ Saque de R$ 25.00 realizado!"
- Saldo muda pra **R$ 75.00** (100 - 25)

## O que você construiu

Parabéns! Você agora tem um sistema bancário funcional rodando localmente com:

✅ **Contas:** criar e listar contas com titular e número  
✅ **Transações:** depositar e sacar dinheiro com validação de saldo  
✅ **Persistência:** dados salvos em banco de dados (H2 em memória durante dev)  
✅ **Interface:** web amigável com feedback em tempo real  

### Próximos passos

- Leia [Como fazer depósitos e saques](./howto-deposit-withdraw.md) pra entender validações
- Consulte [API REST Reference](./reference-api.md) pra usar via curl/Postman
- Explore [Arquitetura em Camadas](./explanation-architecture.md) pra entender o design interno

### Parando o servidor

Volte ao terminal onde o servidor está rodando e pressione `Ctrl+C`.

---

**Sucesso!** Você completou o primeiro ciclo do JavaBank. Agora você pode:
- Criar múltiplas contas
- Testar limites de saque (saque maior que saldo é rejeitado)
- Experimentar via API (veja [reference-api.md](./reference-api.md))
