package br.com.graflogic.hermitex.cliente.web.controller.produto;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CategoriaProduto;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.produto.CategoriaProdutoService;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class CategoriaProdutoController extends CrudBaseController<CategoriaProduto, CategoriaProduto> implements InitializingBean {

	private static final long serialVersionUID = 2066072329838868350L;

	@Autowired
	private CategoriaProdutoService service;

	@Autowired
	private ClienteService clienteService;

	private List<Cliente> clientes;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new CategoriaProduto());

			clientes = clienteService.consulta(new Cliente());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			if (isEditing()) {
				try {
					service.atualiza(getEntity());

					returnInfoMessage("Categoria atualizada com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				service.cadastra(getEntity());

				returnInfoMessage("Categoria cadastrada com sucesso", null);
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
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a coleção", null);
			return;
		}

		setEntity(new CategoriaProduto());
		getEntity().setIdCliente(getFilterEntity().getIdCliente());
	}

	@Override
	protected void executeEdit(CategoriaProduto entity) {
		setEntity(service.consultaPorId(entity.getId()));
	}

	@Override
	protected void executeSearch() {
		if (!isClienteSelecionado()) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor selecionar a coleção", null);
			return;
		}

		setEntities(service.consulta(getFilterEntity()));
	}

	// Util
	public void changeCliente() {
		try {
			setEntities(null);

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao consultar dados da coleção, contate o administrador", t);
		}
	}

	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Categoria inativada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inativar categoria, contate o administrador", t);
		}
	}

	public void ativa() {
		try {
			service.ativa(getEntity());

			returnInfoMessage("Categoria ativada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao ativar categoria, contate o administrador", t);
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
}