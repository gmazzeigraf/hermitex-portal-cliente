package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.util.ArrayList;
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
import br.com.graflogic.hermitex.cliente.data.entity.pedido.TrocaItem;
import br.com.graflogic.hermitex.cliente.data.impl.aud.TrocaAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.TrocaItemRepository;
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
	private TrocaItemRepository itemRepository;

	@Autowired
	private TrocaAuditoriaRepository auditoriaRepository;

	@Autowired
	private PedidoService pedidoService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Troca entity) {
		List<TrocaItem> itens = new ArrayList<>();

		for (TrocaItem item : entity.getItens()) {
			PedidoItem pedidoItem = pedidoService.consultaItemPorId(item.getIdPedidoItem());
			Integer quantidadeDisponivel = pedidoItem.getQuantidade();

			List<TrocaItem> solicitacoes = itemRepository.consultaPorPedidoItem(item.getIdPedidoItem());

			for (TrocaItem solicitacao : solicitacoes) {
				quantidadeDisponivel -= solicitacao.getQuantidade();
			}

			if (0 == quantidadeDisponivel) {
				throw new DadosInvalidosException(
						"Nenhuma unidade está disponível para troca do item " + item.getTituloProduto() + " no tamanho " + item.getCodigoTamanho());

			} else if (quantidadeDisponivel < item.getQuantidade()) {
				throw new DadosInvalidosException("Apenas " + quantidadeDisponivel + " unidades estão disponíveis para troca do item "
						+ item.getTituloProduto() + " no tamanho " + item.getCodigoTamanho());

			}

			if (item.getQuantidade() > 0) {
				itens.add(item);
			}
		}

		if (itens.isEmpty()) {
			throw new DadosInvalidosException("Ao menos um item deve ser selecionado");
		}

		try {
			entity.setItens(null);

			entity.setStatus(DomStatusTroca.CADASTRADA);

			repository.store(entity);

			for (TrocaItem item : itens) {
				item.setIdTroca(entity.getId());
			}

			entity.setItens(itens);

			executaAtualiza(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaTroca.CADASTRO, null);

		} catch (Throwable t) {
			entity.setItens(itens);

			throw t;
		}
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

	public Troca consultaPorId(Long id) {
		try {
			Troca entity = repository.consultaPorId(id);

			entity.setItens(consultaItens(id));

			return entity;

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}

	public List<TrocaItem> consultaItens(Long id) {
		return itemRepository.consultaPorTroca(id);
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