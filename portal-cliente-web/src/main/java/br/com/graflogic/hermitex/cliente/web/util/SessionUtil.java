package br.com.graflogic.hermitex.cliente.web.util;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomTipoUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.web.security.UserInfo;

/**
 * 
 * @author gmazz
 *
 */
public class SessionUtil {

	public static boolean isAutenticado() {
		if (null == SecurityContextHolder.getContext().getAuthentication()
				|| SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
			return false;
		}
		return true;
	}

	public static void redirecionaView(String view) throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().dispatch(view);
	}

	public static Usuario getAuthenticatedUsuario() {
		UserInfo userInfo = getAuthenticatedUser();

		if (null != userInfo) {
			return userInfo.getUsuario();
		}

		return null;
	}

	public static UserInfo getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (null != authentication && authentication instanceof UsernamePasswordAuthenticationToken) {
			UserInfo user = (UserInfo) ((UsernamePasswordAuthenticationToken) authentication).getPrincipal();
			return user;
		} else {
			return null;
		}
	}

	public static boolean possuiPermissao(String permissao) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getAuthorities().contains(new SimpleGrantedAuthority(permissao));
	}

	public static String getIdLogin() {
		return getAuthenticatedUser().getIdLogin();
	}

	public static boolean isTipo(String tipo) {
		return getAuthenticatedUsuario().getTipo().equals(tipo);
	}

	public static boolean isUsuarioAdministrador() {
		return isTipo(DomTipoUsuario.ADMINISTRADOR);
	}

	public static boolean isUsuarioCliente() {
		return isTipo(DomTipoUsuario.CLIENTE);
	}

	public static boolean isUsuarioFilial() {
		return isTipo(DomTipoUsuario.FILIAL);
	}

	public static boolean isUsuarioRepresentante() {
		return isTipo(DomTipoUsuario.REPRESENTANTE);
	}
}