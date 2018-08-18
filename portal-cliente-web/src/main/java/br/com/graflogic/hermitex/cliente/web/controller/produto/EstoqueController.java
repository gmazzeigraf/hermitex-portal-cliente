package br.com.graflogic.hermitex.cliente.web.controller.produto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.EstoqueService;
import br.com.graflogic.hermitex.cliente.service.model.produto.ItemRelatorioEstoque;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.presentationutil.controller.BaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class EstoqueController extends BaseController implements InitializingBean {

	private static final long serialVersionUID = -5591467308070574432L;

	@Autowired
	private EstoqueService service;

	@Autowired
	private ClienteService clienteService;

	private List<Cliente> clientes;

	private Integer idCliente;

	private List<ItemRelatorioEstoque> itensRelatorio;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			idCliente = SessionUtil.getIdCliente();

			if (SessionUtil.isUsuarioAdministrador()) {
				clientes = clienteService.consulta(new Cliente());

			} else if (SessionUtil.isUsuarioRepresentante()) {
				clientes = clienteService.consultaPorRepresentante(SessionUtil.getIdRepresentante());
			}

			itensRelatorio = new ArrayList<>();

			if (isClienteSelecionado()) {
				changeCliente();
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	// Relatorio
	public void geraRelatorio() {
		try {
			itensRelatorio.clear();

			itensRelatorio.addAll(service.geraRelatorio(idCliente, SessionUtil.isUsuarioCliente()));

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao gerar relatório, contate o administrador", t);
		}
	}

	// Util
	public void changeCliente() {
		try {
			itensRelatorio.clear();

			if (isClienteSelecionado()) {
				geraRelatorio();
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", t);
		}
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != idCliente && 0 != idCliente;
	}

	// Getters e Setters
	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<ItemRelatorioEstoque> getItensRelatorio() {
		return itensRelatorio;
	}
}