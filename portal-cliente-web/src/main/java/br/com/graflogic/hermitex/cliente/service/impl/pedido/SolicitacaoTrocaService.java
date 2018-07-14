package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaSolicitacaoTroca;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusSolicitacaoTroca;
import br.com.graflogic.hermitex.cliente.data.entity.aud.SolicitacaoTrocaAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.SolicitacaoTroca;
import br.com.graflogic.hermitex.cliente.data.impl.aud.SolicitacaoTrocaAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.SolicitacaoTrocaRepository;
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
public class SolicitacaoTrocaService {

	@Autowired
	private SolicitacaoTrocaRepository repository;

	@Autowired
	private SolicitacaoTrocaAuditoriaRepository auditoriaRepository;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(SolicitacaoTroca entity) {
		// TODO Valida a quantidade e se ja foi feita para o produto

		entity.setStatus(DomStatusSolicitacaoTroca.CADASTRADA);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaSolicitacaoTroca.CADASTRO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void finaliza(SolicitacaoTroca entity, String observacao) {
		if (!entity.isCadastrada()) {
			throw new DadosInvalidosException("Apenas solicitações cadastradas podem ser marcados como finalizadas");
		}

		entity.setStatus(DomStatusSolicitacaoTroca.FINALIZADA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaSolicitacaoTroca.FINALIZACAO, observacao);
	}

	private void executaAtualiza(SolicitacaoTroca entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<SolicitacaoTroca> consulta(SolicitacaoTroca entity) {
		return repository.consulta(entity);
	}

	public SolicitacaoTroca consultaPorId(Long id) {
		try {
			return repository.consultaPorId(id);

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}

	// Util
	private String registraAuditoria(Long id, SolicitacaoTroca objeto, String codigoEvento, String observacao) {
		SolicitacaoTrocaAuditoria evento = new SolicitacaoTrocaAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (SolicitacaoTroca) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}