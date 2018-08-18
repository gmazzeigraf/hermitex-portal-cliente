package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.io.IOException;
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
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaJanelaCompra;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatus;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusJanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.aud.JanelaCompraAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.hermitex.cliente.data.impl.aud.JanelaCompraAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.JanelaCompraRepository;
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
public class JanelaCompraService {

	@Autowired
	private JanelaCompraRepository repository;

	@Autowired
	private JanelaCompraAuditoriaRepository auditoriaRepository;

	@Autowired
	private PedidoService pedidoService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(JanelaCompra entity) {
		entity.setStatus(DomStatusJanelaCompra.CADASTRADA);

		if (!repository.isJanelaComprasDisponivel(entity.getIdCliente(), entity.getDataAbertura(), entity.getDataFechamento())) {
			throw new DadosInvalidosException("Já existe uma janela cadastrada nesse período");
		}

		repository.store(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaJanelaCompra.CADASTRO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void fecha(JanelaCompra entity) {
		if (!entity.isAtiva()) {
			throw new DadosInvalidosException("A janela não está ativa para ser fechada");
		}

		entity.setStatus(DomStatusJanelaCompra.FECHADA);

		// Consulta os pedidos
		List<PedidoSimple> pedidos = pedidoService.consultaPorJanelaCompra(entity.getId());

		entity.setQuantidadePedido(pedidos.size());

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaJanelaCompra.FECHAMENTO, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void reabre(JanelaCompra entity) {
		if (!entity.isFechada()) {
			throw new DadosInvalidosException("A janela não está fechada para ser reaberta");
		}

		entity.setStatus(DomStatusJanelaCompra.REABERTA);

		// Reinicia os pedidos
		entity.setQuantidadePedido(null);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaJanelaCompra.REABERTURA, null);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void cancela(JanelaCompra entity) {
		if (!entity.isAtiva()) {
			throw new DadosInvalidosException("A janela não está ativa para ser cancelada");
		}

		List<PedidoSimple> pedidos = pedidoService.consultaPorJanelaCompra(entity.getId());

		if (!pedidos.isEmpty()) {
			throw new DadosInvalidosException("Janelas com pedidos não podem ser canceladas");
		}

		entity.setStatus(DomStatusJanelaCompra.CANCELADA);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaJanelaCompra.CANCELAMENTO, null);
	}

	private void executaAtualiza(JanelaCompra entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	// Consulta
	public List<JanelaCompra> consulta(JanelaCompra entity) {
		return repository.consulta(entity);
	}

	public JanelaCompra consultaPorId(Integer id) {
		JanelaCompra entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	public List<JanelaCompra> consultaPorCliente(Integer idCliente) {
		JanelaCompra filter = new JanelaCompra();
		filter.setIdCliente(idCliente);

		return consulta(filter);
	}

	public JanelaCompra consultaPorClienteData(Integer idCliente, Date data) {
		try {
			return repository.consultaPorClienteData(idCliente, data);

		} catch (NoResultException e) {
			throw new ResultadoNaoEncontradoException();
		}
	}

	public JanelaCompra consultaAtiva(Integer idCliente) {
		try {
			JanelaCompra entity = consultaPorClienteData(idCliente, new Date());

			if (!entity.isAtiva()) {
				throw new DadosInvalidosException("A janela de compras está fechada");
			}

			return entity;

		} catch (ResultadoNaoEncontradoException e) {
			throw new DadosInvalidosException("Não existe janela de compras cadastrada");
		}
	}

	// Extracao
	public byte[] geraExtracao(JanelaCompra entity) throws IOException {
		// Consulta os pedidos
		List<PedidoSimple> pedidos = pedidoService.consultaPorJanelaCompra(entity.getId());

		if (pedidos.isEmpty()) {
			throw new DadosInvalidosException("Nenhum pedido encontrado para a janela de compra");
		}

		// Gera os itens
		List<PedidoItem> itens = new ArrayList<>();
		for (PedidoSimple pedido : pedidos) {
			// Nao considera os pedidos cancelados
			if (DomStatus.CANCELADO.equals(pedido.getStatus())) {
				continue;
			}

			itens.addAll(pedidoService.consultaItensPorPedido(pedido.getId()));
		}

		// Gera a extracao
		return pedidoService.geraExtracao(itens);
	}

	// Util
	private String registraAuditoria(Integer id, JanelaCompra objeto, String codigoEvento, String observacao) {
		JanelaCompraAuditoria evento = new JanelaCompraAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (JanelaCompra) ObjectCopier.copy(objeto);

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}
}