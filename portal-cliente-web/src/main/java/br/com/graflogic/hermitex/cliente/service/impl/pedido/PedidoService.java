package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.graflogic.base.service.gson.GsonUtil;
import br.com.graflogic.commonutil.util.PessoaFisicaValidator;
import br.com.graflogic.commonutil.util.PessoaJuridicaValidator;
import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomFormaPagamento;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatus;
import br.com.graflogic.hermitex.cliente.data.entity.aud.PedidoAuditoria;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.hermitex.cliente.data.impl.aud.PedidoAuditoriaRepository;
import br.com.graflogic.hermitex.cliente.data.impl.cadastro.PedidoEnderecoRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoItemRepository;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.model.DadosPagamentoCartaoCredito;
import br.com.graflogic.hermitex.cliente.service.model.FormaPagamento;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repository;

	@Autowired
	private PedidoAuditoriaRepository auditoriaRepository;

	@Autowired
	private PedidoItemRepository itemRepository;

	@Autowired
	private PedidoEnderecoRepository enderecoRepository;

	@Autowired
	private JanelaCompraService janelaCompraService;

	// Fluxo
	@Transactional(rollbackFor = Throwable.class)
	public void cadastra(Pedido entity, DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito) {
		// Caso seja compra com carta, valida o documento do portador
		if (DomFormaPagamento.CARTAO_CREDITO_1.equals(entity.getCodigoFormaPagamento())
				|| DomFormaPagamento.CARTAO_CREDITO_2.equals(entity.getCodigoFormaPagamento())) {
			String documentoPortador = dadosPagamentoCartaoCredito.getDocumentoPortador();

			if (11 == documentoPortador.length()) {
				if (!PessoaFisicaValidator.validateCPF(documentoPortador)) {
					throw new DadosInvalidosException("CPF do portador inválido");
				}
			} else if (14 == documentoPortador.length()) {
				if (!PessoaJuridicaValidator.validateCNPJ(documentoPortador)) {
					throw new DadosInvalidosException("CNPJ do portador inválido");
				}
			} else {
				throw new DadosInvalidosException("Documento do portador inválido");
			}
		}

		// Verifica se a janela de compras esta aberta
		JanelaCompra janelaCompra = janelaCompraService.consultaAtiva(entity.getIdCliente());

		entity.setIdJanelaCompra(janelaCompra.getId());

		// TODO Envia pagamento

		validaDados(entity);

		entity.setStatus(DomStatus.PAGAMENTO_PENDENTE);

		List<PedidoItem> itens = entity.getItens();
		entity.setItens(null);

		try {
			repository.store(entity);

			for (PedidoItem item : itens) {
				item.setIdPedido(entity.getId());
			}

			entity.setItens(itens);

			repository.update(entity);

			registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.CADASTRO, null);

		} catch (Throwable t) {
			entity.setItens(itens);

			throw t;
		}
	}

	public void atualiza(Pedido entity) {
		validaDados(entity);

		executaAtualiza(entity);

		registraAuditoria(entity.getId(), entity, DomEventoAuditoriaPedido.ATUALIZACAO, null);
	}

	private void executaAtualiza(Pedido entity) {
		try {
			repository.update(entity);

		} catch (OptimisticLockException e) {
			throw new DadosDesatualizadosException();
		}
	}

	public List<FormaPagamento> geraFormasPagamento(Cliente cliente, BigDecimal valorTotal) {
		List<FormaPagamento> formasPagamento = new ArrayList<>();
		formasPagamento.add(new FormaPagamento(DomFormaPagamento.BOLETO, 1));
		formasPagamento.add(new FormaPagamento(DomFormaPagamento.CARTAO_CREDITO_1, 1));
		formasPagamento.add(new FormaPagamento(DomFormaPagamento.CARTAO_CREDITO_2, 2));

		for (FormaPagamento forma : formasPagamento) {
			forma.setValor(valorTotal.divide(new BigDecimal(forma.getParcelas())).setScale(2, RoundingMode.HALF_EVEN));

			String descricao = DomPedido.domFormaPagamento.getDeValor(forma.getCodigo());

			if (DomFormaPagamento.BOLETO.equals(forma.getCodigo())) {
				descricao += " " + cliente.getDiasBoleto() + " dias";

			}

			descricao += " R$ " + forma.getValor().toString().replace(".", ",");

			forma.setDescricao(descricao);
		}

		return formasPagamento;
	}

	// Consulta
	public List<PedidoSimple> consulta(PedidoSimple entity) {
		return repository.consulta(entity);
	}

	public Pedido consultaPorId(Long id) {
		Pedido entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}

	public Pedido consultaCompletoPorId(Long id) {
		Pedido entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		preencheRelacionados(entity);

		return entity;
	}

	// Util
	private void validaDados(Pedido entity) {
		if (entity.getItens().isEmpty()) {
			throw new DadosInvalidosException("Ao menos um item deve ser adicionado");
		}
	}

	private String registraAuditoria(Long id, Pedido objeto, String codigoEvento, String observacao) {
		PedidoAuditoria evento = new PedidoAuditoria();
		evento.setId(UUID.randomUUID().toString());
		evento.setData(new Date());
		evento.setIdRelacionado(id);
		evento.setIdResponsavel(SessionUtil.getAuthenticatedUsuario().getId());
		evento.setCodigoEvento(codigoEvento);
		evento.setObservacao(observacao);
		if (null != objeto) {
			objeto = (Pedido) ObjectCopier.copy(objeto);
			// Remove as referencias recursivas
			for (PedidoItem item : objeto.getItens()) {
				item.setPedido(null);
			}
			for (PedidoEndereco endereco : objeto.getEnderecos()) {
				endereco.setPedido(null);
			}

			evento.setObjeto(GsonUtil.gson.toJson(objeto));
		}

		auditoriaRepository.store(evento);

		return evento.getId();
	}

	private void preencheRelacionados(Pedido entity) {
		entity.setItens(itemRepository.consultaPorPedido(entity.getId()));
		entity.setEnderecos(enderecoRepository.consultaPorPedido(entity.getId()));
	}
}