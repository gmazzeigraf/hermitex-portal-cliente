package br.com.graflogic.hermitex.cliente.data.entity.acesso;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.com.graflogic.hermitex.cliente.data.dom.DomAcesso.DomTipoUsuario;

/**
 * 
 * @author gmazz
 *
 */
@Entity
@Table(name = "tb_usuario_administrador")
@PrimaryKeyJoinColumn(name = "id")
public class UsuarioAdministrador extends Usuario {

	private static final long serialVersionUID = -5481235430672903265L;

	public static final String PERMISSAO = "ROLE_USUARIO_ADMINISTRADOR";

	public UsuarioAdministrador() {
		setTipo(DomTipoUsuario.ADMINISTRADOR);
	}
}