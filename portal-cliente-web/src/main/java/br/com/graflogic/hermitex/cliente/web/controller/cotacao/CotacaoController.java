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
import br.com.graflogic.hermitex.cliente.data.dom.DomCotacao;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.Cotacao;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoEndereco;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoItem;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoSimple;
import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.UsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.EstadoService;
import br.com.graflogic.hermitex.cliente.service.impl.auxiliar.MunicipioService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.cotacao.CotacaoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.FormaPagamentoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
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

	private List<Cliente> clientes;

	private List<Filial> filiais;

	private List<Estado> estados;

	private List<Municipio> municipiosFaturamento;

	private List<Municipio> municipiosEntrega;

	private CotacaoSimple entitySimple;

	private CotacaoEndereco enderecoFaturamento;

	private CotacaoEndereco enderecoEntrega;

	private FormaPagamento formaPagamento;

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
		return false;
	}

	@Override
	protected void beforeAdd() {
		setEntity(new Cotacao());
		getEntity().setItens(new ArrayList<>());
		getEntity().setEnderecos(new ArrayList<>());
		getEntity().setFretes(new ArrayList<>());

		getEntity().setIdCliente(getFilterEntity().getIdCliente());

		enderecoEntrega = new CotacaoEndereco();
		enderecoFaturamento = new CotacaoEndereco();
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
			servicoFrete = DomCotacao.domServicoFrete.getDeValor(getEntity().getFretes().get(0).getCodigoServico());
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

			updateComponent("editForm:dtbItens");
			hideDialog("itemDialog");

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao salvar produto, contate o administrador", e);
		}
	}

	public void changeProduto() {
		try {
			if (null != item.getIdProduto()) {
				produto = produtoService.consultaCompletoPorId(item.getIdProduto());

				item.setIdProduto(produto.getId());
				item.setCodigoProduto(produto.getCodigo());
				item.setTituloProduto(produto.getTitulo());
				item.setValorUnitario(produto.getValor());
				item.setValorCorrigidoTamanho(produto.getValor());

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
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao quantidade do produto, contate o administrador", e);
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

				filiais = filialService.consultaPorCliente(getFilterEntity().getIdCliente(), false);

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

		} catch (Exception e) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", e);
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

	// Getters e Setters
	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<Filial> getFiliais() {
		return filiais;
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