package br.com.graflogic.hermitex.cliente.rest.wrapper;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import br.com.graflogic.base.rest.util.CacheUtil;

/**
 * 
 * @author ggraf
 *
 */
public class BaseWrapper {

	protected static final Logger LOGGER_AUDIT = LoggerFactory.getLogger("AUDIT");

	@Autowired
	private DataSource dataSource;

	@Autowired
	protected CacheUtil cacheUtil;

	public JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}
}