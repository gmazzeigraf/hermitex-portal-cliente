CREATE TABLE public.tb_cor_produto
(
    codigo character varying(3) COLLATE pg_catalog."default" NOT NULL,
    nome character varying(50) COLLATE pg_catalog."default" NOT NULL,
    ordem smallint NOT NULL,
    status character(1) COLLATE pg_catalog."default" NOT NULL,
    versao bigint NOT NULL,
    CONSTRAINT pktb_cor_produto PRIMARY KEY (codigo)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tb_cor_produto OWNER to postgres;

CREATE TABLE public.tb_cor_produto_aud
(
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    cd_relacionado character varying(3) COLLATE pg_catalog."default" NOT NULL,
    data timestamp without time zone NOT NULL,
    id_responsavel integer,
    cd_evento character(1) COLLATE pg_catalog."default" NOT NULL,
    observacao text COLLATE pg_catalog."default",
    objeto text COLLATE pg_catalog."default",
    CONSTRAINT pktb_cor_produto_aud PRIMARY KEY (id),
    CONSTRAINT fk_cor_produto_aud FOREIGN KEY (cd_relacionado)
        REFERENCES public.tb_cor_produto (codigo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_cor_produto_aud_usuario FOREIGN KEY (id_responsavel)
        REFERENCES public.tb_usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tb_cor_produto_aud  OWNER to postgres;

CREATE INDEX "tb_cor_produto_aud_id_relacionado_Idx"
ON public.tb_cor_produto_aud USING btree (cd_relacionado COLLATE pg_catalog."default" ASC NULLS LAST) TABLESPACE pg_default;

INSERT INTO tb_permissao_acesso (codigo, descricao, tp_usuario) VALUES ('ROLE_ADM_PRODUTO_COR', 'Administração / Produto / Cor', 'A');