# app-cofre-senhas
Aplicativo para gestão de credenciais focado em usabilidade e acessibilidade

---

## 🔐 Autenticação com JWT

Esta API utiliza **JWT (JSON Web Token)** para autenticar e autorizar usuários. O fluxo é stateless — nenhuma sessão é mantida no servidor. A cada requisição às rotas protegidas, você deve enviar o token no cabeçalho `Authorization`.

---

### 📋 Visão Geral do Fluxo

```
[ Cliente ]                         [ API ]
    |                                  |
    |── POST /usuarios ──────────────→ |  (1) Cria a conta (público)
    |                                  |
    |── POST /auth/login ────────────→ |  (2) Faz login com e-mail + senha
    |←─ { token, tipo, expiracaoEm } ─ |  (3) Recebe o token JWT
    |                                  |
    |── GET /api/senhas/usuario/1 ───→ |  (4) Acessa rota protegida...
    |   Authorization: Bearer <token>  |      ...com o token no header
    |←─ 200 OK (lista de senhas) ───── |  (5) Resposta autorizada ✅
```

---

### Passo 1 — Criar um Usuário

Registre um usuário na API antes de fazer login.

**Endpoint:** `POST /usuarios`

**Body (JSON):**
```json
{
  "nome": "Eduardo Silva",
  "email": "eduardo@email.com",
  "senha": "minhasenha123",
  "tipoUsuario": "USUARIO_LEIGO"
}
```

> **Tipos disponíveis:** `ADMIN` ou `USUARIO_LEIGO`

**Resposta esperada:** `201 Created`

---

### Passo 2 — Realizar o Login

**Endpoint:** `POST /auth/login`

**Body (JSON):**
```json
{
  "email": "eduardo@email.com",
  "senha": "minhasenha123"
}
```

**Resposta esperada:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlZHVhcmRvQGVtYWlsLmNvbSIsImlhdCI6MTc...",
  "tipo": "Bearer",
  "expiracaoEm": 1756598400000
}
```

> **Copie o valor do campo `token`** — você vai precisar dele no próximo passo.

**Erros possíveis:**
| Status | Motivo |
|--------|--------|
| `401 Unauthorized` | E-mail ou senha inválidos |
| `400 Bad Request`  | Campos obrigatórios ausentes ou inválidos |

---

### Passo 3 — Autenticar as Requisições

Todas as rotas em `/api/senhas/**` exigem autenticação. Passe o token no cabeçalho `Authorization` com o prefixo `Bearer `.

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 🧪 Como testar no Swagger UI

O Swagger UI está disponível em: **`http://localhost:8080/swagger-ui.html`**

### Passo a Passo

**1.** Acesse `http://localhost:8080/swagger-ui.html` no seu navegador.

**2.** Expanda a seção **Autenticação** e execute `POST /auth/login`:
   - Clique em **Try it out**
   - Preencha o body com seu `email` e `senha`
   - Clique em **Execute**
   - Na resposta, **copie o valor do campo `token`**

**3.** Clique no botão **🔒 Authorize** (canto superior direito da página do Swagger).

**4.** Na caixa de diálogo que abrir, cole apenas o token JWT no campo **Value** (o Swagger adiciona `Bearer ` automaticamente):
   ```
   eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlZHVhcmRvQGVtYWlsLmNvbSIsImlhdCI6...
   ```
   > ⚠️ **Não** inclua a palavra "Bearer" — o Swagger UI adiciona isso automaticamente.

**5.** Clique em **Authorize** e depois em **Close**.

**6.** Agora todos os endpoints protegidos exibirão o ícone de cadeado fechado 🔒. Execute qualquer rota de `/api/senhas/**` normalmente — o token será enviado automaticamente.

---

## 🧪 Como testar no Postman

**1.** Siga os Passos 1 e 2 da seção anterior para obter o token JWT.

**2.** Crie uma nova requisição para uma rota protegida (ex.: `GET /api/senhas/usuario/1`).

**3.** Na aba **Authorization** da requisição:
   | Campo | Valor |
   |-------|-------|
   | **Type** | `Bearer Token` |
   | **Token** | Cole apenas o valor do campo `token` (sem "Bearer") |

   **Alternativa manual (aba Headers):**
   | Key | Value |
   |-----|-------|
   | `Authorization` | `Bearer eyJhbGciOiJIUzI1NiJ9...` |

**4.** Envie a requisição — você receberá `200 OK` com os dados.

---

## ⚙️ Configuração da Chave Secreta

A chave JWT e o tempo de expiração são definidos em `application.properties`:

```properties
# Chave secreta Base64 (mínimo 256 bits para HS256)
api.security.token.secret=bXlTdXBlclNlY3JldEtleUZvckpXVEF1dGhlbnRpY2F0aW9u...

# Expiração do token em milissegundos — padrão: 24 horas
api.security.token.expiration-ms=86400000
```

> **⚠️ Em produção:** gere uma chave forte com `openssl rand -base64 64` e **nunca** a versione no Git. Use variáveis de ambiente ou um cofre de segredos.

---

## 🗺️ Mapa de Endpoints

| Método | Rota | Autenticação | Descrição |
|--------|------|:---:|-----------|
| `POST` | `/usuarios` | ❌ Pública | Criar novo usuário |
| `POST` | `/auth/login` | ❌ Pública | Login — retorna JWT |
| `GET`  | `/v3/api-docs` | ❌ Pública | Especificação OpenAPI (JSON) |
| `GET`  | `/swagger-ui.html` | ❌ Pública | Interface Swagger UI |
| `GET`  | `/api/senhas/usuario/{id}` | ✅ Bearer | Listar senhas do usuário |
| `POST` | `/api/senhas/usuario/{id}` | ✅ Bearer | Criar nova senha |
| `GET`  | `/api/senhas/{id}` | ✅ Bearer | Buscar senha por ID |
| `PUT`  | `/api/senhas/{id}` | ✅ Bearer | Atualizar senha |
| `DELETE` | `/api/senhas/{id}` | ✅ Bearer | Deletar senha |
