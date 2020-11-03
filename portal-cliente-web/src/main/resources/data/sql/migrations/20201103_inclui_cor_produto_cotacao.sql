ALTER TABLE tb_cotacao_item ADD COLUMN cd_cor character varying(3);

ALTER TABLE tb_cotacao_item ADD CONSTRAINT fk_cotacao_item_cor FOREIGN KEY (cd_cor)
REFERENCES public.tb_cor_produto (codigo) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION;