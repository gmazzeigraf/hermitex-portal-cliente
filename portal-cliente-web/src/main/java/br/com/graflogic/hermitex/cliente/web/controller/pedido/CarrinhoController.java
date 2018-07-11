package br.com.graflogic.hermitex.cliente.web.controller.pedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioCliente;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioFilial;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoEnderecoPK;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.PagamentoException;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.EstadoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.MunicipioService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.JanelaCompraService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.PedidoService;
import br.com.graflogic.hermitex.cliente.service.model.DadosPagamentoCartaoCredito;
import br.com.graflogic.hermitex.cliente.service.model.FormaPagamento;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.presentationutil.controller.BaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("session")
public class CarrinhoController extends BaseController implements InitializingBean {

	private static final long serialVersionUID = 1141659331755233586L;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private EstadoService estadoService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private JanelaCompraService janelaCompraService;

	private List<Estado> estados;

	private List<Municipio> municipiosFaturamento;

	private List<Municipio> municipiosEntrega;

	private Cliente cliente;

	private Pedido pedido;

	private PedidoEndereco enderecoFaturamento;

	private PedidoEndereco enderecoEntrega;

	private DadosPagamentoCartaoCredito dadosPagamentoCartaoCredito;

	private List<FormaPagamento> formasPagamento;

	private JanelaCompra janelaCompra;

	private String mensagemJanelaCompra;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			estados = estadoService.consulta();
			municipiosFaturamento = new ArrayList<Municipio>();
			municipiosEntrega = new ArrayList<>();

			formasPagamento = new ArrayList<>();

			if (SessionUtil.isUsuarioCliente()) {
				cliente = clienteService.consultaPorId(((UsuarioCliente) SessionUtil.getAuthenticatedUsuario()).getIdCliente());

			} else if (SessionUtil.isUsuarioFilial()) {
				cliente = clienteService.consultaPorId(((UsuarioFilial) SessionUtil.getAuthenticatedUsuario()).getIdCliente());

			}

