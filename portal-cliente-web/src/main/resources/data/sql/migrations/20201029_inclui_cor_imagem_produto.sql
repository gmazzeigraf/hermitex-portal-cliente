ALTER TABLE tb_produto_imagem ADD COLUMN cd_cor character varying(3);

ALTER TABLE tb_produto_imagem ADD CONSTRAINT fk_produto_imagem_cor FOREIGN KEY (cd_cor)
REFERENCES public.tb_cor_produto (codigo) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION;