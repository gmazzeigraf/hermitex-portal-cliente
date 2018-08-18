package br.com.graflogic.hermitex.cliente.web.controller.pedido;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.pedido.JanelaCompraService;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class JanelaCompraController extends CrudBaseController<JanelaCompra, JanelaCompra> implements InitializingBean {

	private static final long serialVersionUID = 3702814175660127874L;

	@Autowired
	private JanelaCompraService service;

	@Autowired
	private ClienteService clienteService;

	private List<Cliente> clientes;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new JanelaCompra());

			clientes = clienteService.consulta(new Cliente());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			if (!isEditing()) {
				service.cadastra(getEntity());

				returnInfoMessage("Janela cadastrada com sucesso", null);
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

		setEntity(new JanelaCompra());
		getEntity().setIdCliente(getFilterEntity().getIdCliente());
	}

	@Override
	protected void executeEdit(JanelaCompra entity) {
		setEntity(service.consultaPorId(entity.getId()));
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar o cliente", null);
			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados do cliente, contate o administrador", t);
		}
	}

	public void fecha() {
		try {
			service.fecha(getEntity());

			returnInfoMessage("Janela fechada com sucesso", null);

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao fechar janela, contate o administrador", t);
		}
	}

	public void reabre() {
		try {
			service.reabre(getEntity());

			returnInfoMessage("Janela reaberta com sucesso", null);

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao reabrir janela, contate o administrador", t);
		}
	}

	public void cancela() {
		try {
			service.cancela(getEntity());

			returnInfoMessage("Janela cancelada com sucesso", null);

			afterOperacao();

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao cancelar janela, contate o administrador", t);
		}
	}

	public StreamedContent getExtracaoExcel() throws IOException {
		try {
			byte[] extracao = service.geraExtracao(getEntity());

			return new DefaultStreamedContent(new ByteArrayInputStream(extracao), "", "extracao.xlsx");

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao gerar a extração, contate o administrador", t);
			return null;
		}
	}

	private void afterOperacao() {
		edit(getEntity());
		executeSearch();

		updateComponent("editForm");
		updateComponent("searchForm");
	}

	// Condicoes
	public boolean isClienteSelecionado() {
		return null != getFilterEntity().getIdCliente() && 0 != getFilterEntity().getIdCliente();
	}

	// Getters e Setters
	public List<Cliente> getClientes() {
		return clientes;
	}
}