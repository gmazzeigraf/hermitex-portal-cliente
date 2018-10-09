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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaTroca;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusTroca;
import br.com.graflogic.hermitex.cliente.data.entity.aud.TrocaAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Troca;
import br.com.graflogic.hermitex.cliente.data.impl.aud.TrocaAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.TrocaRepository;
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
public class TrocaService {

	@Autowired
	private TrocaRepository repository;

	@Autowired
	private TrocaAuditoriaRepository auditoriaRepository;

	@Autowired
	private PedidoService pedidoService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Troca entity) {
		List<Troca> solicitacoes = consultaPorPedidoItem(entity.getIdPedidoItem());

		PedidoItem pedidoItem = pedidoService.consultaItemPorId(entity.getIdPedidoItem());
		Integer quantidadeDisponivel = pedidoItem.getQuantidade();

		for (Troca solicitacao : solicitacoes) {
			quantidadeDisponivel -= solicitacao.getQuantidade();
		}

		if (0 == quantidadeDisponivel) {
			throw new DadosInvalidosException("Nenhuma unidade está disponível para troca");

		} else if (quantidadeDisponivel < entity.getQuantidade()) {
			throw new DadosInvalidosException("Apenas " + quantidadeDisponivel + " unidades estão disponíveis para troca");

		}

		entity.setStatus(DomStatusTroca.CADASTRADA);

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaTroca.CADASTRO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void finaliza(Troca entity, String observacao) {
		if (!entity.isCadastrada()) {
			throw new DadosInvalidosException("Apenas solicitações cadastradas podem ser marcados como finalizadas");
		}

		entity.setStatus(DomStatusTroca.FINALIZADA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaTroca.FINALIZACAO, observacao);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void cancela(Troca entity, String observacao) {
		if (!entity.isCadastrada()) {
			throw new DadosInvalidosException("Apenas solicitações cadastradas podem ser marcados como canceladas");
		}

		entity.setStatus(DomStatusTroca.CANCELADA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaTroca.CANCELAMENTO, observacao);
	}

	private void executaAtualiza(Troca entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<Troca> consulta(Troca entity) {
		return repository.consulta(entity);
	}

	public List<Troca> consultaPorPedidoItem(Long idPedidoItem) {
		Troca filter = new Troca();
		filter.setIdPedidoItem(idPedidoItem);

		return consulta(filter);
	}

	public Troca consultaPorId(Long id) {
		try {
			return repository.consultaPorId(id);

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}

	// Util
	private String registraAuditoria(Long id, Troca objeto, String codigoEvento, String observacao) {
		TrocaAuditoria evento = new TrocaAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Troca) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}