@echo off
echo ===============================
echo CONFIGURAÇÃO SUPABASE PASSWORD
echo ===============================
echo.
echo Configuração do Supabase detectada:
echo Host: db.hmjldrzvmaqgetjcepay.supabase.co
echo Port: 5432
echo Database: postgres
echo User: postgres
echo Password: CkTMz5oUISI5gIUn
echo.
echo Executando aplicação com Supabase...
echo URL: jdbc:postgresql://db.hmjldrzvmaqgetjcepay.supabase.co:5432/postgres?sslmode=require
echo.

.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=supabase -DSUPABASE_PASSWORD="CkTMz5oUISI5gIUn"

echo.
echo ===============================
echo   APLICAÇÃO INICIADA COM SUCESSO!
echo ===============================
echo.
echo 🌐 URLs da aplicação:
echo   • API: http://localhost:8090
echo   • Swagger UI: http://localhost:8090/swagger-ui.html
echo   • API Docs: http://localhost:8090/v3/api-docs
echo.
echo 🔑 Sistema de usuários com código automático:
echo   • Códigos de acesso gerados automaticamente (6 caracteres)
echo   • Login case-insensitive (ABC123 = abc123)
echo   • Todos os endpoints CRUD disponíveis
echo   • CORS configurado para desenvolvimento (localhost/127.0.0.1)
echo.
echo 📋 Para testar a API:
echo   1. Acesse o Swagger UI no navegador
echo   2. Crie um usuário (POST /api/users) - código será gerado
echo   3. Use o código gerado para fazer login
echo   4. Teste os outros endpoints CRUD
echo.
echo Pressione qualquer tecla para finalizar...

pause