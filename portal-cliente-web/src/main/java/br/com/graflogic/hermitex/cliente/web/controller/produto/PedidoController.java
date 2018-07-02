package br.com.graflogic.hermitex.cliente.web.controller.produto;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioCliente;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioFilial;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.PedidoService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.presentationutil.controller.SearchBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class PedidoController extends SearchBaseController<PedidoSimple, Pedido> implements InitializingBean {

	private static final long serialVersionUID = 3702814175660127874L;

	@Autowired
	private PedidoService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	private List<Cliente> clientes;

	private List<Filial> filiais;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new PedidoSimple());

			if (SessionUtil.isUsuarioAdministrador()) {
				clientes = clienteService.consulta(new Cliente());

			} else if (SessionUtil.isUsuarioCliente()) {
				getFilterEntity().setIdCliente(((UsuarioCliente) SessionUtil.getAuthenticatedUsuario()).getIdCliente());

			} else if (SessionUtil.isUsuarioFilial()) {
				getFilterEntity().setIdCliente(((UsuarioFilial) SessionUtil.getAuthenticatedUsuario()).getIdCliente());
				getFilterEntity().setIdFilial(((UsuarioFilial) SessionUtil.getAuthenticatedUsuario()).getIdFilial());
			}

			if (null != getFilterEntity().getIdCliente()) {
				changeCliente();

			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
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
	protected void select(PedidoSimple entity) {
		setEntity(service.consultaCompletoPorId(entity.getId()));
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);

			if (isClienteSelecionado() && !SessionUtil.isUsuarioFilial()) {
				filiais = filialService.consultaPorCliente(getFilterEntity().getIdCliente(), false);
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", t);
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

	public List<Filial> getFiliais() {
		return filiais;
	}
}