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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaEmbalagem;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusEmbalagem;
import br.com.graflogic.hermitex.cliente.data.entity.aud.EmbalagemAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Embalagem;
import br.com.graflogic.hermitex.cliente.data.impl.aud.EmbalagemAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.EmbalagemRepository;
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
public class EmbalagemService {

	private static final String CACHE_NAME = "embalagens";

	@Autowired
	private EmbalagemRepository repository;

	@Autowired
	private EmbalagemAuditoriaRepository auditoriaRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Embalagem entity) {
		entity.setStatus(DomStatusEmbalagem.ATIVO);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaEmbalagem.CADASTRO, null);

		limpaCache();
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(Embalagem entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaEmbalagem.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(Embalagem entity) {
		entity.setStatus(DomStatusEmbalagem.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaEmbalagem.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(Embalagem entity) {
		entity.setStatus(DomStatusEmbalagem.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaEmbalagem.ATIVACAO, null);
	}

	private void executaAtualiza(Embalagem entity) {
		try {
			repository.update(entity);

			limpaCache();

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<Embalagem> consulta(Embalagem entity) {
		return repository.consulta(entity);
	}

	@SuppressWarnings("unchecked")
	public List<Embalagem> consulta(boolean somenteAtivos) {
		String key = null;

		if (somenteAtivos) {
			key = "ativos";
		} else {
			key = "all";
		}

		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);

		if (null == cacheObj) {
			// Consulta
			Embalagem filter = new Embalagem();
			if (somenteAtivos) {
				filter.setStatus(DomStatusEmbalagem.ATIVO);
			}

			List<Embalagem> objetos = consulta(filter);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, key, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);
		}

		return (List<Embalagem>) ObjectCopier.copy(cacheObj);
	}

	public Embalagem consultaPorId(Integer id) {
		Embalagem entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	// Util
	private String registraAuditoria(Integer id, Embalagem objeto, String codigoEvento, String observacao) {
		EmbalagemAuditoria evento = new EmbalagemAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Embalagem) ObjectCopier.copy(objeto);

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