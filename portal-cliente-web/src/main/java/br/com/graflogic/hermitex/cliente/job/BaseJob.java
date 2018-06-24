package br.com.graflogic.hermitex.cliente.job;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.sql.DataSource;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomStatusProcessamento;

/**
 * 
 * @author gmazz
 *
 */
public abstract class BaseJob extends QuartzJobBean {

	protected Logger logger;

	protected Properties properties;

	private JdbcTemplate jdbcTemplate;

	protected int quantidadeSucesso;

	protected int quantidadeErro;

	protected String caminhoArquivo;

	protected abstract void execute(ApplicationContext applicationContext) throws IOException, SQLException;

	public void executeInternal(ApplicationContext applicationContext) throws JobExecutionException {
		logger = LoggerFactory.getLogger(this.getClass());

		try {
			properties = PropertiesLoaderUtils
					.loadProperties(new InputStreamResource(getClass().getClassLoader().getResourceAsStream("job/job.properties")));
		} catch (IOException e) {
			logger.error("Erro ao carregar configuracoes", e);
			return;
		}

		String codigoProcessamento = getCodigo();

		if (isRunning()) {
			logger.info("Ja existe um processamento " + codigoProcessamento + " em execucao");
			return;
		}

		Date dataInicio = new Date();
		try {
			setRunning(true);

			DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");

			jdbcTemplate = new JdbcTemplate(dataSource);

			execute(applicationContext);

			audita(codigoProcessamento, dataInicio, DomStatusProcessamento.FINALIZADO);

		} catch (Throwable t) {
			logger.error("Erro durante o processamento " + codigoProcessamento, t);

			audita(codigoProcessamento, dataInicio, DomStatusProcessamento.ERRO);

		} finally {
			setRunning(false);
		}
	}

	private void audita(String codigoProcessamento, Date dataInicio, String status) {
		if (quantidadeSucesso > 0 || quantidadeErro > 0 || status.equals(DomStatusProcessamento.ERRO)) {
			jdbcTemplate.update(
					"INSERT INTO tb_auditoria_processamento(id, cd_processamento, dt_inicio, dt_fim, qt_sucesso, qt_erro, caminho_arquivo, status)"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
					new Object[] { UUID.randomUUID().toString(), codigoProcessamento, dataInicio, new Date(), quantidadeSucesso, quantidadeErro,
							caminhoArquivo, status });
		}
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			executeInternal((ApplicationContext) context.getScheduler().getContext().get("applicationContext"));
		} catch (SchedulerException e) {
			throw new RuntimeException("Erro ao iniciar o contexto", e);
		}
	}

	protected abstract boolean isRunning();

	protected abstract void setRunning(boolean running);

	protected abstract String getCodigo();

	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}