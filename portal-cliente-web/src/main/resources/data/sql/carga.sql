-- CONFIGURACAO
INSERT INTO tb_configuracao VALUES ('usuario.email_senha.titulo', 'Hermitex Web - Senha de acesso');
INSERT INTO tb_configuracao VALUES ('usuario.email_senha.conteudo', '<meta charset="UTF-8"><p>Ol&aacute; #NOME#,</p>
<p>Seu usu&aacute;rio foi cadastrado em nosso sistema e o acesso dever&aacute; ser feito atrav&eacute;s do seu e-mail e a senha <span style="color: #ff0000;"><strong>#SENHA#</strong></span>.</p>
<p>Caso queira, troque sua senha atrav&eacute;s da op&ccedil;&atilde;o contida no menu localizado no canto superior direito do sistema.</p>
<p>Obrigado,</p><p>Acesse&nbsp;<a href="http://hermitex.com.br">http://hermitex.com.br</a>.</p>');

INSERT INTO tb_configuracao VALUES ('usuario.email_nova_senha.titulo', 'Hermitex Web - Nova senha de acesso');
INSERT INTO tb_configuracao VALUES ('usuario.email_nova_senha.conteudo', '<meta charset="UTF-8"><p>Ol&aacute; #NOME#,</p>
<p>A sua nova senha para acesso ao sistema &eacute; <span style="color: #ff0000;"><strong>#SENHA#</strong></span>.</p>
<p>Caso queira, troque sua senha atrav&eacute;s da op&ccedil;&atilde;o contida no menu localizado no canto superior direito do sistema.</p>
<p>Obrigado,</p><p>Acesse&nbsp;<a href="http://hermitex.com.br">http://hermitex.com.br</a>.</p>');

INSERT INTO tb_configuracao VALUES ('email.servidor', 'br432.hostgator.com.br');
INSERT INTO tb_configuracao VALUES ('email.porta', '465');
INSERT INTO tb_configuracao VALUES ('email.usuario', 'no-reply@graflogic.com.br');
INSERT INTO tb_configuracao VALUES ('email.senha', 'n127415');
INSERT INTO tb_configuracao VALUES ('email.ssl', 'true');

INSERT INTO tb_configuracao VALUES ('web.url', 'http://localhost:8080/web');