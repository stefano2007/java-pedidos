# language: pt

Funcionalidade: Cadastro de usuário

  Como um cliente da API
  Quero cadastrar um usuário
  Para que ele possa ser persistido no sistema

  Cenário: Criar usuário com sucesso Teste
    Dado que quero criar um usuario nome "Teste Usuario" com email "criarUsuario@email.com" e senha "Te@123456"
    Quando envio uma requisição para criar o usuário
    Então o status da resposta deve ser 200
    E o usuário deve ser criado com sucesso com nome "Teste Usuario" e email "criarUsuario@email.com" e ativo

  Cenário: Realizar login com sucesso
    Dado que existe um usuário com nome "TesteUsuario", email "testeUsuario@email.com" e senha "Te@123456"
    Quando envio uma requisição para realizar login
    Então o status da resposta deve ser 200
    E o login deve ser realizado com sucesso

  ## Erros

  Cenário: Criar usuário com email já existente
    Dado que existe um usuário com nome "TesteUsuario", email "testeDuplicado@email.com" e senha "Te@123456"
    E que quero criar um usuario nome "Teste" com email "testeDuplicado@email.com" e senha "Te@123456"
    Quando envio uma requisição para criar o usuário
    Então o status da resposta deve ser 409
    E o response com path "/usuarios", status 409 e mensagem "Usuário já existe" devem ser retornados

  Cenário: Email invalido
    Dado que quero criar um usuario nome "Teste" com email "emailInvalido.com" e senha "Te@123456"
    Quando envio uma requisição para criar o usuário
    Então o status da resposta deve ser 400
    E o response com path "/usuarios", status 400 e mensagem "email: Email inválido" devem ser retornados

  Cenário: Senha invalido
    Dado que quero criar um usuario nome "Teste" com email "testeUsuario@email.com" e senha "123456"
    Quando envio uma requisição para criar o usuário
    Então o status da resposta deve ser 400
    E o response com path "/usuarios", status 400 e mensagem "senha: A senha deve conter no mínimo 8 caracteres, 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial" devem ser retornados

  Cenário: Senha de confirmação diferente da senha informada
    Dado que quero criar um usuario nome "Teste" com email "testeUsuario@email.com", senha "Te@123456" e senha de confirmação "te@123456"
    Quando envio uma requisição para criar o usuário
    Então o status da resposta deve ser 400
    E o response com path "/usuarios", status 400 e mensagem "Senha de confirmação diferente da senha informada" devem ser retornados


  # Cenário: Obter usuario por ID

  Cenário: Obter usuario por ID
    Dado que existe um usuário com nome "TesteUsuario", email "testeUsuario@email.com" e senha "Te@123456"
    E envio uma requisição para realizar login
    Quando envio uma requisição para obter o usuário ID "1"
    Então o status da resposta deve ser 200
    E o usuário deve ser retornado

  ## Erros
  Cenário: Quando não existe um usuário com o ID solicitado
    Dado que existe um usuário com nome "TesteUsuario", email "testeUsuario@email.com" e senha "Te@123456"
    E envio uma requisição para realizar login
    Quando envio uma requisição para obter o usuário ID "999"
    Então o status da resposta deve ser 404
    E o response com path "/usuarios/999", status 404 e mensagem "Usuário não encontrado: 999" devem ser retornados