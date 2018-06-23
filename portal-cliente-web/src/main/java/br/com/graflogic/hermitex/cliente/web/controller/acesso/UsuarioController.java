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
import br.com.graflogic.hermitex.cliente.service.exception.DadosDesatualizadosException;
import br.com.graflogic.hermitex.cliente.service.exception.DadosInvalidosException;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PerfilUsuarioService;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PermissaoAcessoService;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.UsuarioService;
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

	@Autowired
	private UsuarioService service;

	@Autowired
	private PerfilUsuarioService perfilService;

	@Autowired
	private PermissaoAcessoService permissaoAcessoService;

	private List<PerfilUsuario> perfis;

	private List<Object> entidades;

	private String senhaAtual;

	private String novaSenha;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			entidades = new ArrayList<>();

			if (isView(VIEW_ALTERACAO_SENHA)) {
				setEntity(SessionUtil.getAuthenticatedUsuario());

				if (DomStatusSenhaUsuario.TEMPORARIA.equals(getEntity().getStatusSenha())) {
					returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Favor alterar a senha temporária para acesso completo ao sistema", null);
				}

			} else if (isView(VIEW_ADMINISTRADOR)) {
				setFilterEntity(new UsuarioAdministrador());

				perfis = perfilService.consulta(DomTipoUsuario.ADMINISTRADOR);
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao inicializar tela, contate o administrador", t);
		}
	}

	@Override
	protected boolean executeSave() {
		try {
			if (isEditing()) {
				service.atualiza(getEntity());

				returnInfoMessage("Usuário atualizado com sucesso", null);

			} else {
				service.cadastra(getEntity());

				returnInfoMessage("Usuário cadastrado com sucesso", null);

			}
		} catch (DadosInvalidosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), e.getMessage(), null);
			return false;
		}

		executeEdit(getEntity());

		return true;
	}

	@Override
	protected void afterSave() {
		setEntity(service.consultaPorId(getEntity().getId()));
	}

	@Override
	protected void beforeAdd() {
		if (isView(VIEW_ADMINISTRADOR)) {
			setEntity(new UsuarioAdministrador());

		}
	}

	@Override
	protected void executeEdit(Usuario entity) {
		setEntity(entity);
	}

	@Override
	protected void executeSearch() {
		setEntities(service.consulta(getFilterEntity()));
	}

	// Util
	public void inativa() {
		try {
			service.inativa(getEntity());

			returnInfoMessage("Usuário inativado com sucesso", null);

			search();

		} catch (DadosDesatualizadosException e) {
			returnWarnDialogMessage(I18NUtil.getLabel("aviso"), "Registro com dados desatualizados, altere novamente", null);

			edit(getEntity());

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao invativar usuário, contate o administrador", t);
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

			setEntity(service.consultaPorId(getEntity().getId()));

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

	public List<Object> getEntidades() {
		return entidades;
	}
}