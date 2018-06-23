package br.com.graflogic.hermitex.cliente.service.impl.auxiliar;

import java.io.Serializable;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.impl.auxiliar.ConfiguracaoRepository;
import br.com.graflogic.hermitex.cliente.data.util.ConfiguracaoEnum;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class ConfiguracaoService implements Serializable {

	private static final long serialVersionUID = -4054810010325009173L;

	private static final String CACHE_NAME = "configuracoes";

	@Autowired
	private ConfiguracaoRepository repository;

	@Autowired
	private CacheUtil cacheUtil;

	public String consulta(ConfiguracaoEnum configuracao) {
		String codigo = configuracao.getCodigo();

		if (null == cacheUtil.findOnCache(CACHE_NAME, codigo)) {
			try {
				String valor = repository.consulta(codigo);
				cacheUtil.putOnCache(CACHE_NAME, codigo, valor);
				return valor;

			} catch (NoResultException e) {
				throw new ResultadoNaoEncontradoException();
			}
		} else {
			return (String) cacheUtil.findOnCache(CACHE_NAME, codigo);
		}
	}
}