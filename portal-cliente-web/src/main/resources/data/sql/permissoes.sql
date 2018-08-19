-- Remove a FK
ALTER TABLE tb_perfil_usuario_permissao_acesso DROP CONSTRAINT fk_perfil_usuario_permissao_acesso_permissao;

-- Limpa a tabela
TRUNCATE tb_perfil_usuario_permissao_acesso;
TRUNCATE tb_permissao_acesso;

-- Administracao
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ADM_ACESSO_PERFIL', 'Administração / Acesso / Perfil', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ADM_ACESSO_USUARIO', 'Administração / Acesso / Usuário', 'A');

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ADM_PRODUTO_TAMANHO', 'Administração / Produto / Tamanho', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ADM_PRODUTO_EMBALAGEM', 'Administração / Produto / Embalagem', 'A');

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_CADASTRO', 'Cliente / Cadastro', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_PRODUTO_CATEGORIA', 'Cliente / Produto / Categoria', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_PRODUTO_SETOR', 'Cliente / Produto / Setor', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_PRODUTO_CADASTRO', 'Cliente / Produto / Cadastro', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_ACESSO_PERFIL', 'Cliente / Acesso / Perfil', 'AC');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_ACESSO_USUARIO', 'Cliente / Acesso / Usuário', 'AC');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_JANELA_COMPRA', 'Cliente / Janela Compra', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_FORMA_PAGAMENTO', 'Cliente / Forma Pagamento', 'A');

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_FIL_CADASTRO', 'Filial / Cadastro', 'AC');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_FIL_CADASTRO_BLOQUEIO_COMPRA', 'Filial / Cadastro - Bloqueio Compra', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_FIL_ACESSO_PERFIL', 'Filial / Acesso / Perfil', 'ACF');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_FIL_ACESSO_USUARIO', 'Filial / Acesso / Usuário', 'ACF');

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_REP_CADASTRO', 'Representante / Cadastro', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_REP_ACESSO_PERFIL', 'Representante / Acesso / Perfil', 'AR');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_REP_ACESSO_USUARIO', 'Representante / Acesso / Usuário', 'AR');

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_PRODUTOS', 'Produtos', 'ACFR');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ESTOQUE', 'Estoque', 'ACR');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ESTOQUE_SOLICITACAO', 'Estoque - Solicitação', 'C');

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_COMPRA', 'Compra', 'CF');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_COMPRA_VISUALIZACAO_ESTOQUE', 'Compra - Visualização Estoque', 'ACFR');

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_PEDIDO_CONSULTA', 'Pedido / Consulta', 'ACFR');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_PEDIDO_SOLICITACAO_TROCA', 'Pedido / Solicitação Troca', 'ACF');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_PEDIDO_SOLICITACAO_TROCA_FINALIZACAO', 'Pedido / Solicitação Troca - Finalização', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_PEDIDO_BAIXA_PAGAMENTO', 'Pedido / Consulta - Baixa Pagamento', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_PEDIDO_ENVIO', 'Pedido / Consulta - Envio', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_PEDIDO_FINALIZACAO', 'Pedido / Consulta - Finalização', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_PEDIDO_CANCELAMENTO', 'Pedido / Consulta - Cancelamento', 'A');

-- Cria  FK
ALTER TABLE tb_perfil_usuario_permissao_acesso ADD CONSTRAINT fk_perfil_usuario_permissao_acesso_permissao
	FOREIGN KEY (cd_permissao) REFERENCES tb_permissao_acesso (codigo)
	ON UPDATE NO ACTION ON DELETE NO ACTION;


-- Carga
INSERT INTO tb_perfil_usuario (id, nome, descricao, tp_usuario, status, versao) VALUES (NEXTVAL('sq_perfil_usuario'), 'Administrador', '', 'A', 'A', 0);
INSERT INTO tb_perfil_usuario_administrador (id) VALUES ((SELECT MIN(id) FROM tb_perfil_usuario));
INSERT INTO tb_perfil_usuario_permissao_acesso (id_perfil, cd_permissao) SELECT (SELECT MIN(id) FROM tb_perfil_usuario), codigo FROM tb_permissao_acesso WHERE tp_usuario LIKE '%A%';

INSERT INTO tb_usuario (id, email, nome, telefone, senha, id_perfil, tipo, status, status_senha, versao) VALUES 
(NEXTVAL('sq_usuario'), 'ggraf@graflogic.com.br', 'Guilherme Graf', '11947789236', 'A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhP5eNfIRvQ=', (SELECT MAX(id) FROM tb_perfil_usuario), 'A', 'A', 'D', 0);
INSERT INTO tb_usuario_administrador (id) VALUES ((SELECT MAX(id) FROM tb_usuario));