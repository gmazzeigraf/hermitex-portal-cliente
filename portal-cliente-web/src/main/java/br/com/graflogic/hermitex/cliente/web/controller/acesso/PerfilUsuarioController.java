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
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuarioCliente;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuarioFilial;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuarioRepresentante;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioCliente;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioFilial;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioRepresentante;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Cliente;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Representante;
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PerfilUsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PermissaoAcessoService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.ClienteService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.FilialService;
import br.com.graflogic.hermitex.cliente.service.impl.cadastro.RepresentanteService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
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
	private static final String VIEW_CLIENTE = "cliente/acesso/perfil.xhtml";
	private static final String VIEW_FILIAL = "filial/acesso/perfil.xhtml";
	private static final String VIEW_REPRESENTANTE = "representante/acesso/perfil.xhtml";

	@Autowired
	private PerfilUsuarioService service;

	@Autowired
	private PermissaoAcessoService permissaoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private RepresentanteService representanteService;

	private DualListModel<PermissaoAcesso> permissoes;

	private List<PermissaoAcesso> permissoesDisponiveis;

	private List<Object> entidades;

	private Integer idEntidade;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			entidades = new ArrayList<>();

			if (isViewAdministrador()) {
				setFilterEntity(new PerfilUsuarioAdministrador());

			} else if (isViewCliente()) {
				setFilterEntity(new PerfilUsuarioCliente());

				if (SessionUtil.isUsuarioAdministrador()) {
					entidades.addAll(clienteService.consulta(new Cliente()));

				} else if (SessionUtil.isUsuarioCliente()) {
					idEntidade = ((UsuarioCliente) SessionUtil.getAuthenticatedUsuario()).getIdCliente();
				}

			} else if (isViewFilial()) {
				setFilterEntity(new PerfilUsuarioFilial());

				if (SessionUtil.isUsuarioAdministrador()) {
					entidades.addAll(filialService.consulta(new Filial()));

				} else if (SessionUtil.isUsuarioCliente()) {
					entidades
							.addAll(filialService.consultaPorCliente(((UsuarioCliente) SessionUtil.getAuthenticatedUsuario()).getIdCliente(), false));

				} else if (SessionUtil.isUsuarioFilial()) {
					idEntidade = ((UsuarioFilial) SessionUtil.getAuthenticatedUsuario()).getIdFilial();

				}
			} else if (isViewRepresentante()) {
				setFilterEntity(new PerfilUsuarioRepresentante());

				if (SessionUtil.isUsuarioAdministrador()) {
					entidades.addAll(representanteService.consulta(new Representante()));

				} else if (SessionUtil.isUsuarioRepresentante()) {
					idEntidade = ((UsuarioRepresentante) SessionUtil.getAuthenticatedUsuario()).getIdRepresentante();
				}

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
		if (isViewAdministrador()) {
			setEntity(new PerfilUsuarioAdministrador());

		} else if (isViewCliente()) {
			setEntity(new PerfilUsuarioCliente());
			((PerfilUsuarioCliente) getEntity()).setIdCliente(idEntidade);

		} else if (isViewFilial()) {
			setEntity(new PerfilUsuarioFilial());
			((PerfilUsuarioFilial) getEntity()).setIdFilial(idEntidade);

		} else if (isViewRepresentante()) {
			setEntity(new PerfilUsuarioRepresentante());
			((PerfilUsuarioRepresentante) getEntity()).setIdRepresentante(idEntidade);

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
		if (isViewCliente()) {
			((PerfilUsuarioCliente) getFilterEntity()).setIdCliente(idEntidade);

		} else if (isViewFilial()) {
			((PerfilUsuarioFilial) getFilterEntity()).setIdFilial(idEntidade);

		} else if (isViewRepresentante()) {
			((PerfilUsuarioRepresentante) getFilterEntity()).setIdRepresentante(idEntidade);

		}

		setEntities(service.consulta(getFilterEntity()));
	}

	// Util
	private void carregaPermissoes() {
		permissoes.getSource().clear();
		permissoes.getSource().addAll(permissoesDisponiveis);
		permissoes.getTarget().clear();
	}

	public void changeEntidade() {
		try {
			setEntities(null);

		} catch (Throwable t) {
			returnFatalDialogMessage("Erro", "Erro ao alterar valor, contate o administrador", t);
		}
	}

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

	// Condicoes
	public boolean isEntidadeSelecionada() {
		return (null != idEntidade && idEntidade > 0);
	}

	public boolean isViewAdministrador() {
		return isView(VIEW_ADMINISTRADOR);
	}

	public boolean isViewCliente() {
		return isView(VIEW_CLIENTE);
	}

	public boolean isViewFilial() {
		return isView(VIEW_FILIAL);
	}

	public boolean isViewRepresentante() {
		return isView(VIEW_REPRESENTANTE);
	}

	// Getters e Setters
	public DualListModel<PermissaoAcesso> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(DualListModel<PermissaoAcesso> permissoes) {
		this.permissoes = permissoes;
	}

	public Integer getIdEntidade() {
		return idEntidade;
	}

	public void setIdEntidade(Integer idEntidade) {
		this.idEntidade = idEntidade;
	}

	public List<Object> getEntidades() {
		return entidades;
	}
}