#!/bin/bash
echo "==============================="
echo "MAICONSOFT - VERIFICACAO DO SISTEMA"
echo "==============================="
echo

JAVA_OK=0
MAVEN_OK=0
PORT_OK=0
SUPABASE_OK=0
CONFIG_OK=0
STRUCTURE_OK=0
POM_OK=0

echo "[1/7] Verificando Java..."
if command -v java &> /dev/null; then
    echo "✅ Java encontrado"
    java -version 2>&1 | head -1
    JAVA_OK=1
else
    echo "❌ Java NAO encontrado!"
    echo "📥 Baixe Java 17+ em: https://adoptium.net/"
fi
echo

echo "[2/7] Verificando Maven Wrapper..."
if [ -f "mvnw" ]; then
    echo "✅ Maven Wrapper encontrado"
    MAVEN_OK=1
else
    echo "❌ mvnw NAO encontrado!"
    echo "Certifique-se de estar na pasta maiconsoft_api"
fi
echo

echo "[3/7] Verificando porta 8090..."
if lsof -i :8090 &> /dev/null; then
    echo "⚠️  Porta 8090 em uso"
    echo "Processos usando a porta:"
    lsof -i :8090
else
    echo "✅ Porta 8090 livre"
    PORT_OK=1
fi
echo

echo "[4/7] Verificando conectividade Supabase..."
if ping -c 1 db.hmjldrzvmaqgetjcepay.supabase.co &> /dev/null; then
    echo "✅ Supabase acessivel"
    SUPABASE_OK=1
else
    echo "❌ Supabase inacessivel"
    echo "Verifique sua conexao de internet"
fi
echo

echo "[5/7] Verificando arquivos de configuracao..."
if [ -f "src/main/resources/application.properties" ]; then
    echo "✅ application.properties encontrado"
    CONFIG_OK=1
else
    echo "❌ application.properties NAO encontrado!"
fi

if [ -f "src/main/resources/application-supabase.properties" ]; then
    echo "✅ application-supabase.properties encontrado"
else
    echo "❌ application-supabase.properties NAO encontrado!"
    CONFIG_OK=0
fi
echo

echo "[6/7] Verificando estrutura do projeto..."
if [ -d "src/main/java" ]; then
    echo "✅ Pasta src/main/java encontrada"
    STRUCTURE_OK=1
else
    echo "❌ Estrutura do projeto incorreta!"
fi
echo

echo "[7/7] Verificando dependencias..."
if [ -f "pom.xml" ]; then
    echo "✅ pom.xml encontrado"
    POM_OK=1
else
    echo "❌ pom.xml NAO encontrado!"
fi
echo

echo "==============================="
echo "RESUMO DA VERIFICACAO"
echo "==============================="
[ $JAVA_OK -eq 1 ] && echo "✅ Java" || echo "❌ Java"
[ $MAVEN_OK -eq 1 ] && echo "✅ Maven" || echo "❌ Maven"
[ $PORT_OK -eq 1 ] && echo "✅ Porta 8090" || echo "⚠️  Porta 8090"
[ $SUPABASE_OK -eq 1 ] && echo "✅ Supabase" || echo "❌ Supabase"
[ $CONFIG_OK -eq 1 ] && echo "✅ Configuracao" || echo "❌ Configuracao"
[ $STRUCTURE_OK -eq 1 ] && echo "✅ Estrutura" || echo "❌ Estrutura"
[ $POM_OK -eq 1 ] && echo "✅ POM" || echo "❌ POM"
echo

if [ $JAVA_OK -eq 1 ] && [ $MAVEN_OK -eq 1 ] && [ $CONFIG_OK -eq 1 ] && [ $STRUCTURE_OK -eq 1 ] && [ $POM_OK -eq 1 ]; then
    echo "🎉 SISTEMA PRONTO PARA EXECUCAO!"
    echo "Execute: ./run-with-supabase.sh"
else
    echo "🔧 CORRIJA OS PROBLEMAS ACIMA ANTES DE CONTINUAR"
    echo
    echo "Solucoes rapidas:"
    [ $JAVA_OK -eq 0 ] && echo "- Instale Java 17+: https://adoptium.net/"
    [ $MAVEN_OK -eq 0 ] && echo "- Execute na pasta correta: maiconsoft_api/"
    [ $CONFIG_OK -eq 0 ] && echo "- Verifique arquivos de configuracao"
    [ $PORT_OK -eq 0 ] && echo "- Libere porta 8090: lsof -i :8090"
    [ $SUPABASE_OK -eq 0 ] && echo "- Verifique internet ou use modo offline"
fi
echo