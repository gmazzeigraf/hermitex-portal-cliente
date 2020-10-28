package br.com.graflogic.hermitex.cliente.service.impl.produto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaCorProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusCor;
import br.com.graflogic.hermitex.cliente.data.entity.aud.CorProdutoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CorProduto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.CorProdutoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.CorProdutoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class CorProdutoService {

	private static final String CACHE_NAME = "coresProduto";

	@Autowired
	private CorProdutoRepository repository;

	@Autowired
	private CorProdutoAuditoriaRepository auditoriaRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(CorProduto entity) {
		entity.setStatus(DomStatusCor.ATIVO);

		try {
			consultaPorCodigo(entity.getCodigo());

			throw new DadosInvalidosException("Código já cadastrado");

		} catch (ResultadoNaoEncontradoException e) {
		}

		repository.store(entity);

		registraAuditoria(entity.getCodigo(), entity, DomEventoAuditoriaCorProduto.CADASTRO, null);

		limpaCache();
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(CorProduto entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getCodigo(), entity, DomEventoAuditoriaCorProduto.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(CorProduto entity) {
		entity.setStatus(DomStatusCor.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getCodigo(), null, DomEventoAuditoriaCorProduto.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(CorProduto entity) {
		entity.setStatus(DomStatusCor.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getCodigo(), null, DomEventoAuditoriaCorProduto.ATIVACAO, null);
	}

	private void executaAtualiza(CorProduto entity) {
		try {
			repository.update(entity);

			limpaCache();

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<CorProduto> consulta(CorProduto entity) {
		return repository.consulta(entity);
	}

	@SuppressWarnings("unchecked")
	public List<CorProduto> consulta(boolean somenteAtivos) {
		String key = null;

		if (somenteAtivos) {
			key = "ativos";
		} else {
			key = "all";
		}

		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);

		if (null == cacheObj) {
			// Consulta
			CorProduto filter = new CorProduto();
			if (somenteAtivos) {
				filter.setStatus(DomStatusCor.ATIVO);
			}

			List<CorProduto> objetos = consulta(filter);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, key, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);
		}

		return (List<CorProduto>) ObjectCopier.copy(cacheObj);
	}

	public CorProduto consultaPorCodigo(String codigo) {
		CorProduto entity = repository.findById(codigo);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	// Util
	private String registraAuditoria(String codigo, CorProduto objeto, String codigoEvento, String observacao) {
		CorProdutoAuditoria evento = new CorProdutoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setCodigoRelacionado(codigo);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (CorProduto) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void limpaCache() {
		cacheUtil.putOnCache(CACHE_NAME, "ativos", null);
		cacheUtil.putOnCache(CACHE_NAME, "all", null);
	}
}