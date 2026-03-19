# 📦 API Pedidos

API RESTful para gerenciamento de pedidos, produtos, usuários e estoque com autenticação JWT e integração com Apache Kafka.

**Status:** ✅ Em desenvolvimento  
**Versão:** 0.0.1-SNAPSHOT  
**Java:** 21  
**Spring Boot:** 3.5.11

---

## 📑 Índice

- [Arquitetura](#-arquitetura)
- [Autenticação](#-autenticação)
- [Tratamento de Erros](#-tratamento-de-erros)
- [Tecnologias](#-tecnologias)
- [Configuração](#-configuração)
- [Endpoints](#-endpoints)
- [Roles e Permissões](#-roles-e-permissões)
- [Executando](#-executando)

---

## 🏗️ Arquitetura

O projeto segue a arquitetura **Clean Architecture** com separação clara de responsabilidades em camadas:

### Estrutura de Pastas

```
src/main/java/com/stefano/pedidos/
├── endpoints/              # Camada de apresentação (Controllers + DTOs)
│   ├── auth/              # Autenticação (Login, Refresh Token)
│   ├── usuarios/          # Gerenciamento de usuários e roles
│   │   ├── entity/        # Entidades JPA
│   │   ├── repository/    # Acesso ao banco
│   │   ├── service/       # Lógica de negócio
│   │   └── dto/           # Transferência de dados
│   ├── produtos/          # Catálogo de produtos
│   ├── pedidos/           # Processamento de pedidos
│   └── estoques/          # Controle de estoque
│
├── config/                # Configurações da aplicação
│   ├── filter/            # Filtros Spring Security
│   │   ├── JwtFilter      # Validação de JWT
│   │   └── LoggingContextFilter # MDC para logging
│   ├── logging/           # Configuração de logs
│   ├── model/             # Modelos de configuração
│   ├── SeguracaConfig     # Spring Security Config
│   └── KafkaConfig        # Kafka Config
│
├── exception/             # Tratamento centralizado de erros
│   ├── GlobalExceptionHandler # Handler global
│   ├── ErroResponse       # DTO padrão de erro
│   └── RecursoNaoEncontradoException
│
├── kafka/                 # Integração com Apache Kafka
│   ├── event/            # Eventos de domínio
│   ├── producer/         # Produtores de eventos
│   └── consumer/         # Consumidores de eventos
│
└── util/                  # Classes utilitárias
    └── PedidoConstantes   # Constantes do projeto
```

### Fluxo de uma Requisição HTTP

```
┌─────────────────────────────────────────────────────────────┐
│  1. HTTP Request (GET/POST/PUT/DELETE)                      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  2. LoggingContextFilter                                    │
│     • Gera UUID único da requisição (idRequisicao)          │
│     • Captura IP, método HTTP, caminho                      │
│     • Extrai usuário autenticado                            │
│     • Adiciona tudo ao MDC (Mapped Diagnostic Context)      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  3. JwtFilter                                               │
│     • Extrai token do header Authorization                  │
│     • Valida assinatura do JWT                              │
│     • Carrega usuário e suas roles                          │
│     • Define autenticação no SecurityContext                │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  4. Spring Security Authorization                           │
│     • Valida @PreAuthorize nos endpoints                    │
│     • Bloqueia se usuário não tem role necessária           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  5. Controller                                              │
│     • Recebe a requisição                                   │
│     • Valida DTOs (@Valid)                                  │
│     • Chama o serviço correspondente                        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  6. Service (Lógica de Negócio)                             │
│     • Validações de regras de negócio                       │
│     • Operações no banco via Repository                     │
│     • Publicação de eventos Kafka                           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  7. Repository (Acesso a Dados)                             │
│     • Consultas ao banco de dados (SQL Server)              │
│     • Retorna entidades JPA                                 │
└─────────────────────────────────────────────────────────────┘
                            ↓
        ┌───────────────────┴────────────────────┐
        ↓                                         ↓
   ✅ Sucesso              ❌ Exceção não tratada
        ↓                                         ↓
   Return DTO          GlobalExceptionHandler
        ↓                                         ↓
   200 OK              400/404/409/500
        ↓                                         ↓
        └───────────────────┬────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  8. Response                                                │
│     • JSON com dados ou erro                                │
│     • HTTP Status Code apropriado                           │
│     • Headers (Content-Type, etc)                           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  9. Logging                                                 │
│     • MDC limpo automaticamente                             │
│     • Log estruturado em JSON (Logstash)                    │
│     • Incluindo: timestamp, level, userId, requestId, etc   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔐 Autenticação

A autenticação é baseada em **JWT (JSON Web Token)** com suporte a Refresh Token para renovação automática sem solicitar nova senha.

### 1️⃣ Fluxo de Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "senha": "SuaSenha@123"
}
```

**Resposta (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c3VhcmlvSWQiOjEsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwic3ViIjoidXN1YXJpb0BleGFtcGxlLmNvbSIsImlhdCI6MTcxMDg5MTIzNCwiZXhwIjoxNzEwODkyMTM0fQ.signature...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3VhcmlvQGV4YW1wbGUuY29tIiwiaWF0IjoxNzEwODkxMjM0LCJleHAiOjE3MTE0OTYwMzR9.signature..."
}
```

### 2️⃣ Estrutura do JWT (Access Token)

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload (Claims):**
```json
{
  "usuarioId": 1,                         // ID do usuário no banco
  "roles": ["ROLE_ADMIN", "ROLE_GERENCIADOR"],  // Permissões do usuário
  "sub": "usuario@example.com",           // Email (subject)
  "iat": 1710891234,                      // Issued At (emitido em)
  "exp": 1710892134                       // Expiration (expira em)
}
```

**Validade:**
- `accessToken`: **15 minutos** (para operações da API)
- `refreshToken`: **7 dias** (para renovar o accessToken)

### 3️⃣ Renovar Token

Quando o `accessToken` expirar, use o `refreshToken` para obter um novo:

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Resposta:**
```json
{
  "accessToken": "novo-token...",
  "refreshToken": "novo-refresh-token..."
}
```

### 4️⃣ Usando o Token

Adicione o token no header `Authorization` de cada requisição:

```http
GET /pedidos
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

---

## ⚠️ Tratamento de Erros

Todos os erros são tratados centralizadamente via `@RestControllerAdvice` e retornam um padrão consistente de resposta.

### Resposta Padrão de Erro

```json
{
  "timestamp": "2026-03-19T10:30:45.123",
  "status": 400,
  "erro": "Descrição do erro",
  "caminhoRequisicao": "/api/usuarios"
}
```

### Tipos de Erro e Status HTTP

| Exceção | HTTP | Cenário |
|---------|------|---------|
| `MethodArgumentNotValidException` | **400** | Validação de campo falhou (@Valid) |
| `RecursoNaoEncontradoException` | **404** | Recurso não encontrado (ID inválido) |
| `UsuarioJaExisteException` | **409** | Email já cadastrado |
| `SenhaInvalidaException` | **400** | Senha incorreta no login |
| `IllegalStateException` | **409** | Estado de negócio inválido |
| Exception genérica | **500** | Erro não tratado |

### Exemplos de Erros

#### ❌ Erro 400 - Validação

```http
POST /usuarios
Content-Type: application/json

{
  "nome": "",
  "email": "invalido",
  "senha": "123"
}
```

**Resposta:**
```json
{
  "timestamp": "2026-03-19T10:30:45.123",
  "status": 400,
  "erro": "Nome é obrigatório",
  "caminhoRequisicao": "/usuarios"
}
```

#### ❌ Erro 404 - Não Encontrado

```http
GET /pedidos/999
```

**Resposta:**
```json
{
  "timestamp": "2026-03-19T10:30:45.123",
  "status": 404,
  "erro": "Pedido não encontrado com ID: 999",
  "caminhoRequisicao": "/pedidos/999"
}
```

### 📊 Logging Estruturado

Todos os erros são registrados com contexto completo em formato JSON:

```json
{
  "@timestamp": "2026-03-19T15:12:02.901076-03:00",
  "@version": "1",
  "message": "Pedido retornado com sucesso: 4",
  "logger_name": "com.stefano.pedidos.endpoints.pedidos.service.PedidoService",
  "thread_name": "http-nio-9080-exec-2",
  "level": "INFO",
  "level_value": 20000,
  "correlationId": "5489c315-0c60-4b24-b9a0-482936b6150a",
  "personId": "1",
  "sessionId": "95b15248-30df-4e22-b2f8-3b4cec182512"
}
```

---

## 🛠️ Tecnologias

### Backend
- **Java 21** - Linguagem principal
- **Spring Boot 3.5.11** - Framework web
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - ORM e persistência
- **Hibernate** - Mapeamento objeto-relacional

### Banco de Dados
- **SQL Server 2022** - SGBD principal
- **H2** - Banco em memória para testes

### Segurança & Token
- **JWT (JJWT 0.11.5)** - JSON Web Tokens
- **BCrypt** - Hashing de senhas

### Mensageria
- **Apache Kafka** - Event streaming
- **Spring Kafka** - Integração com Kafka

### Logging
- **Logback** - Framework de logging
- **Logstash Logback Encoder 7.4** - Logging estruturado em JSON

### Desenvolvimento
- **Maven** - Gerenciador de dependências

---

## ⚙️ Configuração

### 1. Variáveis de Ambiente

Edite `src/main/resources/application.properties`:

```properties
# Aplicação
spring.application.name=pedidos
server.port=9080

# Banco de Dados
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=pedidosDB;TrustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=SuaSenhaForte@123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=pedidos-group

# Segurança JWT
seguraca.jwt.secret=60fef54d-5504-4179-9d3c-68c3627eea3a

# Logging
logging.format=json
logging.level.root=INFO
logging.level.com.stefano.pedidos=DEBUG
```

### 2. Inicializar Banco de Dados

Execute o arquivo SQL incluído no projeto:

```bash
sqlcmd -S localhost -U sa -P "SuaSenhaForte@123" -i "Cria db de pedidos.sql"
```

Isso criará todas as tabelas necessárias e inserirá as roles padrão.

---

## 📡 Endpoints

### 🔑 Autenticação

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `POST` | `/auth/login` | Login e obter tokens JWT | ❌ Não requerida |
| `POST` | `/auth/refresh` | Renovar access token | ❌ Não requerida |

### 👥 Usuários

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `POST` | `/usuarios` | Criar novo usuário | ❌ Não requerida |
| `GET` | `/usuarios` | Listar usuários (paginado) | ✅ Requerida |
| `GET` | `/usuarios/{usuarioId}` | Obter detalhes do usuário | ✅ Requerida |

### 📦 Produtos

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `GET` | `/produtos` | Listar produtos (paginado) | ✅ Requerida |
| `GET` | `/produtos/{produtoId}` | Obter detalhes do produto | ✅ Requerida |
| `POST` | `/produtos` | Criar novo produto | ✅ Requerida |
| `PATCH` | `/produtos/{produtoId}/inativar` | Inativar produto | ✅ Requerida |

### 🛒 Pedidos

| Método | Endpoint | Descrição | Autenticação | Restrição |
|--------|----------|-----------|--------------|-----------|
| `GET` | `/pedidos` | Listar pedidos (paginado) | ✅ Requerida | - |
| `GET` | `/pedidos/{pedidoId}` | Obter detalhes do pedido | ✅ Requerida | - |
| `POST` | `/pedidos` | Criar novo pedido | ✅ Requerida | - |
| `POST` | `/pedidos/{pedidoId}/validar` | Validar pedido | ✅ Requerida | - |
| `POST` | `/pedidos/{pedidoId}/cancelar` | Cancelar pedido | ✅ Requerida | **ROLE_GERENCIADOR** |

### 📊 Estoque

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `GET` | `/estoques` | Listar estoque (paginado) | ✅ Requerida |
| `POST` | `/estoques` | Criar entrada de estoque | ✅ Requerida |
| `PUT` | `/estoques/conferencia` | Atualizar/Conferir estoque | ✅ Requerida |
| `GET` | `/estoques/{produtoId}/produto` | Obter estoque por produto | ✅ Requerida |
| `GET` | `/estoques/saldo-atual` | Obter saldo atual de todos produtos | ✅ Requerida |
| `GET` | `/estoques/{produtoId}/produto-saldo-atual` | Obter saldo atual de um produto | ✅ Requerida |

---

## 🔒 Roles e Permissões

O sistema possui **3 roles** principais:

| Role | Descrição | Permissões |
|------|-----------|-----------|
| **ROLE_ADMIN** | Administrador do sistema | Acesso total - sem restrições especiais |
| **ROLE_GERENCIADOR** | Gerenciador de pedidos e estoque | Cancelar pedidos, gerenciar estoque |
| **ROLE_USER** | Usuário comum | Visualizar e criar pedidos e produtos |

### Controle de Acesso por Endpoint

Atualmente, apenas **1 endpoint** possui proteção de role:

```java
@PostMapping("{pedidoId}/cancelar")
@PreAuthorize("hasRole('ROLE_GERENCIADOR')")
public ResponseEntity<PedidoResponse> cancelarPedido(...) { }
```

**Todos os outros endpoints** requerem autenticação, mas não validam roles específicas (qualquer usuário autenticado pode acessar).

### Como Adicionar Proteção por Role em Novos Endpoints

```java
// Exemplo: apenas ADMIN e GERENCIADOR podem deletar
@DeleteMapping("{id}")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_GERENCIADOR')")
public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
    // ...
}
```

---

## 🚀 Executando

### 1. Pré-requisitos
- Java 21+
- Maven 3.8+
- SQL Server 2019+
- Apache Kafka (opcional, para eventos assíncrono)

### 2. Build do Projeto

```bash
# Fazer download de dependências
mvn clean install

# Compilar
mvn clean package

# Pular testes (opcional)
mvn clean package -DskipTests
```

### 3. Executar a Aplicação

```bash
# Via Maven
mvn spring-boot:run

# Via JAR
java -jar target/pedidos-0.0.1-SNAPSHOT.jar

# Com variáveis de ambiente
java -Dspring.datasource.password=SuaSenha -jar target/pedidos-0.0.1-SNAPSHOT.jar
```

A aplicação será iniciada em: **http://localhost:9080**

### 4. Verificar Saúde (se implementado)

```bash
curl -X GET http://localhost:9080/actuator/health
```

---

## 📝 Notas Importantes

✅ **Autenticação**
- Todo endpoint (exceto `/auth/login` e `/usuarios`) requer um token JWT válido no header `Authorization`
- Tokens expiram em **15 minutos**
- Use o refresh token para obter novos tokens sem fazer login novamente

✅ **Validações**
- Emails devem ser únicos
- Senhas mínimo 8 caracteres e devem conter: maiúscula, minúscula, número e caractere especial
- Campos obrigatórios são sempre validados via `@Valid`

✅ **Segurança**
- Senhas são salvas com hash BCrypt
- JWTs assinados com HS256
- CORS desabilitado (configurável em SeguracaConfig)
- SQL Injection prevenido via JPA Parameterized Queries

✅ **Performance**
- Logging assíncrono via AsyncAppender
- Conexões pooladas no banco
- Paginação em endpoints de listagem (padrão: 20 itens)

✅ **Observabilidade**
- Todos os logs em formato JSON estruturado
- MDC com ID único da requisição
- Rastreamento de IP, usuário e endpoint de cada requisição
- LoggingContextFilter adiciona contexto automaticamente

---

## 📚 Estrutura de Dados

### Tabelas Principais

#### USUARIOS
```sql
CREATE TABLE USUARIOS (
    id BIGINT IDENTITY PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_criacao DATETIME2 DEFAULT GETDATE(),
    ativo BIT DEFAULT 1
);
```

#### ROLES
```sql
CREATE TABLE ROLES (
    id BIGINT IDENTITY PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    ativo BIT NOT NULL DEFAULT 1
);
```

#### USUARIO_ROLES (Relacionamento M-N)
```sql
CREATE TABLE USUARIO_ROLES (
    usuario_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    FOREIGN KEY (usuario_id) REFERENCES USUARIOS(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES ROLES(id) ON DELETE CASCADE
);
```

#### PRODUTOS
```sql
CREATE TABLE PRODUTOS (
    id BIGINT IDENTITY PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    preco DECIMAL(18,2) NOT NULL,
    ativo BIT NOT NULL DEFAULT 1,
    data_criacao DATETIME2 DEFAULT GETDATE()
);
```

#### PEDIDOS
```sql
CREATE TABLE PEDIDOS(
    id BIGINT IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    data_criacao DATETIME2 DEFAULT GETDATE(),
    status VARCHAR(60) DEFAULT 'CRIADO',
    motivo_cancelamento VARCHAR(255),
    CONSTRAINT FK_PEDIDO_USUARIO FOREIGN KEY (usuario_id) REFERENCES USUARIOS(id)
);
```

---

## 🎯 Próximas Melhorias Sugeridas

- [ ] Adicionar `@PreAuthorize` em mais endpoints para melhor controle de acesso
- [ ] Implementar refresh automático de tokens
- [ ] Adicionar auditoria (quem criou/modificou cada registro)
- [ ] Implementar soft delete em entidades
- [ ] Adicionar cache de produtos (Redis)
- [ ] Implementar rate limiting

---

**Desenvolvido com ❤️ por Stefano**  
Última atualização: 19/03/2026

