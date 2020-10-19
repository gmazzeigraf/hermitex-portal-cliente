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
import br.com.graflogic.hermitex.cliente.data.dom.DomCadastro.DomTipoEndereco;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomPermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomServicoFrete;
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
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoImagem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.service.exception.CorreiosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
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

	private String observacao;

	private Cliente cliente;

	private Filial filial;

	private CotacaoItem item;

	private List<Produto> produtos;

	private Produto produto;

	private Usuario usuarioCadastro;

	private String servicoFrete;

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

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", e);
		}
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar o cliente", null);
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
		getEntity().setValorFrete(BigDecimal.ZERO);
		getEntity().setPedidoFaturado(DomBoolean.SIM);

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
			updateComponent("editForm:informacoesGrid");
			updateComponent("editForm:pagamentoGrid");
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
		updateComponent("editForm:informacoesGrid");
		updateComponent("editForm:pagamentoGrid");
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

			if (null != getEntity().getIdFormaPagamento() && 0 != getEntity().getIdFormaPagamento()) {
				for (FormaPagamento formaPagamento : formasPagamento) {
					if (getEntity().getIdFormaPagamento().equals(formaPagamento.getId())) {
						this.formaPagamento = formaPagamento;
						break;
					}
				}
			}

			atualizaCotacao();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar a forma de pagamento, contate o administrador", e);
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
			atualizaCotacao();

			if (null != tipoFrete && DomServicoFrete.TRANSPORTADORA.equals(tipoFrete.getCodigoServico())) {
				getEntity().getFretes().get(0).setValor(getEntity().getValorFrete());
			}

			updateComponent("editForm:pagamentoGrid");

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar valor do frete, contate o administrador", e);
		}
	}

	public void changeDescontoLivre() {
		try {
			// TODO Valida valor do desconto pelo perfil

			atualizaCotacao();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar o desconto livre, contate o administrador", e);
		}
	}

	public void changeDescontoEspecial() {
		try {
			// TODO Valida valor do desconto pelo perfil

			atualizaCotacao();

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar o desconto livre, contate o administrador", e);
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
				produtos.addAll(produtoService.consulta(produtoFilter));
			}

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", e);
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
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados da filial, contate o administrador", e);
		}
	}

	public void changePedidoFaturado() {
		try {

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar pedido faturado, contate o administrador", e);
		}
	}

	private void atualizaCotacao() {
		formasPagamento.clear();

		getEntity().setValorProdutos(BigDecimal.ZERO);
		getEntity().setValorDescontoPagamento(BigDecimal.ZERO);

		getEntity().setValorTotal(BigDecimal.ZERO);
		getEntity().setPesoTotal(BigDecimal.ZERO);
		getEntity().setQuantidadeTotalItens(0);

		for (CotacaoItem cotacaoItem : getEntity().getItens()) {
			getEntity().setValorProdutos(getEntity().getValorProdutos().add(cotacaoItem.getValorTotal()));
			getEntity().setPesoTotal(getEntity().getPesoTotal().add(cotacaoItem.getPesoTotal()));
			getEntity().setQuantidadeTotalItens(getEntity().getQuantidadeTotalItens() + cotacaoItem.getQuantidade());
		}

		getEntity().setValorTotal(getEntity().getValorProdutos());
		getEntity().setValorTotal(getEntity().getValorTotal().subtract(getEntity().getValorDescontoLivre()));
		getEntity().setValorTotal(getEntity().getValorTotal().subtract(getEntity().getValorDescontoEspecial()));

		if (null != formaPagamento) {
			getEntity().setDescricaoFormaPagamento(formaPagamento.getDescricao());
			getEntity().setValorDescontoPagamento(getEntity().getValorProdutos().multiply(formaPagamento.getPorcentagemDesconto())
					.divide(new BigDecimal("100"), 2, RoundingMode.HALF_EVEN));
		}

		getEntity().setValorTotal(getEntity().getValorTotal().add(getEntity().getValorFrete()));
		getEntity().setValorTotal(getEntity().getValorTotal().subtract(getEntity().getValorDescontoPagamento()));
		getEntity().setValorTotal(getEntity().getValorTotal().setScale(2, RoundingMode.HALF_EVEN));

		atualizaFormasPagamento();
	}

	public void atualizaFormasPagamento() {
		try {
			formasPagamento = formaPagamentoService.gera(getEntity());

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
		// TODO
		return false;
//		return null != getEntity() && getEntity().isGerada() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_COTACAO_FINALIZACAO);
	}

	public boolean isCancelavel() {
		// TODO
		return false;
//		return null != getEntity() && (getEntity().isGerada()) && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_COTACAO_CANCELAMENTO);
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
		return !isEditing() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_COTACAO_DESCONTO_ESPECIAL);
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

	public int getIndexRelacionado() {
		return indexRelacionado;
	}

	public void setIndexRelacionado(int indexRelacionado) {
		this.indexRelacionado = indexRelacionado;
	}

}