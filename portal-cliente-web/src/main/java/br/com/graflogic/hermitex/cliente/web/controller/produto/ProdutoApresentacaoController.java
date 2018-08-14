package br.com.graflogic.hermitex.cliente.web.controller.produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CategoriaProduto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoApresentacaoLista;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.data.entity.produto.SetorProduto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.TamanhoProduto;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.CategoriaProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.ProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.SetorProdutoService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.TamanhoProdutoService;
import br.com.graflogic.hermitex.cliente.web.controller.pedido.CarrinhoController;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.presentationutil.controller.SearchBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class ProdutoApresentacaoController extends SearchBaseController<ProdutoApresentacaoLista, Produto> implements InitializingBean {

	private static final long serialVersionUID = -1972295739395287005L;

	@Autowired
	private ProdutoService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private TamanhoProdutoService tamanhoService;

	@Autowired
	private CategoriaProdutoService categoriaService;

	@Autowired
	private SetorProdutoService setorService;

	@Autowired
	private CarrinhoController carrinhoController;

	private List<Cliente> clientes;

	private List<TamanhoProduto> tamanhos;

	private List<CategoriaProduto> categorias;

	private List<SetorProduto> setores;

	private PedidoItem itemPedido;

	private ProdutoApresentacaoLista produtoApresentacao;

	private String tabelaMedidas;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new ProdutoApresentacaoLista());

			tamanhos = tamanhoService.consulta(true);
			categorias = new ArrayList<>();
			setores = new ArrayList<>();

			if (SessionUtil.isUsuarioAdministrador()) {
				clientes = clienteService.consultaAtivos();

			} else if (SessionUtil.isUsuarioCliente() || SessionUtil.isUsuarioFilial()) {
				getFilterEntity().setIdCliente(SessionUtil.getIdCliente());
			}

			if (null != getFilterEntity().getIdCliente()) {
				changeCliente();

				search();
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected void executeSearch() {
		setEntities(service.consultaApresentacaoLista(getFilterEntity()));
	}

	@Override
	protected void select(ProdutoApresentacaoLista entity) {
		setEntity(service.consultaCompletoPorId(entity.getId()));

		this.produtoApresentacao = entity;

		this.tabelaMedidas = service.geraTabelaMedidas(entity.getId());

		preparaNovoItem();
	}

	public void adicionaItem() {
		try {
			// Adiciona o item no carrinho
			carrinhoController.adicionaItem(itemPedido);

			preparaNovoItem();

			returnInfoMessage("Item adicionado com sucesso", null);

			updateComponent("editForm:messagesGrowl");

		} catch (DadosInvalidosException e) {
			returnWarnMessage(e.getMessage(), null, null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao adicionar item, contate o administrador", t);
		}
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);
			categorias.clear();
			setores.clear();

			if (isClienteSelecionado()) {
				search();
				categorias.addAll(categoriaService.consultaPorCliente(getFilterEntity().getIdCliente(), true));
				setores.addAll(setorService.consultaPorCliente(getFilterEntity().getIdCliente(), true));
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", t);
		}
	}

	public void changeTamanho() {
		try {
			itemPedido.setValorCorrigidoTamanho(itemPedido.getValorUnitario());

			// Caso o tamanho esteja selecionado calcula o valor pelo fato
			if (StringUtils.isNotEmpty(itemPedido.getCodigoTamanho())) {
				BigDecimal fator = null;

				for (ProdutoTamanho tamanho : getEntity().getTamanhos()) {
					if (tamanho.getId().getCodigoTamanho().equals(itemPedido.getCodigoTamanho())) {
						fator = tamanho.getFator();
						break;
					}
				}

				itemPedido.setValorCorrigidoTamanho(itemPedido.getValorCorrigidoTamanho().multiply(fator).setScale(2, RoundingMode.HALF_EVEN));
			}

			calculaTotalItem();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar tamanho do produto, contate o administrador", t);
		}
	}

	private void preparaNovoItem() {
		itemPedido = new PedidoItem();
		itemPedido.setIdProduto(getEntity().getId());
		itemPedido.setValorUnitario(getEntity().getValor());
		itemPedido.setCodigoTamanho(getEntity().getTamanhos().get(0).getId().getCodigoTamanho());
		itemPedido.setQuantidade(1);
		itemPedido.setCodigoProduto(getEntity().getCodigo());
		itemPedido.setTituloProduto(getEntity().getTitulo());
		itemPedido.setIdImagemCapaProduto(produtoApresentacao.getIdImagemCapa());

		changeTamanho();

		calculaTotalItem();
	}

	public void aumentaQuantidade() {
		try {
			if (itemPedido.getQuantidade() >= 999) {
				return;
			}

			itemPedido.setQuantidade(itemPedido.getQuantidade() + 1);

			calculaTotalItem();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao aumentar quantidade, contate o administrador", t);
		}
	}

	public void diminuiQuantidade() {
		try {
			if (itemPedido.getQuantidade() <= 1) {
				return;
			}

			itemPedido.setQuantidade(itemPedido.getQuantidade() - 1);

			calculaTotalItem();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao diminuir quantidade, contate o administrador", t);
		}
	}

	public void calculaTotalItem() {
		itemPedido.setValorTotal(BigDecimal.ZERO);
		itemPedido.setPesoTotal(BigDecimal.ZERO);

		if (itemPedido.getQuantidade() > 0) {
			itemPedido.setValorTotal(itemPedido.getValorCorrigidoTamanho().multiply(new BigDecimal(itemPedido.getQuantidade())));
			itemPedido.setPesoTotal(getEntity().getPeso().multiply(new BigDecimal(itemPedido.getQuantidade())));
		}
	}

	public StreamedContent getFitaMetrica() {
		try {
			return new DefaultStreamedContent(this.getClass().getClassLoader().getResourceAsStream("web/arquivos/fitametrica.pdf"), "",
					"fitametrica.pdf");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao realizar download da fita métrica, contate o administrador", t);
			return null;
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	// Getters e Setters
	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<CategoriaProduto> getCategorias() {
		return categorias;
	}

	public List<SetorProduto> getSetores() {
		return setores;
	}

	public List<TamanhoProduto> getTamanhos() {
		return tamanhos;
	}

	public PedidoItem getItemPedido() {
		return itemPedido;
	}

	public String getTabelaMedidas() {
		return tabelaMedidas;
	}
}