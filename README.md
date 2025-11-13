# MAICOSOFT

Sistema de Gestão Empresarial para Construção Civil

![Demo do Sistema](https://github.com/nisze/MaicoSoft/blob/master/Imagens/cut.gif)

---

## Sobre o Projeto

O **MaicoSoft** é um sistema completo de gestão empresarial desenvolvido como projeto acadêmico para atender pequenas e médias empresas do setor de construção civil. 

Oferece controle de clientes (Pessoa Física e Jurídica), gestão de vendas, sistema de cupons de desconto, controle de usuários por perfis de acesso e dashboard analítico com métricas em tempo real.

**Instituição:** Fatec Itapetininga  
**Curso:** Análise e Desenvolvimento de Sistemas - 5º Semestre Noturno  
**Período:** 2º Semestre 2025

---

## Equipe de Desenvolvimento

- **João Vinícius Fonseca Diniz** - Líder do Projeto
- **Denise Korgiski de Oliveira** - Backend & Banco de Dados
- **Luiz Antonio Pereira Lima** - Frontend & UI/UX
- **Leandro Nicolas Silveira Gonçalves** - DevOps & Infraestrutura

---

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- Spring Boot Mail
- Flyway (Migrações de banco)
- BCrypt (Criptografia de senhas)
- Maven

### Frontend
- HTML5
- CSS3
- JavaScript (Vanilla)
- Chart.js (Gráficos)
- Fetch API

### Banco de Dados
- PostgreSQL 16 (Produção)
- H2 Database (Desenvolvimento/Testes)
- Supabase (Hospedagem cloud opcional)

### Infraestrutura
- Docker & Docker Compose
- Nginx (Servidor web)
- WSL2 (Ambiente de desenvolvimento Windows)

---

## Arquitetura

O projeto utiliza **containerização completa** com Docker:

- **Container Backend:** Aplicação Spring Boot (Porta 8090)
- **Container Frontend:** Nginx servindo aplicação web (Porta 3000)
- **Container Banco de Dados:** PostgreSQL (Porta 5432)

Todos os serviços são orquestrados via **Docker Compose**, garantindo fácil deploy e portabilidade.

---

## Instalação e Execução

### Pré-requisitos

- **Docker** instalado e em execução
- **WSL2** (para usuários Windows)
- **Git** para clonar o repositório

### Passo a Passo

1. **Clone o repositório:**

```bash
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft
```

2. **Execute o projeto com Docker Compose:**

```bash
docker-compose up -d
```

3. **Aguarde a inicialização dos containers** (pode levar alguns minutos na primeira vez)

4. **Acesse a aplicação:**

- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8090
- **Swagger UI:** http://localhost:8090/swagger-ui.html

---

## Acesso ao Sistema

### Credenciais de Teste

**Admin Sistema:**
- **Código de Acesso:** ADM001
- **Senha:** 123456

Este usuário possui acesso completo a todas as funcionalidades do sistema.

---

## Funcionalidades Principais

### Autenticação e Segurança
- Login com validação de credenciais
- Senhas criptografadas com BCrypt
- Recuperação de senha via email
- Controle de acesso por perfis (ADMIN, DIRETOR, FUNCIONARIO, VENDEDOR)

### Gestão de Clientes
- Cadastro de Pessoa Física (CPF) e Jurídica (CNPJ)
- Busca automática de endereço via CEP (integração ViaCEP)
- Upload de foto de perfil
- Histórico de vendas por cliente

### Gestão de Vendas
- Registro de vendas com múltiplas formas de pagamento
- Aplicação de cupons de desconto
- Validação automática de cupons (validade e limite de uso)
- Notificações por email

### Sistema de Cupons
- Criação de cupons com código único
- Definição de desconto percentual
- Controle de validade e limite de utilizações
- Incremento automático de uso

### Dashboard Analítico
- Total de vendas realizadas
- Receita total acumulada
- Gráficos de vendas por período
- Top 5 clientes (maiores compradores)
- Atualização em tempo real

---

## Estrutura do Projeto

```
MaicoSoft/
 maiconsoft_api/          # Backend Spring Boot
    src/main/java/       # Código-fonte Java
    src/main/resources/  # Configurações e migrations
    pom.xml              # Dependências Maven
    Dockerfile           # Container backend
 frontend/                # Frontend
    pages/               # Páginas HTML
    js/                  # Scripts JavaScript
    css/                 # Estilos CSS
    assets/              # Imagens e recursos
 docker-compose.yml       # Orquestração containers
 nginx.conf               # Configuração Nginx
 README.md                # Este arquivo
```

---

## Documentação Completa

A documentação técnica completa do projeto (92 páginas) está disponível em PDF:

- **PDF:** [Maicosoft - Documentação Completa do Projeto.pdf](./Maicosoft%20-%20Documentação%20Completa%20do%20Projeto%20f.pdf)

### Conteúdo da Documentação:
1. Equipe de Desenvolvimento
2. Problema e Solução
3. Docker e WSL
4. Banco de Dados (Modelagem ER, Scripts, Entidades)
5. Backend (Arquitetura, APIs REST, Serviços, Controllers)
6. Frontend (Páginas, Componentes, Interações)
7. Requisitos (Funcionais, Não-Funcionais, Casos de Uso, Diagramas UML, Personas)
8. Bibliografia e Referências

---

## Comandos Úteis

### Parar os containers:
```bash
docker-compose down
```

### Ver logs dos containers:
```bash
docker-compose logs -f
```

### Reiniciar um serviço específico:
```bash
docker-compose restart backend
docker-compose restart frontend
docker-compose restart postgres
```

### Reconstruir containers após mudanças:
```bash
docker-compose up -d --build
```

---

## Links Úteis

- **Repositório GitHub:** https://github.com/nisze/MaicoSoft
- **Swagger API (após iniciar):** http://localhost:8090/swagger-ui.html

---

## Licença

Este projeto foi desenvolvido como Trabalho de Conclusão de semestre das matérias Programação WEB e Laboratório de Engenharia de Software.

**Fatec Itapetininga - Análise e Desenvolvimento de Sistemas - 2025**

---

## Contribuições

Desenvolvido pela equipe MaicoSoft.

Para dúvidas ou sugestões, entre em contato através do repositório GitHub.
