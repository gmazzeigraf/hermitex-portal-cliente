package br.com.graflogic.hermitex.cliente.web.controller.pedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
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
import br.com.graflogic.hermitex.cliente.service.exception.CorreiosException;
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
import br.com.graflogic.hermitex.cliente.service.model.TipoFrete;
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

	private List<TipoFrete> tiposFrete;

	private JanelaCompra janelaCompra;

	private String mensagemJanelaCompra;

	private String mensagemConclusaoPedido;

	private Integer sequencialFormaPagamento;

	private String codigoServicoFrete;

	private Integer passo;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			if (SessionUtil.isUsuarioCliente() || SessionUtil.isUsuarioFilial()) {
				cliente = clienteService.consultaPorId(SessionUtil.getIdCliente());

			} else {
				return;
			}

			estados = estadoService.consulta();
			municipiosFaturamento = new ArrayList<Municipio>();
			municipiosEntrega = new ArrayList<>();

			formasPagamento = new ArrayList<>();

			tiposFrete = new ArrayList<>();

			iniciaPassos(false);

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
	public void iniciaPassos(boolean direciona) {
		try {
			passo = 1;

			if (direciona) {
				redirectView(getApplicationUrl() + "/pages/compra/carrinho.jsf");
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao acessar o carrinho, contate o administrador", t);
		}
	}

	private void atualizaPedido() {
		pedido.setValorProdutos(BigDecimal.ZERO);
		pedido.setValorTotal(BigDecimal.ZERO);
		pedido.setPesoTotal(BigDecimal.ZERO);
		pedido.setQuantidadeTotalItens(0);

		for (PedidoItem item : pedido.getItens()) {
			pedido.setValorProdutos(pedido.getValorProdutos().add(item.getValorTotal()));

			pedido.setPesoTotal(pedido.getPesoTotal().add(item.getPesoTotal()));

			pedido.setQuantidadeTotalItens(pedido.getQuantidadeTotalItens() + item.getQuantidade());
		}

		pedido.setValorTotal(pedido.getValorTotal().add(pedido.getValorProdutos()));
		pedido.setValorTotal(pedido.getValorTotal().add(pedido.getValorFrete()));
		pedido.setValorTotal(pedido.getValorTotal().setScale(2, RoundingMode.HALF_EVEN));
	}

	private void preparaNovoPedido() {
		if (!SessionUtil.isUsuarioCliente() && !SessionUtil.isUsuarioFilial()) {
			return;
		}

		sequencialFormaPagamento = -1;

		formasPagamento.clear();

		tiposFrete.clear();

		codigoServicoFrete = null;

		pedido = new Pedido();
		pedido.setItens(new ArrayList<>());
		pedido.setEnderecos(new ArrayList<>());
		pedido.setFretes(new ArrayList<>());

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
			pedido.setIdCliente(cliente.getId());

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

			Filial filial = filialService.consultaPorId(pedido.getIdFilial());

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

		consultaJanelaCompra();
	}

	public void prosseguePagamento() {
		try {
			enderecoFaturamento.setPedido(pedido);
			enderecoEntrega.setPedido(pedido);

			pedido.getEnderecos().add(enderecoFaturamento);
			pedido.getEnderecos().add(enderecoEntrega);

			if (null == janelaCompra) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Não existe janela de compras cadastrada", null);
				return;
			}

			codigoServicoFrete = null;
			sequencialFormaPagamento = -1;

			tiposFrete.clear();
			formasPagamento.clear();

			tiposFrete = pedidoService.geraTiposFrete(pedido);

			if (pedido.getItens().isEmpty()) {
				redirectView(getApplicationUrl() + "/pages/compra/produtos.jsf");
			}

			passo = 2;

		} catch (CorreiosException e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao calcular frete, contate o administrador", e);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao prosseguir para pagamento, contate o administrador", t);
		}
	}

	public void finalizaPedido() {
		try {
			if (null == janelaCompra) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Não existe janela de compras cadastrada", null);
				return;
			}

			if (!isFormaPagamentoSelecionada()) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a forma de pagamento", null);
				return;
			}

			if (!isTipoFreteSelecionado()) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a tipo de frete", null);
				return;
			}

			pedidoService.cadastra(pedido, pedido.isPagamentoCartaoCredito() ? dadosPagamentoCartaoCredito : null,
					SessionUtil.getAuthenticatedUsuario().getId());

			mensagemConclusaoPedido = "Pedido " + pedido.getFormattedId() + " efetuado com sucesso"
					+ (pedido.isPagamentoBoleto() ? ", visualize o boleto <a href=\"" + pedido.getUrlBoleto() + "\" target=\"_blank\">aqui</a> ou na"
							: ", para mais informações acesse a")
					+ " página \"Pedido / Consulta\"";

			passo = 3;

			preparaNovoPedido();

		} catch (PagamentoException e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao enviar o pagamento, contate o administrador", e);

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao cadastrar pedido, contate o administrador", t);
		}
	}

	// Util
	public void changeFormaPagamento() {
		try {
			if (isFormaPagamentoSelecionada()) {
				// Obtem a forma de pagamento
				FormaPagamento formaPagamento = formasPagamento.get(sequencialFormaPagamento);
				pedido.setCodigoFormaPagamento(formaPagamento.getCodigo());
				pedido.setFormaPagamento(formaPagamento.getDescricao());

				if (pedido.isPagamentoCartaoCredito()) {
					dadosPagamentoCartaoCredito.setParcelas(formaPagamento.getParcelas());
				}
			} else {
				pedido.setCodigoFormaPagamento(null);
				pedido.setFormaPagamento(null);
				dadosPagamentoCartaoCredito.setParcelas(null);
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar forma de pagamento, contate o administrador", t);
		}
	}

	public void changeTipoFrete() {
		try {
			pedido.getFretes().clear();
			pedido.setValorFrete(BigDecimal.ZERO);
			formasPagamento.clear();
			sequencialFormaPagamento = -1;

			if (StringUtils.isNotEmpty(codigoServicoFrete)) {
				for (TipoFrete tipoFrete : tiposFrete) {
					if (codigoServicoFrete.equals(tipoFrete.getCodigoServico())) {
						pedido.setFretes(tipoFrete.getFretes());
						pedido.setValorFrete(tipoFrete.getValor());

						atualizaPedido();

						formasPagamento = pedidoService.geraFormasPagamento(cliente, pedido.getValorTotal());

						break;
					}
				}
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar tipo de frete, contate o administrador", t);
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

	// Condicoes
	public boolean isFormaPagamentoSelecionada() {
		return null != sequencialFormaPagamento && sequencialFormaPagamento >= 0;
	}

	public boolean isTipoFreteSelecionado() {
		return StringUtils.isNotEmpty(codigoServicoFrete);
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

	public List<TipoFrete> getTiposFrete() {
		return tiposFrete;
	}

	public JanelaCompra getJanelaCompra() {
		return janelaCompra;
	}

	public String getMensagemJanelaCompra() {
		return mensagemJanelaCompra;
	}

	public String getMensagemConclusaoPedido() {
		return mensagemConclusaoPedido;
	}

	public Integer getSequencialFormaPagamento() {
		return sequencialFormaPagamento;
	}

	public void setSequencialFormaPagamento(Integer sequencialFormaPagamento) {
		this.sequencialFormaPagamento = sequencialFormaPagamento;
	}

	public String getCodigoServicoFrete() {
		return codigoServicoFrete;
	}

	public void setCodigoServicoFrete(String codigoServicoFrete) {
		this.codigoServicoFrete = codigoServicoFrete;
	}

	public Integer getPasso() {
		return passo;
	}
}