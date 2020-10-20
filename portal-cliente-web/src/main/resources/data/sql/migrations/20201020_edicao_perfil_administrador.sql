ALTER TABLE tb_perfil_usuario_administrador ADD COLUMN pc_desconto_livre_maximo numeric(4,2) NULL;
UPDATE tb_perfil_usuario_administrador SET pc_desconto_livre_maximo = 0;
ALTER TABLE tb_perfil_usuario_administrador ALTER COLUMN pc_desconto_livre_maximo SET NOT NULL;

ALTER TABLE tb_perfil_usuario_administrador ADD COLUMN pc_desconto_gerencial_maximo numeric(4,2) NULL;
UPDATE tb_perfil_usuario_administrador SET pc_desconto_gerencial_maximo = 0;
ALTER TABLE tb_perfil_usuario_administrador ALTER COLUMN pc_desconto_gerencial_maximo SET NOT NULL;