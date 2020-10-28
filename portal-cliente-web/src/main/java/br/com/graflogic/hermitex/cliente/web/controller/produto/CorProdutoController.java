package br.com.graflogic.hermitex.cliente.web.controller.produto;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.produto.CorProduto;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.produto.CorProdutoService;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class CorProdutoController extends CrudBaseController<CorProduto, CorProduto> implements InitializingBean {

	private static final long serialVersionUID = 7057057933973863270L;

	@Autowired
	private CorProdutoService service;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			setFilterEntity(new CorProduto());

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

					returnInfoMessage("Cor atualizada com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				service.cadastra(getEntity());

				returnInfoMessage("Cor cadastrada com sucesso", null);
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
		setEntity(new CorProduto());
	}

	@Override
	protected void executeEdit(CorProduto entity) {
		setEntity(service.consultaPorCodigo(entity.getCodigo()));
	}

	@Override
	protected void executeSearch() {
		setEntities(service.consulta(getFilterEntity()));
	}

	// Util
	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Cor inativada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inativar cor, contate o administrador", t);
		}
	}

	public void ativa() {
		try {
			service.ativa(getEntity());

			returnInfoMessage("Cor ativada com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao ativar cor, contate o administrador", t);
		}
	}
}