package br.com.graflogic.hermitex.cliente.web.controller.cotacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomPermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.hermitex.cliente.data.dom.DomCotacao.DomPedidoFaturado;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomServicoFrete;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatus;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuarioAdministrador;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.Cotacao;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoEnderecoPK;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoItem;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoSimple;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoCor;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoImagem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.service.exception.CorreiosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PerfilUsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.UsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.EstadoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.MunicipioService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.frete.FreteService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.cotacao.CotacaoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.FormaPagamentoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;
import br.com.graflogic.hermitex.cliente.service.model.pedido.TipoFrete;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class CotacaoController extends CrudBaseController<CotacaoSimple, Cotacao> implements InitializingBean {

	private static final long serialVersionUID = -1506916665774731904L;

	@Autowired
	private CotacaoService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private EstadoService estadoService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private FormaPagamentoService formaPagamentoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PerfilUsuarioService perfilUsuarioService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private FreteService freteService;

	private List<Cliente> clientes;

	private List<Filial> filiais;

	private List<FormaPagamento> formasPagamento;

	private List<TipoFrete> tiposFrete;

	private List<Estado> estados;

	private List<Municipio> municipiosFaturamento;

	private List<Municipio> municipiosEntrega;

	private CotacaoSimple entitySimple;

	private CotacaoEndereco enderecoFaturamento;

	private CotacaoEndereco enderecoEntrega;

	private FormaPagamento formaPagamento;

	private TipoFrete tipoFrete;

	private String codigoServicoFrete;

	private BigDecimal porcentagemDescontoLivre;

	private BigDecimal porcentagemDescontoEspecial;

	private BigDecimal porcentagemDescontoGerencial;

	private String observacao;

	private Cliente cliente;

	private Filial filial;

	private CotacaoItem item;

	private List<Produto> produtos;

	private Produto produto;

	private Usuario usuarioCadastro;

	private String servicoFrete;

	private PerfilUsuarioAdministrador perfilUsuario;

	private int indexRelacionado;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new CotacaoSimple());

			filiais = new ArrayList<>();

			clientes = clienteService.consulta(new Cliente());

			estados = estadoService.consulta();
			municipiosFaturamento = new ArrayList<>();
			municipiosEntrega = new ArrayList<>();
			produtos = new ArrayList<>();

			perfilUsuario = (PerfilUsuarioAdministrador) perfilUsuarioService.consultaPorId(SessionUtil.getAuthenticatedUsuario().getIdPerfil());

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", e);
		}
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a coleção", null);
			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	@Override
	protected boolean executeSave() {
		try {
			enderecoFaturamento.setCotacao(getEntity());
			enderecoEntrega.setCotacao(getEntity());

			getEntity().getEnderecos().clear();
			getEntity().getEnderecos().add(enderecoFaturamento);
			getEntity().getEnderecos().add(enderecoEntrega);

			if (isEditing()) {
				try {
					// TODO

					returnInfoMessage("Cotação atualizada com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				service.cadastra(getEntity(), SessionUtil.getAuthenticatedUsuario().getId());

				returnInfoMessage("Cotação cadastrada com sucesso", null);

			}
		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);
			return false;
		}

		setEntity(null);
		setEditing(false);

		search();

		return false;
	}

	@Override
	protected void beforeAdd() {
		setEntity(new Cotacao());
		getEntity().setItens(new ArrayList<>());
		getEntity().setEnderecos(new ArrayList<>());
		getEntity().setFretes(new ArrayList<>());
		getEntity().setIdCliente(getFilterEntity().getIdCliente());
		getEntity().setValorDescontoLivre(BigDecimal.ZERO);
		getEntity().setValorDescontoEspecial(BigDecimal.ZERO);
		getEntity().setValorDescontoGerencial(BigDecimal.ZERO);
		getEntity().setValorFrete(BigDecimal.ZERO);
		getEntity().setPedidoFaturado(DomBoolean.SIM);

		porcentagemDescontoLivre = BigDecimal.ZERO;
		porcentagemDescontoEspecial = BigDecimal.ZERO;
		porcentagemDescontoGerencial = BigDecimal.ZERO;

		codigoServicoFrete = null;

		formasPagamento = new ArrayList<>();
		tiposFrete = new ArrayList<>();

		atualizaCotacao();

		enderecoEntrega = new CotacaoEndereco();
		enderecoFaturamento = new CotacaoEndereco();

		formaPagamento = null;
		tipoFrete = null;

		filial = null;
		servicoFrete = null;
	}

	@Override
	protected void executeEdit(CotacaoSimple entity) throws Exception {
		entitySimple = entity;

		select(entity.getId());

		// Consulta o usuario de cadastro
		usuarioCadastro = usuarioService.consultaPorId(entity.getIdUsuarioCadastro());
	}

	private void select(Long id) {
		setEntity(service.consultaCompletoPorId(id));

		municipiosFaturamento.clear();
		municipiosEntrega.clear();

		enderecoFaturamento = getEntity().getEnderecoFaturamento();
		enderecoEntrega = getEntity().getEnderecoEntrega();

		municipiosFaturamento.addAll(municipioService.consulta(enderecoFaturamento.getSiglaEstado()));
		municipiosEntrega.addAll(municipioService.consulta(enderecoEntrega.getSiglaEstado()));

		formaPagamento = formaPagamentoService.consultaPorId(getEntity().getIdFormaPagamento());

		cliente = clienteService.consultaPorId(getEntity().getIdCliente());

		if (null != getEntity().getIdFilial() && 0 != getEntity().getIdFilial()) {
			filial = filialService.consultaPorId(getEntity().getIdFilial());
		} else {
			filial = null;
		}

		servicoFrete = null;

		if (!getEntity().getFretes().isEmpty()) {
			servicoFrete = DomPedido.domServicoFrete.getDeValor(getEntity().getFretes().get(0).getCodigoServico());
		}
	}

	@Override
	protected void executeEditRelated(Object relatedEntity) throws Exception {
		if (relatedEntity instanceof CotacaoItem) {
			item = (CotacaoItem) ObjectCopier.copy(relatedEntity);

		}
	}

	// Produto
	public void prepareAddItem() {
		try {
			setEditingRelated(false);

			item = new CotacaoItem();
			item.setValorTotal(BigDecimal.ZERO);
			item.setPesoTotal(BigDecimal.ZERO);
			item.setQuantidade(1);

			showDialog("itemDialog");

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao adicionar produto, contate o administrador", e);
		}
	}

	public void saveItem() {
		try {
			if (isEditingRelated()) {
				getEntity().getItens().set(indexRelacionado, item);
			} else {
				getEntity().getItens().add(item);
			}

			atualizaCotacao();

			tiposFrete.clear();

			updateComponent("editForm:dtbItens");
			updateComponent("editForm:pagamentoGrid");
			updateComponent("editForm:descontoGrid");
			updateComponent("editForm:freteGrid");

			hideDialog("itemDialog");

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao salvar produto, contate o administrador", e);
		}
	}

	public void removeItem() {
		getEntity().getItens().remove(indexRelacionado);

		atualizaCotacao();

		tiposFrete.clear();

		updateComponent("editForm:dtbItens");
		updateComponent("editForm:pagamentoGrid");
		updateComponent("editForm:descontoGrid");
		updateComponent("editForm:freteGrid");

		hideDialog("itemDialog");
	}

	public void changeProduto() {
		try {
			if (null != item.getIdProduto()) {
				produto = produtoService.consultaCompletoPorId(item.getIdProduto());

				item.setIdProduto(produto.getId());
				item.setCodigoProduto(produto.getCodigo());
				item.setTituloProduto(produto.getTitulo());
				item.setSkuProduto(produto.getSku());
				item.setValorUnitario(produto.getValor());
				item.setCodigoTamanho(produto.getTamanhos().get(0).getId().getCodigoTamanho());

				for (ProdutoImagem imagem : produto.getImagens()) {
					if (imagem.isCapa()) {
						item.setIdImagemCapaProduto(imagem.getId());
						break;
					}
				}

				changeTamanhoProduto();

				calculaTotalItem();
			}

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar o produto, contate o administrador", e);
		}
	}

	public void changeTamanhoProduto() {
		try {
			item.setValorCorrigidoTamanho(produto.getValor());

			if (StringUtils.isNotEmpty(item.getCodigoTamanho())) {
				BigDecimal fator = null;

				for (ProdutoTamanho tamanho : produto.getTamanhos()) {
					if (tamanho.getId().getCodigoTamanho().equals(item.getCodigoTamanho())) {
						fator = tamanho.getFator();
						break;
					}
				}

				item.setValorCorrigidoTamanho(item.getValorCorrigidoTamanho().multiply(fator).setScale(2, RoundingMode.HALF_EVEN));

				calculaTotalItem();
			}
		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar tamanho do produto, contate o administrador", e);
		}
	}

	public void changeCorProduto() {
		try {
			item.setNomeCor(null);

			if (StringUtils.isNotEmpty(item.getCodigoCor())) {
				for (ProdutoCor cor : produto.getCores()) {
					if (cor.getId().getCodigoCor().equals(item.getCodigoCor())) {
						item.setNomeCor(cor.getNomeCor());
						break;
					}
				}
			}

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar tamanho do produto, contate o administrador", e);
		}
	}

	public void changeQuantidadeProduto() {
		try {
			calculaTotalItem();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar a quantidade do produto, contate o administrador", e);
		}
	}

	public void changeFormaPagamento() {
		try {
			this.formaPagamento = null;

			atualizaFormaPagamento();

			atualizaValores();

			changePedidoFaturado();

			updateComponent("editForm:descontoGrid");
			updateComponent("editForm:freteGrid");
			updateComponent("editForm:faturamentoGrid");

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar a forma de pagamento, contate o administrador", e);
		}
	}

	private void atualizaFormaPagamento() {
		if (null != getEntity().getIdFormaPagamento() && 0 != getEntity().getIdFormaPagamento()) {
			for (FormaPagamento formaPagamento : formasPagamento) {
				if (getEntity().getIdFormaPagamento().equals(formaPagamento.getId())) {
					this.formaPagamento = formaPagamento;
					break;
				}
			}

			if (!DomPedidoFaturado.TODOS.equals(formaPagamento.getPedidoFaturado())) {
				getEntity().setPedidoFaturado(formaPagamento.getPedidoFaturado());
			}
		}
	}

	public void changeTipoFrete() {
		try {
			getEntity().getFretes().clear();
			getEntity().setValorFrete(BigDecimal.ZERO);

			formasPagamento.clear();

			getEntity().setIdFormaPagamento(null);

			if (StringUtils.isNotBlank(codigoServicoFrete)) {
				for (TipoFrete tipoFrete : tiposFrete) {
					if (codigoServicoFrete.equals(tipoFrete.getCodigoServico())) {
						getEntity().getFretes().addAll(tipoFrete.getFretesCotacao());
						getEntity().setValorFrete(tipoFrete.getValor());
						servicoFrete = DomPedido.domServicoFrete.getDeValor(codigoServicoFrete);
						this.tipoFrete = tipoFrete;

						break;
					}
				}
			}

			changeFormaPagamento();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar tipo de frete, contate o administrador", e);
		}
	}

	public void changeValorFrete() {
		try {
			atualizaValores();

			if (null != tipoFrete && DomServicoFrete.TRANSPORTADORA.equals(tipoFrete.getCodigoServico())) {
				getEntity().getFretes().get(0).setValor(getEntity().getValorFrete());
			}

			updateComponent("editForm:pagamentoGrid");

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar valor do frete, contate o administrador", e);
		}
	}

	private void calculaTotalItem() {
		item.setValorTotal(BigDecimal.ZERO);
		item.setPesoTotal(BigDecimal.ZERO);

		if (item.getQuantidade() > 0) {
			item.setValorTotal(item.getValorCorrigidoTamanho().multiply(new BigDecimal(item.getQuantidade())));
			item.setPesoTotal(produto.getPeso().multiply(new BigDecimal(item.getQuantidade())));
		}
	}

	// Desconto
	public void changePorcentagemDescontoLivre() {
		try {
			if (null != porcentagemDescontoLivre && porcentagemDescontoLivre.compareTo(perfilUsuario.getPorcentagemDescontoLivreMaximo()) > 0) {
				porcentagemDescontoLivre = BigDecimal.ZERO;
				returnWarnDialogMessage("Aviso", "Desconto livre superior ao permitido", null);
			}

			changeDesconto();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar porcentagem de desconto livre, contate o administrador", e);
		}
	}

	private void calculaDescontoLivre() {
		if (null != porcentagemDescontoLivre && BigDecimal.ZERO.compareTo(porcentagemDescontoLivre) < 0
				&& BigDecimal.ZERO.compareTo(getEntity().getValorProdutos()) < 0) {
			getEntity().setValorDescontoLivre(
					getEntity().getValorProdutos().multiply(porcentagemDescontoLivre).divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_EVEN));
		} else {
			getEntity().setValorDescontoLivre(BigDecimal.ZERO);
		}

		getEntity().setValorTotal(getEntity().getValorTotal().subtract(getEntity().getValorDescontoLivre()));
	}

	public void changePorcentagemDescontoEspecial() {
		try {
			if (null != porcentagemDescontoEspecial
					&& porcentagemDescontoEspecial.compareTo(perfilUsuario.getPorcentagemDescontoEspecialMaximo()) > 0) {
				porcentagemDescontoEspecial = BigDecimal.ZERO;
				returnWarnDialogMessage("Aviso", "Desconto especial superior ao permitido", null);
			}

			changeDesconto();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar porcentagem de desconto especial, contate o administrador", e);
		}
	}

	private void calculaDescontoEspecial() {
		if (!isDescontoEspecialEditavel()) {
			porcentagemDescontoEspecial = BigDecimal.ZERO;
		}

		if (null != porcentagemDescontoEspecial && BigDecimal.ZERO.compareTo(porcentagemDescontoEspecial) < 0
				&& BigDecimal.ZERO.compareTo(getEntity().getValorProdutos()) < 0) {
			getEntity().setValorDescontoEspecial(getEntity().getValorProdutos().multiply(porcentagemDescontoEspecial)
					.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_EVEN));
		} else {
			getEntity().setValorDescontoEspecial(BigDecimal.ZERO);
		}

		getEntity().setValorTotal(getEntity().getValorTotal().subtract(getEntity().getValorDescontoEspecial()));
	}

	public void changePorcentagemDescontoGerencial() {
		try {
			if (null != porcentagemDescontoGerencial
					&& porcentagemDescontoGerencial.compareTo(perfilUsuario.getPorcentagemDescontoGerencialMaximo()) > 0) {
				porcentagemDescontoGerencial = BigDecimal.ZERO;
				returnWarnDialogMessage("Aviso", "Desconto gerencial superior ao permitido", null);
			}

			changeDesconto();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar porcentagem de desconto gerencial, contate o administrador", e);
		}
	}

	private void calculaDescontoGerencial() {
		if (null != porcentagemDescontoGerencial && BigDecimal.ZERO.compareTo(porcentagemDescontoGerencial) < 0
				&& BigDecimal.ZERO.compareTo(getEntity().getValorProdutos()) < 0) {
			getEntity().setValorDescontoGerencial(getEntity().getValorProdutos().multiply(porcentagemDescontoGerencial)
					.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_EVEN));
		} else {
			getEntity().setValorDescontoGerencial(BigDecimal.ZERO);
		}

		getEntity().setValorTotal(getEntity().getValorTotal().subtract(getEntity().getValorDescontoGerencial()));
	}

	private void changeDesconto() {
		atualizaValores();

		updateComponent("editForm:freteGrid");
		updateComponent("editForm:pagamentoGrid");
		updateComponent("editForm:descontoGrid");
	}

	private void calculaDescontoFormaPagamento() {
		if (null != formaPagamento) {
			getEntity().setDescricaoFormaPagamento(formaPagamento.getDescricao());
			getEntity().setValorDescontoPagamento(getEntity().getValorProdutos().multiply(formaPagamento.getPorcentagemDesconto())
					.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_EVEN));
		} else {
			getEntity().setValorDescontoPagamento(BigDecimal.ZERO);
		}

		getEntity().setValorTotal(getEntity().getValorTotal().subtract(getEntity().getValorDescontoPagamento()));
	}

	// Fluxo
	public void finaliza() {
		try {
			service.finaliza(getEntity(), SessionUtil.getAuthenticatedUsuario().getId(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Cotação marcada como finalizada com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Cotação com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar cotação como finalizada, contate o administrador", e);
		}
	}

	public void cancela() {
		try {
			service.cancela(getEntity(), SessionUtil.getAuthenticatedUsuario().getId(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Cotação marcada como cancelada com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Cotação com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar cotação como cancelada, contate o administrador", e);
		}
	}

	private void afterOperacao() {
		select(getEntity().getId());
		executeSearch();

		updateComponent("editForm");
		updateComponent("searchForm");
		observacao = null;
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);
			getFilterEntity().setIdFilial(null);

			produtos.clear();

			if (null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente()) {
				cliente = clienteService.consultaPorId(getFilterEntity().getIdCliente());

				filiais = filialService.consultaPorCliente(getFilterEntity().getIdCliente(), true);

				Produto produtoFilter = new Produto();
				produtoFilter.setIdCliente(getFilterEntity().getIdCliente());
				if (!isEditing()) {
					produtoFilter.setStatus(DomStatus.ATIVO);
				}
				produtos.addAll(produtoService.consulta(produtoFilter));
			}

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados da coleção, contate o administrador", e);
		}
	}

	public void changeFilial() {
		try {
			if (null != getEntity().getIdFilial() && null != getEntity()) {
				filial = filialService.consultaPorId(getEntity().getIdFilial());

				enderecoFaturamento = new CotacaoEndereco(new CotacaoEnderecoPK(null, DomTipoEndereco.FATURAMENTO));
				enderecoFaturamento.setSiglaEstado(filial.getEnderecoFaturamento().getSiglaEstado());
				enderecoFaturamento.setIdMunicipio(filial.getEnderecoFaturamento().getIdMunicipio());
				enderecoFaturamento.setCep(filial.getEnderecoFaturamento().getCep());
				enderecoFaturamento.setBairro(filial.getEnderecoFaturamento().getBairro());
				enderecoFaturamento.setLogradouro(filial.getEnderecoFaturamento().getLogradouro());
				enderecoFaturamento.setNumero(filial.getEnderecoFaturamento().getNumero());
				enderecoFaturamento.setComplemento(filial.getEnderecoFaturamento().getComplemento());

				enderecoEntrega = new CotacaoEndereco(new CotacaoEnderecoPK(null, DomTipoEndereco.ENTREGA));
				enderecoEntrega.setSiglaEstado(filial.getEnderecoEntrega().getSiglaEstado());
				enderecoEntrega.setIdMunicipio(filial.getEnderecoEntrega().getIdMunicipio());
				enderecoEntrega.setCep(filial.getEnderecoEntrega().getCep());
				enderecoEntrega.setBairro(filial.getEnderecoEntrega().getBairro());
				enderecoEntrega.setLogradouro(filial.getEnderecoEntrega().getLogradouro());
				enderecoEntrega.setNumero(filial.getEnderecoEntrega().getNumero());
				enderecoEntrega.setComplemento(filial.getEnderecoEntrega().getComplemento());
			}

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", e);
		}
	}

	public void changePedidoFaturado() {
		try {
			changePorcentagemDescontoLivre();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar pedido faturado, contate o administrador", e);
		}
	}

	private void atualizaCotacao() {
		atualizaItens();

		atualizaValores();
	}

	private void atualizaValores() {
		getEntity().setValorTotal(getEntity().getValorProdutos());

		calculaDescontoLivre();
		calculaDescontoEspecial();
		calculaDescontoGerencial();

		getEntity().setValorTotal(getEntity().getValorTotal().add(getEntity().getValorFrete()));
		getEntity().setValorTotal(getEntity().getValorTotal().setScale(2, RoundingMode.HALF_EVEN));

		atualizaFormasPagamento();

		calculaDescontoFormaPagamento();
	}

	private void atualizaItens() {
		getEntity().setValorProdutos(BigDecimal.ZERO);
		getEntity().setPesoTotal(BigDecimal.ZERO);
		getEntity().setQuantidadeTotalItens(0);

		for (CotacaoItem cotacaoItem : getEntity().getItens()) {
			getEntity().setValorProdutos(getEntity().getValorProdutos().add(cotacaoItem.getValorTotal()));
			getEntity().setPesoTotal(getEntity().getPesoTotal().add(cotacaoItem.getPesoTotal()));
			getEntity().setQuantidadeTotalItens(getEntity().getQuantidadeTotalItens() + cotacaoItem.getQuantidade());
		}
	}

	public void atualizaFormasPagamento() {
		try {
			formasPagamento = formaPagamentoService.gera(getEntity());

			atualizaFormaPagamento();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao atualizar formas de pagamento, contate o administrador", e);
		}
	}

	public void atualizaTiposFrete() {
		try {
			tiposFrete.clear();

			getEntity().getEnderecos().clear();

			if (null != enderecoEntrega) {
				getEntity().getEnderecos().add(enderecoEntrega);
			}

			try {
				tiposFrete.addAll(freteService.geraTiposCorreios(getEntity()));
			} catch (CorreiosException e) {
				returnFatalDialogMessage(I18NUtil.getLabel("erro"),
						"Cálculo de frete dos Correios indisponível, tente novamente mais tarde ou selecione outro tipo", e);
			}

			tiposFrete.add(freteService.geraTipoRetirada());
			tiposFrete.add(freteService.geraTipoTransportadora());

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao atualizar tipos de frete, contate o administrador", e);
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	public boolean isFinalizavel() {
		return null != getEntity() && getEntity().isGerada() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_COTACAO_FINALIZACAO);
	}

	public boolean isCancelavel() {
		return null != getEntity() && (getEntity().isGerada()) && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_COTACAO_CANCELAMENTO);
	}

	public boolean isPesquisavel() {
		return isClienteSelecionado();
	}

	public boolean isValorFreteEditavel() {
		return !isEditing() && null != tipoFrete && tipoFrete.isValorFreteManual();
	}

	public boolean isDescontoLivreEditavel() {
		return !isEditing() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_COTACAO_DESCONTO_LIVRE);
	}

	public boolean isDescontoEspecialEditavel() {
		return !isEditing() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_COTACAO_DESCONTO_ESPECIAL)
				&& DomBoolean.NAO.equals(getEntity().getPedidoFaturado()) && null != formaPagamento
				&& DomBoolean.SIM.equals(formaPagamento.getDescontoEspecial());
	}

	public boolean isDescontoGerencialEditavel() {
		return !isEditing() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_COTACAO_DESCONTO_GERENCIAL);
	}

	// Getters e Setters
	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<Filial> getFiliais() {
		return filiais;
	}

	public List<FormaPagamento> getFormasPagamento() {
		return formasPagamento;
	}

	public List<TipoFrete> getTiposFrete() {
		return tiposFrete;
	}

	public List<Estado> getEstados() {
		return estados;
	}

	public List<Municipio> getMunicipiosFaturamento() {
		return municipiosFaturamento;
	}

	public List<Municipio> getMunicipiosEntrega() {
		return municipiosEntrega;
	}

	public CotacaoSimple getEntitySimple() {
		return entitySimple;
	}

	public CotacaoEndereco getEnderecoFaturamento() {
		return enderecoFaturamento;
	}

	public CotacaoEndereco getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public String getCodigoServicoFrete() {
		return codigoServicoFrete;
	}

	public void setCodigoServicoFrete(String codigoServicoFrete) {
		this.codigoServicoFrete = codigoServicoFrete;
	}

	public BigDecimal getPorcentagemDescontoLivre() {
		return porcentagemDescontoLivre;
	}

	public void setPorcentagemDescontoLivre(BigDecimal porcentagemDescontoLivre) {
		this.porcentagemDescontoLivre = porcentagemDescontoLivre;
	}

	public BigDecimal getPorcentagemDescontoEspecial() {
		return porcentagemDescontoEspecial;
	}

	public void setPorcentagemDescontoEspecial(BigDecimal porcentagemDescontoEspecial) {
		this.porcentagemDescontoEspecial = porcentagemDescontoEspecial;
	}

	public BigDecimal getPorcentagemDescontoGerencial() {
		return porcentagemDescontoGerencial;
	}

	public void setPorcentagemDescontoGerencial(BigDecimal porcentagemDescontoGerencial) {
		this.porcentagemDescontoGerencial = porcentagemDescontoGerencial;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Filial getFilial() {
		return filial;
	}

	public CotacaoItem getItem() {
		return item;
	}

	public List<Produto> getProdutos() {
		return produtos;
	}

	public Produto getProduto() {
		return produto;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public String getServicoFrete() {
		return servicoFrete;
	}

	public PerfilUsuario getPerfilUsuario() {
		return perfilUsuario;
	}

	public int getIndexRelacionado() {
		return indexRelacionado;
	}

	public void setIndexRelacionado(int indexRelacionado) {
		this.indexRelacionado = indexRelacionado;
	}

}