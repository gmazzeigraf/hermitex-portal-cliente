package br.com.graflogic.hermitex.cliente.service.impl.auxiliar;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.impl.auxiliar.MunicipioRepository;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class MunicipioService {

	private static final String CACHE_NAME = "municipios";

	@Autowired
	private MunicipioRepository repository;

	@Autowired
	private CacheUtil cacheUtil;

	public void atualiza(Municipio entity) {
		repository.update(entity);

		cacheUtil.putOnCache(CACHE_NAME, entity.getSiglaEstado(), null);
	}

	public List<Municipio> consulta(Municipio entity) {
		return repository.consulta(entity);
	}

	@SuppressWarnings("unchecked")
	public List<Municipio> consulta(String siglaEstado) {
		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, siglaEstado);

		if (null == cacheObj) {
			List<Municipio> objetos = repository.consulta(siglaEstado);
			cacheUtil.putOnCache(CACHE_NAME, siglaEstado, ObjectCopier.copy(objetos));
			return objetos;

		} else {
			List<Municipio> objetos = new ArrayList<Municipio>();
			objetos.addAll((List<Municipio>) cacheObj);
			return objetos;

		}
	}

	public Municipio consultaPorId(Integer id) {
		return repository.findById(id);
	}

	public Municipio consultaPorNomeEstado(String nome, String siglaEstado) {
		try {
			return repository.consultaPorNomeEstado(nome, siglaEstado);

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}
}