package br.com.graflogic.hermitex.cliente.web.controller;

import javax.faces.application.Resource;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import br.com.graflogic.base.service.util.I18NUtil;
import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomStatusSenhaUsuario;
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

	@Value("${version}")
	private String versaoSistema;

	@Value("${build.date}")
	private String dataBuild;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao iniciar a sessão, contate o administrador", t);
		}
	}

	// Util
	public void verificaSenhaTemporaria() {
		try {
			if (!isView("/acesso/alteracao_senha") && null != SessionUtil.getAuthenticatedUsuario() && !isSenhaUsuarioDefinitiva()) {
				SessionUtil.redirecionaView("/pages/usuario/acesso/alteracao_senha.jsf");
			}

		} catch (Throwable t) {
			returnFatalDialogMessage(I18NUtil.getLabel("erro"), "Erro ao verificar senha temporária, contate o administrador", null);
		}
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
			Resource resource = context.getApplication().getResourceHandler().createResource("images/logo-black.png", "poseidon-layout");
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
}