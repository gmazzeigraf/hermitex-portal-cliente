package br.com.graflogic.hermitex.cliente.web.controller.pedido;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomPermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.SolicitacaoTroca;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.PedidoService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.SolicitacaoTrocaService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class SolicitacaoTrocaController extends CrudBaseController<SolicitacaoTroca, SolicitacaoTroca> implements InitializingBean {

	private static final long serialVersionUID = -8717653202903739039L;

	@Autowired
	private SolicitacaoTrocaService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private PedidoService pedidoService;

	private List<Cliente> clientes;

	private List<Filial> filiais;

	private Pedido pedido;

	private String observacao;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new SolicitacaoTroca());

			filiais = new ArrayList<>();

			getFilterEntity().setIdCliente(SessionUtil.getIdCliente());

			if (SessionUtil.isUsuarioAdministrador()) {
				clientes = clienteService.consulta(new Cliente());

			} else if (SessionUtil.isUsuarioCliente()) {

			} else if (SessionUtil.isUsuarioFilial()) {
				getFilterEntity().setIdFilial(SessionUtil.getIdFilial());
			}

			if (null != getFilterEntity().getIdCliente()) {
				changeCliente();
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

				returnInfoMessage("Solicitação de troca cadastrada com sucesso", null);
			}
		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);
			return false;
		}

		return true;
	}

	@Override
	protected void afterSave() {
		edit(getEntity());
	}

	@Override
	protected void beforeAdd() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar o cliente", null);
			return;
		}

		setEntity(new SolicitacaoTroca());
		getEntity().setIdCliente(getFilterEntity().getIdCliente());
		getEntity().setIdFilial(getFilterEntity().getIdFilial());

		pedido = null;
	}

	@Override
	protected void executeEdit(SolicitacaoTroca entity) {
		setEntity(service.consultaPorId(entity.getId()));

		pedido = pedidoService.consultaPorId(getEntity().getIdPedido());
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar o cliente", null);
			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	// Fluxo
	public void finaliza() {
		try {
			service.finaliza(getEntity(), observacao);

			returnInfoDialogMessage(I18NUtil.getLabel("sucesso"), "Solicitação de troca marcada como finalizada com sucesso");

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Solicitação de troca com dados desatualizados, altere novamente", null);

			select(getEntity().getId());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao marcar solicitação de troca como finalizada, contate o administrador", t);
		}
	}

	private void afterOperacao() {
		edit(getEntity());
		executeSearch();

		updateComponent("editForm");
		updateComponent("searchForm");
		observacao = null;
	}

	// Util
	public void consultaPedido() {
		try {
			pedido = null;

			pedido = pedidoService.consultaPorIdClienteFilial(getEntity().getIdPedido(), getEntity().getIdCliente(), getEntity().getIdFilial());

		} catch (ResultadoNaoEncontradoException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Pedido não encontrado", null);

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar pedido, contate o administrador", t);
		}
	}

	public void changeCliente() {
		try {
			setEntities(null);
			if (!SessionUtil.isUsuarioFilial()) {
				getFilterEntity().setIdFilial(null);
			}
			filiais.clear();

			if (isClienteSelecionado() && !SessionUtil.isUsuarioFilial()) {
				filiais.addAll(filialService.consultaPorCliente(getFilterEntity().getIdCliente(), false));
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", t);
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	public boolean isFinalizavel() {
		return null != getEntity() && getEntity().isCadastrada()
				&& SessionUtil.possuiPermissao(DomPermissaoAcesso.ROLE_PEDIDO_SOLICITACAO_TROCA_FINALIZACAO);
	}

	// Getters e Setters
	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<Filial> getFiliais() {
		return filiais;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}