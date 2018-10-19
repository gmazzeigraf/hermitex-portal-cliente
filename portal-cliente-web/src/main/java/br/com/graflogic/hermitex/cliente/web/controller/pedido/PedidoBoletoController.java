package br.com.graflogic.hermitex.cliente.web.controller.pedido;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoBoleto;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.PedidoBoletoService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.PedidoService;
import br.com.graflogic.utilities.presentationutil.controller.SearchBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class PedidoBoletoController extends SearchBaseController<PedidoBoleto, PedidoBoleto> implements InitializingBean {

	private static final long serialVersionUID = -3559118582167158036L;

	@Autowired
	private PedidoBoletoService service;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private PedidoService pedidoService;

	private List<Cliente> clientes;

	private List<Filial> filiais;

	private Cliente cliente;

	private Filial filial;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new PedidoBoleto());

			clientes = clienteService.consulta(new Cliente());
			filiais = new ArrayList<>();

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected void executeSearch() {
		setEntities(service.consulta(getFilterEntity()));
	}

	@Override
	protected void select(PedidoBoleto entity) {
		setEntity(service.consultaPorId(entity.getId()));

		Pedido pedido = pedidoService.consultaPorId(entity.getIdPedido());

		cliente = clienteService.consultaPorId(pedido.getIdCliente());

		if (null != pedido.getIdFilial() && 0 != pedido.getIdFilial()) {
			filial = filialService.consultaPorId(pedido.getIdFilial());
		} else {
			filial = null;
		}
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);

			getFilterEntity().setIdFilial(null);

			filiais.clear();

			if (isClienteSelecionado()) {
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

	public Cliente getCliente() {
		return cliente;
	}

	public Filial getFilial() {
		return filial;
	}
}