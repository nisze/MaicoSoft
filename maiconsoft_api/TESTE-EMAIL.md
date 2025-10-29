#  Guia de Teste de Envio de Emails - Maiconsoft API

##  Configuração Atual

O sistema já está configurado para enviar emails através do Gmail SMTP:

- **SMTP Host:** smtp.gmail.com
- **SMTP Port:** 587
- **Email Remetente:** empresamaiconsoft@gmail.com
- **Status:** HABILITADO (app.email.enabled=true)

---

##  Como Testar o Envio de Email

### Opção 1: Testar via Postman ou cURL

#### 1. Iniciar a Aplicação
```bash
.\mvnw.cmd spring-boot:run
```

#### 2. Enviar Email de Teste Simples
```bash
curl -X POST "http://localhost:8080/api/test/email/send?para=SEU_EMAIL@gmail.com" ^
  -H "Content-Type: application/json"
```

#### 3. Testar Notificação de Venda (vai para o diretor)
```bash
curl -X POST "http://localhost:8080/api/test/email/send-venda-notification" ^
  -H "Content-Type: application/json"
```

#### 4. Testar Confirmação de Compra
```bash
curl -X POST "http://localhost:8080/api/test/email/send-confirmacao-compra?emailCliente=SEU_EMAIL@gmail.com" ^
  -H "Content-Type: application/json"
```

---

### Opção 2: Testar via Swagger UI

1. Acesse: **http://localhost:8080/swagger-ui.html**
2. Procure pela seção **"Email Test"**
3. Escolha um dos endpoints:
   - `POST /api/test/email/send` - Email genérico
   - `POST /api/test/email/send-venda-notification` - Notificação de venda
   - `POST /api/test/email/send-confirmacao-compra` - Confirmação de compra
4. Clique em **"Try it out"**
5. Preencha o parâmetro `para` com seu email
6. Clique em **"Execute"**

---

### Opção 3: Criar uma Venda Real

Quando você criar uma venda através da API, o sistema automaticamente enviará:
- Email de notificação para o diretor (`diretor@maiconsoft.com`)

Endpoint:
```bash
POST http://localhost:8080/api/vendas
```

---

##  Notas Importantes

### Sobre o Email do Remetente
- Atualmente usa: `empresamaiconsoft@gmail.com`
- Senha de aplicativo já configurada
- ** IMPORTANTE:** Verifique se esta conta Gmail ainda está ativa

### Configurações no application.properties
```properties
# Email habilitado para envio
app.email.enabled=true

# Email do diretor (recebe notificações de vendas)
app.email.diretor=diretor@maiconsoft.com

# Email remetente
app.email.from=noreply@maiconsoft.com
```

### Para Alterar o Email do Diretor
Edite o arquivo `application.properties`:
```properties
app.email.diretor=seu_email@dominio.com
```

---

##  Troubleshooting

### Email não está sendo enviado?

1. **Verifique os logs** da aplicação para ver erros de SMTP
2. **Confirme** que `app.email.enabled=true`
3. **Verifique** se a senha do Gmail ainda é válida
4. **Teste** a conectividade SMTP:
   ```bash
   telnet smtp.gmail.com 587
   ```

### Como saber se funcionou?

-  Verifique os logs da aplicação: "Email enviado com sucesso"
-  Verifique sua caixa de entrada (e spam!)
-  A API retornará status `200 OK` com mensagem de sucesso

---

##  Teste Rápido

**Passo a Passo:**

1. Inicie a aplicação:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

2. Abra o Swagger: http://localhost:8080/swagger-ui.html

3. Vá até **"Email Test"  POST /api/test/email/send**

4. Clique em **"Try it out"**

5. Cole seu email no campo `para`

6. Clique em **"Execute"**

7. Verifique seu email!

---

##  Template de Teste via PowerShell

```powershell
# Substitua SEU_EMAIL pelo seu email
$seuEmail = "SEU_EMAIL@gmail.com"

# Enviar email de teste
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/test/email/send?para=$seuEmail&assunto=Teste Maiconsoft&mensagem=Este é um teste!" `
  -ContentType "application/json"
```

---

**Pronto para testar!** 
