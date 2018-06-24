package br.com.graflogic.hermitex.cliente.web.controller.acesso;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuarioAdministrador;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PermissaoAcesso;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PerfilUsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PermissaoAcessoService;
import br.com.graflogic.utilities.presentationutil.controller.CrudBaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("view")
public class PerfilUsuarioController extends CrudBaseController<PerfilUsuario, PerfilUsuario> implements InitializingBean {

	private static final long serialVersionUID = 1082162763639413315L;

	private static final String VIEW_ADMINISTRADOR = "administracao/acesso/perfil.xhtml";

	@Autowired
	private PerfilUsuarioService service;

	@Autowired
	private PermissaoAcessoService permissaoService;

	private DualListModel<PermissaoAcesso> permissoes;

	private List<PermissaoAcesso> permissoesDisponiveis;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			if (isView(VIEW_ADMINISTRADOR)) {
				setFilterEntity(new PerfilUsuarioAdministrador());

			}

			permissoesDisponiveis = permissaoService.consulta(getFilterEntity().getTipoUsuario());

			permissoes = new DualListModel<PermissaoAcesso>(new ArrayList<PermissaoAcesso>(), new ArrayList<PermissaoAcesso>());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			getEntity().setPermissoes(permissoes.getTarget());

			if (isEditing()) {
				try {
					service.atualiza(getEntity());

					returnInfoMessage("Perfil atualizado com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				service.cadastra(getEntity());

				returnInfoMessage("Perfil cadastrado com sucesso", null);
			}

			edit(getEntity());

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);
			return false;
		}

		return true;
	}

	@Override
	protected void afterSave() {
		setEntity(service.consultaPorId(getEntity().getId()));
	}

	@Override
	protected void beforeAdd() {
		if (isView(VIEW_ADMINISTRADOR)) {
			setEntity(new PerfilUsuarioAdministrador());

		}

		carregaPermissoes();
	}

	@Override
	protected void executeEdit(PerfilUsuario entity) {
		setEntity(service.consultaPorId(entity.getId()));

		List<PermissaoAcesso> permissoesSelecionadas = permissaoService.consultaPorPerfilUsuario(getEntity().getId());

		carregaPermissoes();

		permissoes.getTarget().addAll(permissoesSelecionadas);

		for (PermissaoAcesso permissao : permissoes.getTarget()) {
			permissoes.getSource().remove(permissao);
		}
	}

	@Override
	protected void executeSearch() {
		setEntities(service.consulta(getFilterEntity()));
	}

	// Utilities
	private void carregaPermissoes() {
		permissoes.getSource().clear();
		permissoes.getSource().addAll(permissoesDisponiveis);
		permissoes.getTarget().clear();
	}

	public DualListModel<PermissaoAcesso> getPermissoes() {
		return permissoes;
	}

	// Util
	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Perfil inativado com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao invativar perfil, contate o administrador", t);
		}
	}

	public void ativa() {
		try {
			service.ativa(getEntity());

			returnInfoMessage("Perfil ativado com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao ativar perfil, contate o administrador", t);
		}
	}

	// Getters e Setters
	public void setPermissoes(DualListModel<PermissaoAcesso> permissoes) {
		this.permissoes = permissoes;
	}
}