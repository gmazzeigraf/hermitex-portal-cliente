ALTER TABLE tb_produto ADD COLUMN sku character varying(50);
CREATE INDEX "tb_produto_sku_Idx" ON public.tb_produto USING btree (sku ASC NULLS LAST) TABLESPACE pg_default;