package br.com.graflogic.hermitex.cliente.job;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.SendFailedException;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sun.mail.smtp.SMTPAddressFailedException;

import br.com.graflogic.commonutil.util.email.EmailSender;
import br.com.graflogic.hermitex.cliente.data.dom.DomNotificacao.DomStatus;
import br.com.graflogic.hermitex.cliente.data.dom.DomNotificacao.DomTipo;
import br.com.graflogic.hermitex.cliente.data.util.ConfiguracaoEnum;

/**
 * 
 * @author gmazz
 *
 */
public class EmailJob extends BaseJob {

	private static boolean RUNNING;

	@Override
	protected void execute(ApplicationContext applicationContext) throws IOException, SQLException {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();

		// Consulta emails para envio
		List<Map<String, Object>> notificacoes = jdbcTemplate.queryForList(
				"SELECT id, titulo, conteudo, destinatarios, anexos FROM tb_notificacao WHERE status IN (?, ?) AND tipo = ? ORDER BY dt_solicitacao",
				new Object[] { DomStatus.ERRO, DomStatus.PENDENTE, DomTipo.EMAIL });

		if (!notificacoes.isEmpty()) {
			// Consulta configuracoes do email
			List<Map<String, Object>> configuracoes = jdbcTemplate.queryForList(
					"SELECT codigo, valor FROM tb_configuracao WHERE codigo in (?, ?, ?, ?, ?)",
					new Object[] { ConfiguracaoEnum.EMAIL_SERVIDOR.getCodigo(), ConfiguracaoEnum.EMAIL_PORTA.getCodigo(),
							ConfiguracaoEnum.EMAIL_USUARIO.getCodigo(), ConfiguracaoEnum.EMAIL_SENHA.getCodigo(),
							ConfiguracaoEnum.EMAIL_SSL.getCodigo(), });

			Map<String, String> configuracoesEmail = new HashMap<>();
			for (Map<String, Object> configuracao : configuracoes) {
				configuracoesEmail.put((String) configuracao.get("codigo"), (String) configuracao.get("valor"));
			}

			EmailSender sender = new EmailSender();

			// Envia os emails
			for (Map<String, Object> notificacao : notificacoes) {
				String id = (String) notificacao.get("id");
				String titulo = (String) notificacao.get("titulo");
				String conteudo = (String) notificacao.get("conteudo");
				String destinatariosStr = (String) notificacao.get("destinatarios");
				String anexosStr = (String) notificacao.get("anexos");

				List<File> anexos = new ArrayList<>();

				if (null != anexosStr) {
					List<String> caminhoAnexos = Arrays.asList(anexosStr.split(";"));
					for (String caminhoAnexo : caminhoAnexos) {
						File anexo = new File(caminhoAnexo);

						if (anexo.exists()) {
							anexos.add(anexo);
						}
					}
				}

				String[] destinatarios = destinatariosStr.split(";");

				Date dataEnvio = null;
				String status = null;

				try {
					sender.send(EmailSender.PROTOCOL_SMTP, configuracoesEmail.get(ConfiguracaoEnum.EMAIL_SERVIDOR.getCodigo()),
							configuracoesEmail.get(ConfiguracaoEnum.EMAIL_PORTA.getCodigo()),
							configuracoesEmail.get(ConfiguracaoEnum.EMAIL_USUARIO.getCodigo()),
							configuracoesEmail.get(ConfiguracaoEnum.EMAIL_USUARIO.getCodigo()),
							configuracoesEmail.get(ConfiguracaoEnum.EMAIL_SENHA.getCodigo()), destinatarios, null, titulo, conteudo,
							anexos.toArray(new File[anexos.size()]), false, true,
							Boolean.parseBoolean(configuracoesEmail.get(ConfiguracaoEnum.EMAIL_SSL.getCodigo())),
							Boolean.parseBoolean(configuracoesEmail.get(ConfiguracaoEnum.EMAIL_SSL.getCodigo())), false, false);

					logger.info("Notificação {} enviada com sucesso", id);

					dataEnvio = new Date();
					status = DomStatus.ENVIADA;

					quantidadeSucesso++;

				} catch (Throwable t) {
					quantidadeErro++;
					if (t instanceof SendFailedException && t.getCause() instanceof SMTPAddressFailedException) {
						logger.warn("Endereco invalido para notificacao " + id);

						status = DomStatus.DADOS_INVALIDOS;

					} else {
						logger.error("Erro ao enviar notificacao " + id, t);

						status = DomStatus.ERRO;
					}
				}

				jdbcTemplate.update("UPDATE tb_notificacao SET dt_envio = ?, status = ? WHERE id = ?", new Object[] { dataEnvio, status, id });
			}
		}
	}

	@Override
	protected String getCodigo() {
		return properties.getProperty("email.codigo");
	}

	@Override
	protected boolean isRunning() {
		return RUNNING;
	}

	@Override
	protected void setRunning(boolean running) {
		RUNNING = running;
	}
}