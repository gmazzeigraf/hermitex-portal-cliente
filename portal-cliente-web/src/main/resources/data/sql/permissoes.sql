-- Remove a FK
ALTER TABLE tb_perfil_usuario_permissao_acesso DROP CONSTRAINT fk_permissao_acesso_perfil_usuario;

-- Limpa a tabela
TRUNCATE tb_permissao_acesso;

-- Administracao
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ADM_ACESSO_USUARIO', 'Administração / Acesso / Usuário', 'A');
INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ADM_ACESSO_PERFIL', 'Administração / Acesso / Perfil', 'A');

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_CLI_CADASTRO', 'Cliente / Cadastro', 'A');


-- Cria  FK
ALTER TABLE tb_perfil_usuario_permissao_acesso ADD CONSTRAINT fk_permissao_acesso_perfil_usuario
	FOREIGN KEY (cd_permissao) REFERENCES tb_permissao_acesso (codigo)
	ON UPDATE NO ACTION ON DELETE NO ACTION;


-- Carga
INSERT INTO tb_perfil_usuario (id, nome, descricao, tp_usuario, status, versao) VALUES (NEXTVAL('sq_perfil_usuario'), 'Administrador', '', 'A', 'A', 0);
INSERT INTO tb_perfil_usuario_administrador (id) VALUES ((SELECT MAX(id) FROM tb_perfil_usuario));
INSERT INTO tb_perfil_usuario_permissao_acesso (id_perfil, cd_permissao) SELECT (SELECT MIN(id) FROM tb_perfil_usuario), codigo FROM tb_permissao_acesso;

INSERT INTO tb_usuario (id, email, nome, telefone, senha, id_perfil, tipo, status, status_senha, versao) VALUES 
(NEXTVAL('sq_usuario'), 'ggraf@graflogic.com.br', 'Guilherme Graf', '11947789236', 'A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhP5eNfIRvQ=', (SELECT MAX(id) FROM tb_perfil_usuario), 'A', 'A', 'D', 0);
INSERT INTO tb_usuario_administrador (id) VALUES ((SELECT MAX(id) FROM tb_usuario));