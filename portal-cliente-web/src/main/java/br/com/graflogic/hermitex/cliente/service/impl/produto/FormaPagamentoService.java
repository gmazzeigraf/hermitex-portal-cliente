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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaFormaPagamento;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusFormaPagamento;
import br.com.graflogic.hermitex.cliente.data.entity.aud.FormaPagamentoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.data.impl.aud.FormaPagamentoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.produto.FormaPagamentoRepository;
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
public class FormaPagamentoService {

	private static final String CACHE_NAME = "formasPagamento";

	@Autowired
	private FormaPagamentoRepository repository;

	@Autowired
	private FormaPagamentoAuditoriaRepository auditoriaRepository;

	@Autowired
	private CacheUtil cacheUtil;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(FormaPagamento entity) {
		entity.setStatus(DomStatusFormaPagamento.ATIVA);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaFormaPagamento.CADASTRO, null);

		limpaCache(entity.getIdCliente());
	}

	@Transactional(rollbackFor = Throwable.class)
	public void atualiza(FormaPagamento entity) {
		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaFormaPagamento.ATUALIZACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void inativa(FormaPagamento entity) {
		entity.setStatus(DomStatusFormaPagamento.INATIVA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaFormaPagamento.INATIVACAO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void ativa(FormaPagamento entity) {
		entity.setStatus(DomStatusFormaPagamento.ATIVA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), null, DomEventoAuditoriaFormaPagamento.ATIVACAO, null);
	}

	private void executaAtualiza(FormaPagamento entity) {
		try {
			repository.update(entity);

			limpaCache(entity.getIdCliente());

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<FormaPagamento> consulta(FormaPagamento entity) {
		return repository.consulta(entity);
	}

	@SuppressWarnings("unchecked")
	public List<FormaPagamento> consultaPorCliente(Integer idCliente, boolean somenteAtivas) {
		String key = idCliente.toString();

		if (somenteAtivas) {
			key += "ativas";
		}

		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);

		if (null == cacheObj) {
			// Consulta
			FormaPagamento filter = new FormaPagamento();
			filter.setIdCliente(idCliente);
			if (somenteAtivas) {
				filter.setStatus(DomStatusFormaPagamento.ATIVA);
			}

			List<FormaPagamento> objetos = consulta(filter);

			// Atualiza o cache
			cacheUtil.putOnCache(CACHE_NAME, key, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, key);
		}

		return (List<FormaPagamento>) ObjectCopier.copy(cacheObj);
	}

	public FormaPagamento consultaPorId(Integer id) {
		FormaPagamento entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	// Util
	private String registraAuditoria(Integer id, FormaPagamento objeto, String codigoEvento, String observacao) {
		FormaPagamentoAuditoria evento = new FormaPagamentoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (FormaPagamento) ObjectCopier.copy(objeto);

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