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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaCategoriaProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusCategoria;
import br.com.graflogic.hermitex.cliente.data.entity.aud.CategoriaProdutoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CategoriaProduto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.CategoriaProdutoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.CategoriaProdutoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class CategoriaProdutoService {

	private static final String CACHE_NAME = "categoriasProduto";

	@Autowired
	private CategoriaProdutoRepository repository;

	@Autowired
	private CategoriaProdutoAuditoriaRepository auditoriaRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(CategoriaProduto entity) {
		entity.setStatus(DomStatusCategoria.ATIVA);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCategoriaProduto.CADASTRO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(CategoriaProduto entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaCategoriaProduto.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(CategoriaProduto entity) {
		entity.setStatus(DomStatusCategoria.INATIVA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaCategoriaProduto.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(CategoriaProduto entity) {
		entity.setStatus(DomStatusCategoria.ATIVA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaCategoriaProduto.ATIVACAO, null);
	}

	private void executaAtualiza(CategoriaProduto entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<CategoriaProduto> consulta(CategoriaProduto entity) {
		return repository.consulta(entity);
	}

	@SuppressWarnings("unchecked")
	public List<CategoriaProduto> consultaPorCliente(Integer idCliente, boolean somenteAtivas) {
		String key = idCliente.toString();

		if (somenteAtivas) {
			key += "ativas";
		}

		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);

		if (null == cacheObj) {
			// Consulta
			CategoriaProduto filter = new CategoriaProduto();
			filter.setIdCliente(idCliente);
			if (somenteAtivas) {
				filter.setStatus(DomStatusCategoria.ATIVA);
			}

			List<CategoriaProduto> objetos = consulta(filter);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, key, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);
		}

		return (List<CategoriaProduto>) ObjectCopier.copy(cacheObj);
	}

	public CategoriaProduto consultaPorId(Integer id) {
		CategoriaProduto entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	// Util
	private String registraAuditoria(Integer id, CategoriaProduto objeto, String codigoEvento, String observacao) {
		CategoriaProdutoAuditoria evento = new CategoriaProdutoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (CategoriaProduto) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}