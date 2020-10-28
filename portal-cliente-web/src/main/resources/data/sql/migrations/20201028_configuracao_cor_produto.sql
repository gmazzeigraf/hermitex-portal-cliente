CREATE TABLE public.tb_produto_cor
(
    id_produto integer NOT NULL,
    cd_cor character varying(3) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pktb_produto_cor PRIMARY KEY (id_produto, cd_cor),
    CONSTRAINT fk_produto_cor_produto FOREIGN KEY (id_produto)
        REFERENCES public.tb_produto (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_produto_cor_cor_produto FOREIGN KEY (cd_cor)
        REFERENCES public.tb_cor_produto (codigo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tb_produto_cor OWNER to postgres;