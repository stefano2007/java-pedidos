# language: pt

Funcionalidade: Login do usuário

  Como um cliente da API
  Quero realizar o login do usuário no sistema

  Cenário: Realizar login com sucesso
    Dado que existe um usuário com nome "TesteLogin", email "testeLogin@email.com" e senha "Tl@123456"
    Quando envio uma requisição para realizar login
    Então o status da resposta deve ser 200
    E o login deve ser realizado com sucesso

  Cenário: Realizar refash token com sucesso
    Dado que existe um usuário com nome "TesteLogin", email "testeLogin@email.com" e senha "Tl@123456"
    E que realizo login e obtenho um refresh token
    Quando envio uma requisição para realizar refresh
    Então o status da resposta deve ser 200

  # Cenário: Realizar login com email inválido
  Cenário: Realizar login com usuario não cadastrado
    Dado que possuo um login e email "xpto@email.com" e senha "Tl@123456"
    Quando envio uma requisição para realizar login
    Então o status da resposta deve ser 403