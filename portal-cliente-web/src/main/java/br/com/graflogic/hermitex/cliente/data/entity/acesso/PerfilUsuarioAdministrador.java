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
@Table(name = "tb_perfil_usuario_administrador")
@PrimaryKeyJoinColumn(name = "id")
public class PerfilUsuarioAdministrador extends PerfilUsuario {

	private static final long serialVersionUID = 5470893052520028326L;

	public PerfilUsuarioAdministrador() {
		setTipoUsuario(DomTipoUsuario.ADMINISTRADOR);
	}
}