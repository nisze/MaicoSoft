#  Maiconsoft API - Configuração H2 Database

##  Banco H2 Configurado com Sucesso!

A aplicação agora está configurada para rodar com **banco de dados H2 em memória**, ideal para demonstrações e testes.

---

##  O que foi alterado?

1.  **H2 Database** adicionado ao projeto (em memória)
2.  **Dados iniciais** serão criados automaticamente
3.  **Console H2** habilitado para visualização dos dados
4.  **Configuração SQL Server** preservada como perfil alternativo

---

##  Como Executar

### Opção 1: Com H2 (Padrão - Para Demonstração)
`ash
.\mvnw.cmd spring-boot:run
`

### Opção 2: Com SQL Server
`ash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=sqlserver
`

---

##  Usuários Pré-cadastrados

### Administrador
- **Código:** ADM001
- **Email:** admin@maiconsoft.com
- **Senha:** admin123

### Diretor
- **Código:** DIR001
- **Email:** diretor@maiconsoft.com
- **Senha:** diretor123

### Funcionário
- **Código:** FUN001
- **Email:** funcionario@maiconsoft.com
- **Senha:** func123

---

##  Console H2

Após iniciar a aplicação, acesse o console H2 para visualizar os dados:

**URL:** http://localhost:8080/h2-console

**Configurações de Conexão:**
- **JDBC URL:** jdbc:h2:mem:maiconsoft_db
- **Username:** sa
- **Password:** (deixe em branco)

---

##  Swagger UI

A documentação da API está disponível em:

**URL:** http://localhost:8080/swagger-ui.html

---

##  Dados de Teste Incluídos

-  3 Usuários (Admin, Diretor, Funcionário)
-  2 Clientes de exemplo
-  2 Cupons de desconto
-  3 Roles (Perfis de usuário)

---

##  Importante

- Os dados são **temporários** e serão perdidos ao reiniciar a aplicação
- Ideal para **demonstrações** e **testes rápidos**
- Para produção, use o perfil **sqlserver** com banco persistente

---

##  Voltando ao SQL Server

Para voltar a usar o SQL Server, edite o arquivo pplication.properties ou execute:

`ash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=sqlserver
`

---

##  Notas

- A aplicação criará automaticamente todas as tabelas necessárias
- Os dados serão populados via data.sql na inicialização
- O console H2 permite executar queries SQL diretamente
