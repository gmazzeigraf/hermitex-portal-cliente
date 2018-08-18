package br.com.graflogic.hermitex.cliente.web.controller.acesso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusSenhaUsuario;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomTipoUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioAdministrador;
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
import br.com.graflogic.hermitex.cliente.service.impl.acesso.UsuarioService;
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
public class UsuarioController extends CrudBaseController<Usuario, Usuario> implements InitializingBean {

	private static final long serialVersionUID = -66975495239952137L;

	private static final String VIEW_ALTERACAO_SENHA = "usuario/acesso/alteracao_senha.xhtml";
	private static final String VIEW_ADMINISTRADOR = "administracao/acesso/usuario.xhtml";
	private static final String VIEW_CLIENTE = "cliente/acesso/usuario.xhtml";
	private static final String VIEW_FILIAL = "filial/acesso/usuario.xhtml";
	private static final String VIEW_REPRESENTANTE = "representante/acesso/usuario.xhtml";

	@Autowired
	private UsuarioService service;

	@Autowired
	private PerfilUsuarioService perfilService;

	@Autowired
	private PermissaoAcessoService permissaoAcessoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private RepresentanteService representanteService;

	private List<PerfilUsuario> perfis;

	private String senhaAtual;

	private String novaSenha;

	private List<Object> entidades;

	private Integer idEntidade;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			entidades = new ArrayList<>();

			if (isView(VIEW_ALTERACAO_SENHA)) {
				setEntity(SessionUtil.getAuthenticatedUsuario());

				if (DomStatusSenhaUsuario.TEMPORARIA.equals(getEntity().getStatusSenha())) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor alterar a senha temporária para acesso completo ao sistema", null);
				}

			} else if (isViewAdministrador()) {
				setFilterEntity(new UsuarioAdministrador());

				perfis = perfilService.consulta(DomTipoUsuario.ADMINISTRADOR, null);

			} else if (isViewCliente()) {
				setFilterEntity(new UsuarioCliente());

				if (SessionUtil.isUsuarioAdministrador()) {
					entidades.addAll(clienteService.consulta(new Cliente()));

				} else if (SessionUtil.isUsuarioCliente()) {
					idEntidade = SessionUtil.getIdCliente();

					changeEntidade();
				}

			} else if (isViewFilial()) {
				setFilterEntity(new UsuarioFilial());

				if (SessionUtil.isUsuarioAdministrador()) {
					entidades.addAll(filialService.consulta(new Filial()));

				} else if (SessionUtil.isUsuarioCliente()) {
					entidades.addAll(filialService.consultaPorCliente(SessionUtil.getIdCliente(), false));

				} else if (SessionUtil.isUsuarioFilial()) {
					idEntidade = SessionUtil.getIdFilial();

					changeEntidade();

				}

			} else if (isViewRepresentante()) {
				setFilterEntity(new UsuarioRepresentante());

				if (SessionUtil.isUsuarioAdministrador()) {
					entidades.addAll(representanteService.consulta(new Representante()));

				} else if (SessionUtil.isUsuarioRepresentante()) {
					idEntidade = SessionUtil.getIdRepresentante();

					changeEntidade();
				}
			}

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

					returnInfoMessage("Usuário atualizado com sucesso", null);

				} catch (DadosDesatualizadosException e) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

				}

			} else {
				service.cadastra(getEntity());

				returnInfoMessage("Usuário cadastrado com sucesso", null);

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
			setEntity(new UsuarioAdministrador());

		} else if (isViewCliente()) {
			setEntity(new UsuarioCliente());
			((UsuarioCliente) getEntity()).setIdCliente(idEntidade);

		} else if (isViewFilial()) {
			setEntity(new UsuarioFilial());
			((UsuarioFilial) getEntity()).setIdFilial(idEntidade);

		} else if (isViewRepresentante()) {
			setEntity(new UsuarioRepresentante());
			((UsuarioRepresentante) getEntity()).setIdRepresentante(idEntidade);

		}
	}

	@Override
	protected void executeEdit(Usuario entity) {
		setEntity(service.consultaPorId(entity.getId()));
	}

	@Override
	protected void executeSearch() {
		if (isViewCliente()) {
			((UsuarioCliente) getFilterEntity()).setIdCliente(idEntidade);

		} else if (isViewFilial()) {
			((UsuarioFilial) getFilterEntity()).setIdFilial(idEntidade);

		} else if (isViewRepresentante()) {
			((UsuarioRepresentante) getFilterEntity()).setIdRepresentante(idEntidade);

		}

		setEntities(service.consulta(getFilterEntity()));
	}

	// Util
	public void changeEntidade() {
		try {
			setEntities(null);
			getFilterEntity().setIdPerfil(null);

			perfis = null;

			if (null != idEntidade && 0 != idEntidade) {
				perfis = perfilService.consulta(getFilterEntity().getTipo(), idEntidade);
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar valor, contate o administrador", t);
		}
	}

	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Usuário inativado com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inativar usuário, contate o administrador", t);
		}
	}

	public void ativa() {
		try {
			service.ativa(getEntity());

			returnInfoMessage("Usuário ativado com sucesso", null);

			edit(getEntity());

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao ativar usuário, contate o administrador", t);
		}
	}

	public void geraNovaSenha() {
		try {
			service.geraNovaSenha(getEntity());

			returnInfoMessage("Nova senha gerada com sucesso, um e-mail será enviado para o usuário", null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao gerar nova senha, contate o administrador", t);

		}
	}

	public void alteraSenha() {
		try {
			// Verifica se e senha temporaria
			boolean senhaTemporaria = DomStatusSenhaUsuario.TEMPORARIA.equals(getEntity().getStatusSenha());

			service.alteraSenha(getEntity().getId(), senhaAtual, novaSenha);

			Integer idCliente = SessionUtil.getIdCliente();

			setEntity(service.consultaPorId(getEntity().getId()));

			if (SessionUtil.isUsuarioCliente()) {
				((UsuarioCliente) getEntity()).setIdCliente(idCliente);

			} else if (SessionUtil.isUsuarioFilial()) {
				((UsuarioFilial) getEntity()).setIdCliente(idCliente);

			}

			SessionUtil.getAuthenticatedUser().setUsuario(getEntity());

			// Caso fosse senha temporaria, adiciona as permissoes no usuario logado
			if (senhaTemporaria) {
				List<PermissaoAcesso> permissoesPerfil = permissaoAcessoService.consultaPorPerfilUsuario(getEntity().getIdPerfil());

				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

				for (PermissaoAcesso permissao : permissoesPerfil) {
					authorities.add(new SimpleGrantedAuthority(permissao.getCodigo()));
				}

				SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(getAuthenticatedUser().getPrincipal(),
						getAuthenticatedUser().getCredentials(), authorities));

				returnInfoMessage("Senha alterada com sucesso, o acesso completo foi liberado", getApplicationUrl() + "/pages/home.jsf");

			} else {
				returnInfoMessage("Senha alterada com sucesso", null);
			}

		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao alterar senha, contate o administrador", t);

		} finally {
			novaSenha = null;
			senhaAtual = null;
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
	public String getSenhaAtual() {
		return senhaAtual;
	}

	public List<PerfilUsuario> getPerfis() {
		return perfis;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
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