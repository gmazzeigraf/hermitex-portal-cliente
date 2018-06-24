package br.com.graflogic.hermitex.cliente.service.impl.auxiliar;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.hermitex.cliente.data.impl.auxiliar.EstadoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class EstadoService {

	private static final String CACHE_NAME = "estados";
	private static final String ALL_KEY = "all";

	@Autowired
	private EstadoRepository repository;

	@Autowired
	private CacheUtil cacheUtil;

	@SuppressWarnings("unchecked")
	public List<Estado> consulta() {
		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, ALL_KEY);

		if (null == cacheObj) {
			List<Estado> objetos = repository.consulta();
			cacheUtil.putOnCache(CACHE_NAME, ALL_KEY, ObjectCopier.copy(objetos));
			return objetos;

		} else {
			List<Estado> objetos = new ArrayList<Estado>();
			objetos.addAll((List<Estado>) cacheObj);
			return objetos;

		}
	}

	public Estado consultaPorSigla(String sigla) {
		for (Estado estado : consulta()) {
			if (estado.getSigla().equals(sigla)) {
				return estado;
			}
		}

		throw new ResultadoNaoEncontradoException();
	}
}