			preparaNovoPedido();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao iniciar a sessão, contate o administrador", t);
		}
	}

	// Itens
	public void adicionaItem(PedidoItem item) {
		// Verifica se o pedido e tamanho ja estao no carrinho
		for (PedidoItem itemPedido : pedido.getItens()) {
			if (itemPedido.getIdProduto().equals(item.getIdProduto()) && itemPedido.getCodigoTamanho().equals(item.getCodigoTamanho())) {
				throw new DadosInvalidosException("Item e tamanho já adicionados no carrinho");
			}
		}

		pedido.getItens().add(item);

		atualizaPedido();
	}

	public void excluiItem(Integer index) {
		try {
			pedido.getItens().remove(index.intValue());

			atualizaPedido();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao remover item, contate o administrador", t);
		}
	}

	// Pedido
	private void atualizaPedido() {
		pedido.setValorProdutos(BigDecimal.ZERO);
		pedido.setValorTotal(BigDecimal.ZERO);

		for (PedidoItem item : pedido.getItens()) {
			pedido.setValorProdutos(pedido.getValorProdutos().add(item.getValorTotal()));
		}

		pedido.setValorTotal(pedido.getValorTotal().add(pedido.getValorProdutos()));
		pedido.setValorTotal(pedido.getValorTotal().add(pedido.getValorFrete()));
		pedido.setValorTotal(pedido.getValorTotal().setScale(2, RoundingMode.HALF_EVEN));

		formasPagamento = pedidoService.geraFormasPagamento(cliente, pedido.getValorTotal());
	}

	private void preparaNovoPedido() {
		if (!SessionUtil.isUsuarioCliente() && !SessionUtil.isUsuarioFilial()) {
			return;
		}

		pedido = new Pedido();
		pedido.setItens(new ArrayList<>());
		pedido.setEnderecos(new ArrayList<>());

		dadosPagamentoCartaoCredito = new DadosPagamentoCartaoCredito();

		pedido.setValorProdutos(BigDecimal.ZERO);
		pedido.setValorFrete(BigDecimal.ZERO);
		pedido.setValorTotal(BigDecimal.ZERO);

		enderecoFaturamento = new PedidoEndereco(new PedidoEnderecoPK(null, DomTipoEndereco.FATURAMENTO));
		enderecoEntrega = new PedidoEndereco(new PedidoEnderecoPK(null, DomTipoEndereco.ENTREGA));

		municipiosFaturamento.clear();
		municipiosEntrega.clear();

		// Prepara o pedido de acordo com o tipo de usuario
		if (SessionUtil.isUsuarioCliente()) {
			pedido.setIdCliente(((UsuarioCliente) SessionUtil.getAuthenticatedUsuario()).getIdCliente());

			Cliente cliente = clienteService.consultaCompletoPorId(pedido.getIdCliente());

			enderecoFaturamento.setSiglaEstado(cliente.getEnderecoFaturamento().getSiglaEstado());
			enderecoFaturamento.setIdMunicipio(cliente.getEnderecoFaturamento().getIdMunicipio());
			enderecoFaturamento.setCep(cliente.getEnderecoFaturamento().getCep());
			enderecoFaturamento.setBairro(cliente.getEnderecoFaturamento().getBairro());
			enderecoFaturamento.setLogradouro(cliente.getEnderecoFaturamento().getLogradouro());
			enderecoFaturamento.setNumero(cliente.getEnderecoFaturamento().getNumero());
			enderecoFaturamento.setComplemento(cliente.getEnderecoFaturamento().getComplemento());

			enderecoEntrega.setSiglaEstado(cliente.getEnderecoEntrega().getSiglaEstado());
			enderecoEntrega.setIdMunicipio(cliente.getEnderecoEntrega().getIdMunicipio());
			enderecoEntrega.setCep(cliente.getEnderecoEntrega().getCep());
			enderecoEntrega.setBairro(cliente.getEnderecoEntrega().getBairro());
			enderecoEntrega.setLogradouro(cliente.getEnderecoEntrega().getLogradouro());
			enderecoEntrega.setNumero(cliente.getEnderecoEntrega().getNumero());
			enderecoEntrega.setComplemento(cliente.getEnderecoEntrega().getComplemento());

		} else if (SessionUtil.isUsuarioFilial()) {
			pedido.setIdCliente(((UsuarioFilial) SessionUtil.getAuthenticatedUsuario()).getIdCliente());
			pedido.setIdFilial(((UsuarioFilial) SessionUtil.getAuthenticatedUsuario()).getIdFilial());

			Filial filial = filialService.consultaCompletoPorId(pedido.getIdFilial());

			enderecoFaturamento.setSiglaEstado(filial.getEnderecoFaturamento().getSiglaEstado());
			enderecoFaturamento.setIdMunicipio(filial.getEnderecoFaturamento().getIdMunicipio());
			enderecoFaturamento.setCep(filial.getEnderecoFaturamento().getCep());
			enderecoFaturamento.setBairro(filial.getEnderecoFaturamento().getBairro());
			enderecoFaturamento.setLogradouro(filial.getEnderecoFaturamento().getLogradouro());
			enderecoFaturamento.setNumero(filial.getEnderecoFaturamento().getNumero());
			enderecoFaturamento.setComplemento(filial.getEnderecoFaturamento().getComplemento());

			enderecoEntrega.setSiglaEstado(filial.getEnderecoEntrega().getSiglaEstado());
			enderecoEntrega.setIdMunicipio(filial.getEnderecoEntrega().getIdMunicipio());
			enderecoEntrega.setCep(filial.getEnderecoEntrega().getCep());
			enderecoEntrega.setBairro(filial.getEnderecoEntrega().getBairro());
			enderecoEntrega.setLogradouro(filial.getEnderecoEntrega().getLogradouro());
			enderecoEntrega.setNumero(filial.getEnderecoEntrega().getNumero());
			enderecoEntrega.setComplemento(filial.getEnderecoEntrega().getComplemento());

		}

		municipiosFaturamento.addAll(municipioService.consulta(enderecoFaturamento.getSiglaEstado()));
		municipiosEntrega.addAll(municipioService.consulta(enderecoEntrega.getSiglaEstado()));

		// TODO Calcula frete
		consultaJanelaCompra();
	}

	public void prosseguePagamento() {
		try {
			if (null == janelaCompra) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Não existe janela de compras cadastrada", null);
				return;
			}

			redirectView(getApplicationUrl() + "/pages/compra/pagamento.jsf");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao finalizar pedido, contate o administrador", t);
		}
	}

	public void finalizaPedido() {
		try {
			if (null == janelaCompra) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Não existe janela de compras cadastrada", null);
				return;
			}

			enderecoFaturamento.setPedido(pedido);
			enderecoEntrega.setPedido(pedido);

			pedido.getEnderecos().add(enderecoFaturamento);
			pedido.getEnderecos().add(enderecoEntrega);

			for (FormaPagamento forma : formasPagamento) {
				if (pedido.getCodigoFormaPagamento().equals(forma.getCodigo())) {
					pedido.setFormaPagamento(forma.getDescricao());

					if (pedido.isPagamentoCartaoCredito()) {
						dadosPagamentoCartaoCredito.setParcelas(forma.getParcelas());
					}
				}
			}

			pedidoService.cadastra(pedido, pedido.isPagamentoCartaoCredito() ? dadosPagamentoCartaoCredito : null,
					SessionUtil.getAuthenticatedUsuario().getId());

			returnInfoMessage(
					"Pedido " + pedido.getId() + " cadastrado com sucesso"
							+ (pedido.isPagamentoBoleto() ? ", visualize o boleto na página dos pedidos" : ""),
					getApplicationUrl() + "/pages/compra/produtos.jsf");

			preparaNovoPedido();

		} catch (PagamentoException e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao enviar o pagamento, contate o administrador", e);

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao cadastrar pedido, contate o administrador", t);
		}
	}

	// Janela Compra
	private void consultaJanelaCompra() {
		try {
			janelaCompra = janelaCompraService.consultaAtiva(cliente.getId());

			mensagemJanelaCompra = "A janela de compras fechará no dia "
					+ new SimpleDateFormat("dd/MM/yyyy").format(janelaCompra.getDataFechamento());

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

			janelaCompra = null;
			mensagemJanelaCompra = null;

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar janela de compras, contate o administrador", t);
		}
	}

	// Getters e Setters
	public List<Estado> getEstados() {
		return estados;
	}

	public List<Municipio> getMunicipiosFaturamento() {
		return municipiosFaturamento;
	}

	public List<Municipio> getMunicipiosEntrega() {
		return municipiosEntrega;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public PedidoEndereco getEnderecoFaturamento() {
		return enderecoFaturamento;
	}

	public PedidoEndereco getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public DadosPagamentoCartaoCredito getDadosPagamentoCartaoCredito() {
		return dadosPagamentoCartaoCredito;
	}

	public List<FormaPagamento> getFormasPagamento() {
		return formasPagamento;
	}

	public JanelaCompra getJanelaCompra() {
		return janelaCompra;
	}

	public String getMensagemJanelaCompra() {
		return mensagemJanelaCompra;
	}
}