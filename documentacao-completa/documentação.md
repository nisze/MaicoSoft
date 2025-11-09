A dor do  cliente é a dificuldade de preencher os campos para a adição de um cliente, com layout confuso de dificil usabilidade
A soluçaõ proposta foi um sistema que engloba um cadastro de cliente, ususario, cupom e vendas. 
Sistema completo com login e senha para os funcionarios entrarem, onde apos o cadastro recebem um codigo de acesso e  a funcionalidade de recuperar senha atraves do email

Equipe de desenvolvimento é  Denise
Dev Full Stack / DBA
Backend, Banco de Dados, Arquitetura

Leandro
QA Specialist
Testes, Qualidade, Validação

Luiz Antonio
Dev Front-end
Interface, UX/UI, Responsividade

 João Vinícius
Líder do Projeto
Gestão, Planejamento, Organização
 
Instalação do wsl via terminal para windows, para evitar uso do docker desktop:
Instalar windows Terminal - https://apps.microsoft.com/detail/9n0dx20hk701?hl=pt-BR&gl=BR
Imagem1

Instalar WSL para habilitar o Linux 
https://learn.microsoft.com/pt-br/windows/wsl/install 
(instalar a versão Ubuntu e definir ela como padrão) 


Após a instalação, é possível acessar o Linux por linha de comando usando o Windows Terminal 
Imagem2
Imagem3
Após abrir o terminal com Ubuntu, é bom já deixar instalado o GIT nele também. 
Basta usar os comandos a seguir dentro do Linux no WSL: 

   (explicar os comandos)

Instalar GIT - Linux 
sudo apt-get update 
sudo apt-get install git-all 
 
Instalar Docker dentro do Linux 
  
Instalação do Docker no Ubuntu 
https://docs.docker.com/engine/install/ubuntu/ 
 
Após terminar de instalar o docker, para poder rodar ele sem usar SUDO, basta executar o comando abaixo: 

sudo usermod -aG docker $USER 
 
Aproveitando que estamos falando de docker, o link abaixo é o repositório do código fonte do livro Docker Para Desenvolvedores, uma referência sobre fundamentos de Docker. 
  
Docker Para Desenvolvedores - Código Fonte 
-- após isso explicar como colocar os containers e executar 

Rodando banco / criar tutorial de comando de como seria rodar por aqui dkorgiski@AVAPC-777081231:~$ touch docker-compose.yml
dkorgiski@AVAPC-777081231:~$ nano docker-compose
dkorgiski@AVAPC-777081231:~$ ls
docker-compose  docker-compose.yml  projetos
dkorgiski@AVAPC-777081231:~$ cat docker-compose
cat: docker-compose: Is a directory
dkorgiski@AVAPC-777081231:~$ cat docker-compose.yml
services:
  postgres:
    image: postgres:15-alpine
    container_name: maiconsoft-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: maiconsoft
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
dkorgiski@AVAPC-777081231:~$ docker compose up -d
[+] Running 12/12
 ✔ postgres Pulled                                                                                    11.3s
   ✔ 2d35ebdb57d9 Pull complete                                                                        1.5s
   ✔ d7cf304fb91a Pull complete                                                                        1.5s
   ✔ 01a8e1e8a6d3 Pull complete                                                                        1.6s
   ✔ c8d6a201b2ea Pull complete                                                                        1.6s
   ✔ 665050181beb Pull complete                                                                        1.6s
   ✔ e7d8b5a29e19 Pull complete                                                                        8.6s
   ✔ b7a5b6d84454 Pull complete                                                                        8.6s
   ✔ 8e7af31e0abd Pull complete                                                                        8.6s
   ✔ 0a297d5cb757 Pull complete                                                                        8.6s
   ✔ 78753f2bcd40 Pull complete                                                                        8.7s
   ✔ 9229d0c336e2 Pull complete                                                                        8.7s
[+] Running 3/3
 ✔ Network dkorgiski_default         Created                                                           0.0s
 ✔ Volume "dkorgiski_postgres_data"  Created                                                           0.0s
 ✔ Container maiconsoft-postgres     Started                                                           0.3s
dkorgiski@AVAPC-777081231:~$ docker ps
CONTAINER ID   IMAGE                                        COMMAND                  CREATED          STATUS                          PORTS                                         NAMES
a59daedd47f1   postgres:15-alpine                           "docker-entrypoint.s…"   20 seconds ago   Up 20 seconds                   0.0.0.0:5432->5432/tcp, [::]:5432->5432/tcp   maiconsoft-postgres
28e44dc765cc   mcr.microsoft.com/mssql/server:2022-latest   "/opt/mssql/bin/laun…"   3 months ago     Restarting (1) 12 seconds ago                                                 decolatrips-backend-sqlserver-1
dkorgiski@AVAPC-777081231:~$ docker compose down
[+] Running 2/2
 ✔ Container maiconsoft-postgres  Removed                                                              0.3s
 ✔ Network dkorgiski_default      Removed                                                              0.3s
dkorgiski@AVAPC-777081231:~$ docker compose up -d
[+] Running 2/2
 ✔ Network dkorgiski_default      Created                                                              0.0s
 ✔ Container maiconsoft-postgres  Started                                                              0.2s
dkorgiski@AVAPC-777081231:~$ docker exec -it maiconsoft-postgres psql -U postgres -d maiconsoft -c "\dt"
                 List of relations
 Schema |         Name          | Type  |  Owner
--------+-----------------------+-------+----------
 public | clientes              | table | postgres
 public | cupom                 | table | postgres
 public | dashboard_log         | table | postgres
 public | flyway_schema_history | table | postgres
 public | pagamentos            | table | postgres
 public | servico               | table | postgres
 public | user_roles            | table | postgres
 public | users                 | table | postgres
 public | vendas                | table | postgres
(9 rows)

dkorgiski@AVAPC-777081231:~$ docker-compose -f docker-compose-postgres.yml down -v
Command 'docker-compose' not found, but can be installed with:
sudo snap install docker          # version 28.4.0, or
sudo snap install docker          # version 28.1.1+1
sudo apt  install docker-compose  # version 1.29.2-6
See 'snap info <snapname>' for additional versions.
dkorgiski@AVAPC-777081231:~$ docker compose down
[+] Running 2/2
 ✔ Container maiconsoft-postgres  Removed                                                              0.3s
 ✔ Network dkorgiski_default      Removed                                                              0.3s
dkorgiski@AVAPC-777081231:~$ docker compose up -d
[+] Running 2/2
 ✔ Network dkorgiski_default      Created                                                              0.0s
 ✔ Container maiconsoft-postgres  Started    


