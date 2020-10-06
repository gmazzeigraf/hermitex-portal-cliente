-- Table: public.tb_cotacao

CREATE TABLE public.tb_cotacao
(
    id bigint NOT NULL,
    id_cliente integer NOT NULL,
    id_filial integer,
    vl_produtos numeric(10,2) NOT NULL,
    vl_frete numeric(10,2) NOT NULL,
    vl_total numeric(10,2) NOT NULL,
    peso_total numeric(10,3) NOT NULL,
    ds_forma_pagamento character varying(100) COLLATE pg_catalog."default" NOT NULL,
    status character(1) COLLATE pg_catalog."default" NOT NULL,
    versao bigint NOT NULL,
    qt_total_itens smallint NOT NULL,
    vl_desconto_pagamento numeric(10,2) NOT NULL,
	vl_desconto_livre numeric(10,2) NOT NULL,
	vl_desconto_especial numeric(10,2) NOT NULL,
    id_forma_pagamento integer NOT NULL,
    CONSTRAINT pktb_cotacao PRIMARY KEY (id),
    CONSTRAINT fk_cotacao_cliente FOREIGN KEY (id_cliente) REFERENCES public.tb_cliente (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_filial FOREIGN KEY (id_filial) REFERENCES public.tb_filial (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_forma_pagamento FOREIGN KEY (id_forma_pagamento) REFERENCES public.tb_forma_pagamento (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tb_cotacao OWNER to postgres;
CREATE INDEX "tb_cotacao_id_cliente_Idx" ON public.tb_cotacao USING btree (id_cliente ASC NULLS LAST) TABLESPACE pg_default;
CREATE INDEX "tb_cotacao_id_filial_Idx" ON public.tb_cotacao USING btree (id_filial ASC NULLS LAST) TABLESPACE pg_default;

-- Table: public.tb_cotacao_aud

CREATE TABLE public.tb_cotacao_aud
(
    id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    id_relacionado bigint NOT NULL,
    data timestamp without time zone NOT NULL,
    id_responsavel integer,
    cd_evento character(1) COLLATE pg_catalog."default" NOT NULL,
    observacao text COLLATE pg_catalog."default",
    objeto text COLLATE pg_catalog."default",
    CONSTRAINT pktb_cotacao_aud PRIMARY KEY (id),
    CONSTRAINT fk_cotacao_aud FOREIGN KEY (id_relacionado) REFERENCES public.tb_cotacao (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_aud_usuario FOREIGN KEY (id_responsavel) REFERENCES public.tb_usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tb_cotacao_aud OWNER to postgres;
CREATE INDEX "tb_cotacao_aud_id_relacionado_Idx" ON public.tb_cotacao_aud USING btree (id_relacionado ASC NULLS LAST) TABLESPACE pg_default;

-- Table: public.tb_cotacao_endereco

CREATE TABLE public.tb_cotacao_endereco
(
    id_cotacao bigint NOT NULL,
    tipo character(1) COLLATE pg_catalog."default" NOT NULL,
    sg_estado character(2) COLLATE pg_catalog."default" NOT NULL,
    id_municipio integer NOT NULL,
    cep character(8) COLLATE pg_catalog."default" NOT NULL,
    bairro character varying(100) COLLATE pg_catalog."default" NOT NULL,
    logradouro character varying(100) COLLATE pg_catalog."default" NOT NULL,
    numero character varying(10) COLLATE pg_catalog."default" NOT NULL,
    complemento character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT pktb_cotacao_endereco PRIMARY KEY (id_cotacao, tipo),
    CONSTRAINT fk7b8fkhps9kl7u8inhrhw1neba FOREIGN KEY (id_cotacao) REFERENCES public.tb_cotacao (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_endereco_estado FOREIGN KEY (sg_estado) REFERENCES public.tb_estado (sigla) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_endereco_municipio FOREIGN KEY (id_municipio) REFERENCES public.tb_municipio (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_endereco_cotacao FOREIGN KEY (id_cotacao) REFERENCES public.tb_cotacao (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tb_cotacao_endereco OWNER to postgres;

-- Table: public.tb_cotacao_frete

CREATE TABLE public.tb_cotacao_frete
(
    id bigint NOT NULL,
    id_cotacao bigint NOT NULL,
    id_embalagem integer,
    peso_itens numeric(10,3) NOT NULL,
    qt_itens smallint NOT NULL,
    valor numeric(10,2) NOT NULL,
    prazo_dias smallint NOT NULL,
    cd_servico character varying(5) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pktb_cotacao_frete PRIMARY KEY (id),
    CONSTRAINT fk_cotacao_frete_embalagem FOREIGN KEY (id_embalagem) REFERENCES public.tb_embalagem (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_frete_cotacao FOREIGN KEY (id_cotacao) REFERENCES public.tb_cotacao (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fkes7gkh010ieq7ctgqqpb4v50f FOREIGN KEY (id_cotacao) REFERENCES public.tb_cotacao (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tb_cotacao_frete OWNER to postgres;
CREATE INDEX "tb_cotacao_frete_id_cotacao_Idx" ON public.tb_cotacao_frete USING btree (id_cotacao ASC NULLS LAST) TABLESPACE pg_default;

-- Table: public.tb_cotacao_item

CREATE TABLE public.tb_cotacao_item
(
    id bigint NOT NULL,
    id_cotacao bigint NOT NULL,
    id_produto integer NOT NULL,
    cd_tamanho character varying(3) COLLATE pg_catalog."default" NOT NULL,
    quantidade smallint NOT NULL,
    vl_unitario numeric(10,2) NOT NULL,
    vl_corrigido_tamanho numeric(10,2) NOT NULL,
    vl_total numeric(10,2) NOT NULL,
    peso_total numeric(10,3) NOT NULL,
    CONSTRAINT pktb_cotacao_item PRIMARY KEY (id),
    CONSTRAINT fk92m1orgkgf2dutd43jwuk27oc FOREIGN KEY (id_cotacao) REFERENCES public.tb_cotacao (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_item_cotacao FOREIGN KEY (id_cotacao) REFERENCES public.tb_cotacao (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_item_produto FOREIGN KEY (id_produto) REFERENCES public.tb_produto (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_cotacao_item_tamanho_produto FOREIGN KEY (cd_tamanho) REFERENCES public.tb_tamanho_produto (codigo) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tb_cotacao_item OWNER to postgres;
CREATE INDEX "tb_cotacao_item_id_cotacao_Idx" ON public.tb_cotacao_item USING btree (id_cotacao ASC NULLS LAST) TABLESPACE pg_default;

-- Sequences
CREATE SEQUENCE public.sq_cotacao INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;
ALTER SEQUENCE public.sq_cotacao OWNER TO postgres;

CREATE SEQUENCE public.sq_cotacao_frete INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;
ALTER SEQUENCE public.sq_cotacao_frete OWNER TO postgres;

CREATE SEQUENCE public.sq_cotacao_item INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;
ALTER SEQUENCE public.sq_cotacao_item OWNER TO postgres;