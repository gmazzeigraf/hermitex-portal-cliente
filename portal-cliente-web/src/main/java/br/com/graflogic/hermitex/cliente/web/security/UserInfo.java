package br.com.graflogic.hermitex.cliente.web.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;

/**
 * 
 * @author gmazz
 *
 */
public class UserInfo extends User {

	private static final long serialVersionUID = -3881281666809405628L;

	private String idLogin;

	private Usuario usuario;

	private String ipOrigem;

	private Object empresa;

	public UserInfo(String codigo, String senha, Collection<? extends GrantedAuthority> permissoes, Usuario usuario, Object empresa) {
		super(codigo, senha, permissoes);
		setUsuario(usuario);
		setEmpresa(empresa);
	}

	public String getIdLogin() {
		return idLogin;
	}

	public void setIdLogin(String idLogin) {
		this.idLogin = idLogin;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Object getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Object empresa) {
		this.empresa = empresa;
	}

	public String getIpOrigem() {
		return ipOrigem;
	}

	public void setIpOrigem(String ipOrigem) {
		this.ipOrigem = ipOrigem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}
}