package br.com.graflogic.hermitex.cliente.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.Resource;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusSenhaUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PermissaoAcesso;
import br.com.graflogic.hermitex.cliente.service.impl.acesso.PermissaoAcessoService;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;
import br.com.graflogic.utilities.presentationutil.controller.BaseController;

/**
 * 
 * @author gmazz
 *
 */
@Controller
@Scope("session")
public class SessionController extends BaseController implements InitializingBean {

	private static final long serialVersionUID = 8303028606794937818L;

	@Autowired
	private PermissaoAcessoService permissaoAcessoService;

	@Value("${version}")
	private String versaoSistema;

	@Value("${build.date}")
	private String dataBuild;

	private String parametrosYoutube;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			parametrosYoutube = "?rel=0&modestbranding=1";

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao iniciar a sessão, contate o administrador", t);
		}
	}

	// Util
	public void verificaRedirecionamento() {
		try {
			if (SessionUtil.isAutenticado()) {

				// Verifica se precisa alterar a senha
				if (!isSenhaUsuarioDefinitiva()) {
					if (!isView("acesso/alteracao_senha")) {
						SessionUtil.redirecionaView("/pages/usuario/acesso/alteracao_senha.jsf");
					}

					return;
				}

				// Verifica se e proprietario e precisa selecionar a filial
				if (!isView("filial/seleciona") && SessionUtil.isUsuarioProprietario() && null == SessionUtil.getIdFilial()) {
					SessionUtil.redirecionaView("/pages/filial/seleciona.jsf");
					return;
				}

				// Verifica se precisa redirecionar a pagina inicial
				if (isView("pages/home")) {
					if (isSenhaUsuarioDefinitiva() && (SessionUtil.isUsuarioFilial() || SessionUtil.isUsuarioProprietario())) {
						SessionUtil.redirecionaView("/pages/compra/produtos.jsf");
						return;
					}
				}
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao verificar redirecionamento, contate o administrador", null);
		}
	}

	public void carregaPermissoes() {
		List<PermissaoAcesso> permissoesPerfil = permissaoAcessoService.consultaPorPerfilUsuario(SessionUtil.getAuthenticatedUsuario().getIdPerfil());

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (PermissaoAcesso permissao : permissoesPerfil) {
			authorities.add(new SimpleGrantedAuthority(permissao.getCodigo()));
		}

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(getAuthenticatedUser().getPrincipal(), getAuthenticatedUser().getCredentials(), authorities));
	}

	// Condicoes
	public boolean isAutenticado() {
		return SessionUtil.isAutenticado();
	}

	public boolean isSenhaUsuarioDefinitiva() {
		return DomStatusSenhaUsuario.DEFINITIVA.equals(SessionUtil.getAuthenticatedUsuario().getStatusSenha());
	}

	public boolean isUsuarioAdministrador() {
		return SessionUtil.isUsuarioAdministrador();
	}

	public boolean isUsuarioCliente() {
		return SessionUtil.isUsuarioCliente();
	}

	public boolean isUsuarioFilial() {
		return SessionUtil.isUsuarioFilial();
	}

	public boolean isUsuarioRepresentante() {
		return SessionUtil.isUsuarioRepresentante();
	}

	public boolean isUsuarioProprietario() {
		return SessionUtil.isUsuarioProprietario();
	}

	// Produto
	public String getUrlImagem(String idImagem, Integer miniatura) {
		String url = "/imagens?idImagem=" + idImagem + "&miniatura=" + (null != miniatura ? miniatura : "");

		return url;
	}

	public String getUrlLogotipo() {
		Integer idCliente = SessionUtil.getIdCliente();

		if (null != idCliente) {
			return getUrlLogotipo(idCliente);

		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			Resource resource = context.getApplication().getResourceHandler().createResource("images/logo-colored.png", "poseidon-layout");
			String url = resource.getRequestPath();

			return url;
		}
	}

	public String getUrlLogotipo(Integer idCliente) {
		String url = "/logotipo?idCliente=" + idCliente;

		return url;
	}

	// Getters e Setters
	public String getVersaoSistema() {
		return versaoSistema;
	}

	public String getDataBuild() {
		return dataBuild;
	}

	public String getParametrosYoutube() {
		return parametrosYoutube;
	}
}