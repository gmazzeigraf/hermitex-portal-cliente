-- CONFIGURACAO
INSERT INTO tb_configuracao VALUES ('usuario.email_senha.titulo', 'Senha de acesso');
INSERT INTO tb_configuracao VALUES ('usuario.email_senha.conteudo', '<meta charset="UTF-8"><p>Ol&aacute; #NOME#,</p>
<p>Seu usu&aacute;rio foi cadastrado em nosso sistema de compras e o acesso dever&aacute; ser feito atrav&eacute;s do seu e-mail e a senha <span style="color: #ff0000;"><strong>#SENHA#</strong></span>.</p>
<p>Troque sua senha no primeiro acesso.</p>
<p>Obrigado,</p><p>Acesse&nbsp;<a href="http://localhost:8080/web">http://localhost:8080/web</a>.</p>');

INSERT INTO tb_configuracao VALUES ('usuario.email_nova_senha.titulo', 'Nova senha de acesso');
INSERT INTO tb_configuracao VALUES ('usuario.email_nova_senha.conteudo', '<meta charset="UTF-8"><p>Ol&aacute; #NOME#,</p>
<p>A sua nova senha para acesso ao sistema de compras &eacute; <span style="color: #ff0000;"><strong>#SENHA#</strong></span>.</p>
<p>Troque sua senha no primeiro acesso.</p>
<p>Obrigado,</p><p>Acesse&nbsp;<a href="http://localhost:8080/web">http://localhost:8080/web</a>.</p>');

INSERT INTO tb_configuracao VALUES ('email.servidor', 'email-ssl.com.br');
INSERT INTO tb_configuracao VALUES ('email.porta', '465');
INSERT INTO tb_configuracao VALUES ('email.usuario', 'no-reply@hermitex.com.br');
INSERT INTO tb_configuracao VALUES ('email.senha', '1966Hermitex@');
INSERT INTO tb_configuracao VALUES ('email.ssl', 'true');

INSERT INTO tb_configuracao VALUES ('web.url', 'http://localhost:8080/web');

INSERT INTO tb_configuracao VALUES ('pagamento_boleto.codigo_banco', '033');
INSERT INTO tb_configuracao VALUES ('pagamento_boleto.instrucao', 'Após o vencimento esse boleto será cancelado e será necessário realizar uma nova compra.');
INSERT INTO tb_configuracao VALUES ('pagamento.merchant_key', 'c0ddc16b-0ce7-43eb-bd53-582e658632ba');
INSERT INTO tb_configuracao VALUES ('pagamento.url', 'https://sandbox.mundipaggone.com');

INSERT INTO tb_configuracao VALUES ('correios.codigo_empresa', '12009903');
INSERT INTO tb_configuracao VALUES ('correios.senha', 'Y7T27');
INSERT INTO tb_configuracao VALUES ('correios.url', 'http://ws.correios.com.br/calculador/CalcPrecoPrazo.asmx');

INSERT INTO tb_configuracao VALUES ('frete.cep_origem', '13041311');