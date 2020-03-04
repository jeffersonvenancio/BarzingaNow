# Barzinga Now 
Repositório para a solução do BarzingaNow?

Antes de tudo... use python 2.7...

RODE ESTES COMANDOS PARA ATUALIZAR AS LIBS DE SUPORTE DO PYTHON:
 - sudo apt-get install libffi-dev
 - sudo apt-get install libssl-dev
 - sudo apt-get install python-dev
 - pip install setuptools

QUER INSTALAR AS DEPENDENCIAS???
 - pip install -t ./lib -r requirements.txt --upgrade

DESCRIPTOGRAFAR AS CHAVES DO MAILJET

 - Entrar na pasta bash_scripts e rodar ./decrypt.sh  <*raiz do projeto*> e passe a senha da criptografia quando pedir

QUER RODAR EM DEV (NA SUA MAQUINA, Meu CHAPA)!?!?
 - dev_appserver.py ../python

SE FALHAR POR PERMISSÃO???
 - VERIFIQUE A SUA PERMISSÃO PARA ESTE DIRETÓRIO E CORRIJA:
	/home/[SEU_USUARIO_LOGADO_NA_SUA_MAQUINA]/.config/gcloud

QUER FAZER DEPLOY NO GAE???
 - appcfg.py -A barzinganow -V v1 update ../python/ --noauth_local_webserver

SE FALAR QUE VC NÂO TEM PERMISSÃO, APARE SEU OAUTH CACHE:
 - rm ~/.appcfg_oauth2_tokens

 TA RODANDO A APLICAÇÂO NENE? ENTÃO:
 http://localhost:8080/web/main.html
