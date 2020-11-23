package br.com.graflogic.hermitex.cliente.web.controller.pedido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomPermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Troca;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.TrocaItem;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.hermitex.cliente.data.enums.ParametroClienteEnum;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.PedidoService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.TrocaService;
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
public class TrocaController extends CrudBaseController<Troca, Troca> implements InitializingBean {

	private static final long serialVersionUID = -8717653202903739039L;

	private static final String VIEW_SOLICITACAO = "troca/solicitacao";

	private static final String VIEW_CONSULTA = "troca/consulta";

	@Autowired
	private TrocaService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private ProdutoService produtoService;

	private List<PedidoSimple> pedidos;

	private List<Cliente> clientes;

	private List<Filial> filiais;

	private String observacao;

	private String politicaTroca;

	private HashMap<Integer, List<ProdutoTamanho>> tamanhos;

	private Cliente cliente;

	private Filial filial;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new Troca());

			filiais = new ArrayList<>();

			getFilterEntity().setIdCliente(SessionUtil.getIdCliente());

			if (SessionUtil.isUsuarioAdministrador()) {
				if (isView(VIEW_CONSULTA)) {
					clientes = clienteService.consulta(new Cliente());
				}

			} else if (SessionUtil.isUsuarioCliente()) {

			} else if (SessionUtil.isUsuarioFilial() || SessionUtil.isUsuarioProprietario()) {
				getFilterEntity().setIdFilial(SessionUtil.getIdFilial());

			}

			if (null != getFilterEntity().getIdCliente()) {
				if (isView(VIEW_CONSULTA)) {
					changeCliente();
				}

				try {
					politicaTroca = clienteService.consultaParametro(getFilterEntity().getIdCliente(), ParametroClienteEnum.POLITICA_TROCA);

				} catch (ResultadoNaoEncontradoException e) {
				}
			}

			if (isView(VIEW_SOLICITACAO)) {
				PedidoSimple pedidoFilter = new PedidoSimple();
				pedidoFilter.setIdCliente(getFilterEntity().getIdCliente());
				pedidoFilter.setIdFilial(getFilterEntity().getIdFilial());

				pedidos = pedidoService.consulta(pedidoFilter);

				if (StringUtils.isNotEmpty(politicaTroca)) {
					showDialog("politicaTrocaDialog");
				}

			} else if (isView(VIEW_CONSULTA)) {
				if (SessionUtil.isUsuarioFilial() || SessionUtil.isUsuarioProprietario()) {
					search();
				}
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			if (!isEditing()) {
				service.cadastra(getEntity());

				returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Obrigado pela sua solicitação de número <strong>"
						+ getEntity().getFormattedId() + "</strong>.<br/>"
						+ "Verifique no menu <strong>Troca</strong> as condições de envio dos seus produtos e condições gerais de Política de troca.");
			}

			close();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);
			return false;
		}

		return false;
	}

	@Override
	protected void afterSave() {
		edit(getEntity());
	}

	@Override
	protected void beforeAdd() {
	}

	@Override
	protected void executeEdit(Troca entity) {
		setEntity(service.consultaPorId(entity.getId()));

		Pedido pedido = pedidoService.consultaPorId(entity.getIdPedido());

		cliente = clienteService.consultaPorId(pedido.getIdCliente());

		if (null != pedido.getIdFilial() && 0 != pedido.getIdFilial()) {
			filial = filialService.consultaPorId(pedido.getIdFilial());
		} else {
			filial = null;
		}
	}

	@Override
	protected void executeSearch() {
		if (!isPesquisavel()) {
			if (SessionUtil.isUsuarioFilial() || SessionUtil.isUsuarioProprietario()) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar o cliente", null);
			}
			if (SessionUtil.isUsuarioCliente()) {
				returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a coleção", null);
			}

			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	// Fluxo
	public void finaliza() {
		try {
			service.finaliza(getEntity(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Troca marcada como finalizada com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Troca com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar troca como finalizada, contate o administrador", t);
		}
	}

	public void cancela() {
		try {
			service.cancela(getEntity(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Troca marcada como cancelada com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Troca com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar troca como cancelada, contate o administrador", t);
		}
	}

	private void afterOperacao() {
		edit(getEntity());
		executeSearch();

		updateComponent("editForm");
		updateComponent("searchForm");
		observacao = null;
	}

	// Pedido
	public void setSelectedPedido(PedidoSimple pedidoSimple) {
		try {
			setEntity(new Troca());
			getEntity().setIdCliente(getFilterEntity().getIdCliente());
			getEntity().setItens(new ArrayList<>());
			getEntity().setIdPedido(pedidoSimple.getId());

			Pedido pedido = pedidoService.consultaCompletoPorId(pedidoSimple.getId());

			tamanhos = new HashMap<>();

			for (PedidoItem pedidoItem : pedido.getItens()) {
				Integer idProduto = pedidoItem.getIdProduto();

				TrocaItem item = new TrocaItem();
				item.setIdPedidoItem(pedidoItem.getId());
				item.setIdProduto(idProduto);
				item.setQuantidade(0);
				item.setCodigoProduto(pedidoItem.getCodigoProduto());
				item.setTituloProduto(pedidoItem.getTituloProduto());
				item.setIdImagemCapaProduto(pedidoItem.getIdImagemCapaProduto());
				item.setQuantidadePedido(pedidoItem.getQuantidade());
				item.setCodigoTamanhoPedido(pedidoItem.getCodigoTamanho());

				getEntity().getItens().add(item);

				if (!tamanhos.containsKey(idProduto)) {
					tamanhos.put(idProduto, produtoService.consultaCompletoPorId(idProduto).getTamanhos());
				}
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao selecionar pedido, contate o administrador", t);
		}
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);

			if (!SessionUtil.isUsuarioFilial() && !SessionUtil.isUsuarioProprietario()) {
				getFilterEntity().setIdFilial(null);
			}

			filiais.clear();

			if (isClienteSelecionado() && (!SessionUtil.isUsuarioFilial() && !SessionUtil.isUsuarioProprietario())) {
				filiais.addAll(filialService.consultaPorCliente(getFilterEntity().getIdCliente(), false));
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados da coleção, contate o administrador", t);
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	public boolean isFilialSelecionada() {
		return null != getFilterEntity().getIdFilial() && 0 != getFilterEntity().getIdFilial();
	}

	public boolean isFinalizavel() {
		return null != getEntity() && getEntity().isCadastrada() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_TROCA_FINALIZACAO);
	}

	public boolean isCancelavel() {
		return null != getEntity() && getEntity().isCadastrada() && SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_TROCA_CANCELAMENTO);
	}

	public boolean isPesquisavel() {
		return isClienteSelecionado() || ((SessionUtil.isUsuarioFilial() || SessionUtil.isUsuarioProprietario()) && isFilialSelecionada());
	}

	public boolean isAdicionavel() {
		return (SessionUtil.isUsuarioCliente() && isClienteSelecionado())
				|| ((SessionUtil.isUsuarioFilial() || SessionUtil.isUsuarioProprietario()) && isFilialSelecionada());
	}

	// Getters e Setters
	public List<PedidoSimple> getPedidos() {
		return pedidos;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<Filial> getFiliais() {
		return filiais;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getPoliticaTroca() {
		return politicaTroca;
	}

	public List<ProdutoTamanho> getTamanhos(Integer idProduto) {
		return tamanhos.get(idProduto);
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Filial getFilial() {
		return filial;
	}
}