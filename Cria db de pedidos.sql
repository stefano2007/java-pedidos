CREATE DATABASE pedidosDB
GO
use pedidosDB
GO
CREATE TABLE USUARIOS (
    id    BIGINT IDENTITY PRIMARY KEY,
    nome  VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_criacao DATETIME2 DEFAULT GETDATE(),
    ativo BIT DEFAULT 1
);
GO
CREATE TABLE PRODUTOS (
    id        BIGINT IDENTITY PRIMARY KEY,
    nome      VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    preco     DECIMAL(18,2) NOT NULL,
    ativo     BIT NOT NULL DEFAULT 1,
    data_criacao DATETIME2 DEFAULT GETDATE()
);
GO
CREATE TABLE PEDIDOS(
  id BIGINT IDENTITY PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  data_criacao DATETIME2 DEFAULT GETDATE(),
  [status] varchar(60) DEFAULT 'CRIADO',
  motivo_cancelamento varchar(255)

  CONSTRAINT FK_PEDIDO_USUARIO
        FOREIGN KEY (usuario_id)
        REFERENCES USUARIOS(id)
)
GO
CREATE TABLE PEDIDO_ITENS (
    id                  BIGINT IDENTITY PRIMARY KEY,
    pedido_id           BIGINT NOT NULL,
    produto_id          BIGINT,
    quantidade          INT NOT NULL,
    preco_unitario      DECIMAL(18,2) NOT NULL,
    quantidade_atendida INT,
    status_item         varchar(60) DEFAULT 'CRIADO',
    motivo_cancelamento varchar(255),

    CONSTRAINT FK_PEDIDO_ITENS_PEDIDO
        FOREIGN KEY (pedido_id)
        REFERENCES PEDIDOS(id),
        --ON DELETE CASCADE

    CONSTRAINT FK_PEDIDO_ITENS_PRODUTO
        FOREIGN KEY (produto_id)
        REFERENCES PRODUTOS(id)
);
GO
CREATE TABLE PRODUTOS_ESTOQUE (
    id                     BIGINT IDENTITY PRIMARY KEY,
    produto_id             BIGINT NOT NULL,
    quantidade             INT NOT NULL,
    quantidade_estoque     INT,
    usuario_id             BIGINT NOT NULL,
    status_estoque         varchar(60) DEFAULT 'CRIADO',
    data_criacao           DATETIME2 DEFAULT GETDATE(),
    usuario_conferencia_id BIGINT,
    tipo                   varchar(60) NOT NULL DEFAULT 'ENTRADA'

    CONSTRAINT FK_PRODUTOS_ESTOQUE_PRODUTO
        FOREIGN KEY (produto_id)
        REFERENCES PRODUTOS(id),
    CONSTRAINT FK_PRODUTOS_ESTOQUE_USUARIO
        FOREIGN KEY (usuario_id)
        REFERENCES USUARIOS(id),
    CONSTRAINT FK_PRODUTOS_ESTOQUE_USUARIO_CONFERENCIA
        FOREIGN KEY (usuario_conferencia_id)
        REFERENCES USUARIOS(id)
);
GO
CREATE TABLE PEDIDO_STATUS (
    id BIGINT IDENTITY PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    status    VARCHAR(60) NOT NULL,
    data_criacao DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT fk_pedido_status_pedidos
        FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id)
);
go 
CREATE VIEW VW_PRODUTO_ESTOQUE_ATUAL AS 
SELECT
   P.id as produto_id,
   p.nome as nome,
   SUM(PE.quantidade_estoque) as quantidade_estoque
   FROM PRODUTOS AS P
   INNER JOIN (
   -- (+) Entradas
   SELECT
        PPE.produto_id,
        ISNULL(PPE.quantidade_estoque, 0) AS quantidade_estoque
      FROM PRODUTOS_ESTOQUE AS PPE
      WHERE PPE.status_estoque = 'CONFERIDO'
        AND PPE.tipo = 'ENTRADA'

    UNION ALL

   -- (-) Saidas
    SELECT
        PPE.produto_id,
        ISNULL(PPE.quantidade_estoque, 0) * -1 AS quantidade_estoque
      from PRODUTOS_ESTOQUE AS PPE
      WHERE PPE.status_estoque = 'CONFERIDO'
        AND PPE.tipo = 'SAIDA'

    UNION ALL

    -- (-) Pedidos Estoque Reservados
    SELECT
        PPI.produto_id,
        ISNULL(PPI.quantidade_atendida, 0) * -1 AS quantidade_estoque
      FROM PEDIDO_ITENS AS PPI
      INNER JOIN PEDIDOS AS PP ON PP.id = PPI.pedido_id
                              AND PP.status in ('RESERVADO_ESTOQUE' , 'EM_SEPARACAO', 'SEPARADO', 'EM_TRANSPORTE', 'ENTREGUE') --TODO: pensa em cria nova tabela de pedidos_checkout ou adicionar uma saida em PRODUTOS_ESTOQUE
      WHERE PPI.status_item  = 'RESERVADO_ESTOQUE'

   ) AS PE ON PE.produto_id = P.id

   WHERE P.ativo = 1
   GROUP BY P.id, p.nome

GO

SELECT * FROM VW_PRODUTO_ESTOQUE_ATUAL