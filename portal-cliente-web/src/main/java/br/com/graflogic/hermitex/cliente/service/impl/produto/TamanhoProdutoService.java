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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaTamanhoProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusTamanho;
import br.com.graflogic.hermitex.cliente.data.entity.aud.TamanhoProdutoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.TamanhoProduto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.TamanhoProdutoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.TamanhoProdutoRepository;
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
public class TamanhoProdutoService {

	private static final String CACHE_NAME = "tamanhosProduto";

	@Autowired
	private TamanhoProdutoRepository repository;

	@Autowired
	private TamanhoProdutoAuditoriaRepository auditoriaRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(TamanhoProduto entity) {
		entity.setStatus(DomStatusTamanho.ATIVO);

		try {
			consultaPorCodigo(entity.getCodigo());

			throw new DadosInvalidosException("Código já cadastrado");

		} catch (ResultadoNaoEncontradoException e) {
		}

		repository.store(entity);

		registraAuditoria(entity.getCodigo(), entity, DomEventoAuditoriaTamanhoProduto.CADASTRO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(TamanhoProduto entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getCodigo(), entity, DomEventoAuditoriaTamanhoProduto.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(TamanhoProduto entity) {
		entity.setStatus(DomStatusTamanho.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getCodigo(), null, DomEventoAuditoriaTamanhoProduto.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(TamanhoProduto entity) {
		entity.setStatus(DomStatusTamanho.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getCodigo(), null, DomEventoAuditoriaTamanhoProduto.ATIVACAO, null);
	}

	private void executaAtualiza(TamanhoProduto entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<TamanhoProduto> consulta(TamanhoProduto entity) {
		return repository.consulta(entity);
	}

	@SuppressWarnings("unchecked")
	public List<TamanhoProduto> consulta(boolean somenteAtivos) {
		String key = null;

		if (somenteAtivos) {
			key = "ativos";
		} else {
			key = "all";
		}

		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);

		if (null == cacheObj) {
			// Consulta
			TamanhoProduto filter = new TamanhoProduto();
			if (somenteAtivos) {
				filter.setStatus(DomStatusTamanho.ATIVO);
			}

			List<TamanhoProduto> objetos = consulta(filter);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, key, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);
		}

		return (List<TamanhoProduto>) ObjectCopier.copy(cacheObj);
	}

	public TamanhoProduto consultaPorCodigo(String codigo) {
		TamanhoProduto entity = repository.findById(codigo);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	// Util
	private String registraAuditoria(String codigo, TamanhoProduto objeto, String codigoEvento, String observacao) {
		TamanhoProdutoAuditoria evento = new TamanhoProdutoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setCodigoRelacionado(codigo);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (TamanhoProduto) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}