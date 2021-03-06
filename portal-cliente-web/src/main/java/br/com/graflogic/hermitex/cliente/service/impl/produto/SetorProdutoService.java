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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaSetorProduto;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatusSetor;
import br.com.graflogic.hermitex.cliente.data.entity.aud.SetorProdutoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.SetorProduto;
import br.com.graflogic.hermitex.cliente.data.impl.aud.SetorProdutoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.SetorProdutoRepository;
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
public class SetorProdutoService {

	private static final String CACHE_NAME = "setoresProduto";

	@Autowired
	private SetorProdutoRepository repository;

	@Autowired
	private SetorProdutoAuditoriaRepository auditoriaRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(SetorProduto entity) {
		entity.setStatus(DomStatusSetor.ATIVO);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaSetorProduto.CADASTRO, null);
		
		limpaCache(entity.getIdCliente());
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(SetorProduto entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaSetorProduto.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(SetorProduto entity) {
		entity.setStatus(DomStatusSetor.INATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaSetorProduto.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(SetorProduto entity) {
		entity.setStatus(DomStatusSetor.ATIVO);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaSetorProduto.ATIVACAO, null);
	}

	private void executaAtualiza(SetorProduto entity) {
		try {
			repository.update(entity);
			
			limpaCache(entity.getIdCliente());

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<SetorProduto> consulta(SetorProduto entity) {
		return repository.consulta(entity);
	}

	@SuppressWarnings("unchecked")
	public List<SetorProduto> consultaPorCliente(Integer idCliente, boolean somenteAtivos) {
		String key = idCliente.toString();

		if (somenteAtivos) {
			key += "ativos";
		}

		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);

		if (null == cacheObj) {
			// Consulta
			SetorProduto filter = new SetorProduto();
			filter.setIdCliente(idCliente);
			if (somenteAtivos) {
				filter.setStatus(DomStatusSetor.ATIVO);
			}

			List<SetorProduto> objetos = consulta(filter);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, key, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);
		}

		return (List<SetorProduto>) ObjectCopier.copy(cacheObj);
	}

	public SetorProduto consultaPorId(Integer id) {
		SetorProduto entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	// Util
	private String registraAuditoria(Integer id, SetorProduto objeto, String codigoEvento, String observacao) {
		SetorProdutoAuditoria evento = new SetorProdutoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (SetorProduto) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
	
	private void limpaCache(Integer idCliente) {
		cacheUtil.putOnCache(CACHE_NAME, idCliente.toString(), null);
		cacheUtil.putOnCache(CACHE_NAME, idCliente.toString() + "ativas", null);
	}
}