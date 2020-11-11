ALTER TABLE tb_filial RENAME COLUMN cnpj TO documento;

ALTER TABLE tb_filial ALTER COLUMN documento TYPE character varying(14);