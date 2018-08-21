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
@Table(name = "tb_perfil_usuario_proprietario")
@PrimaryKeyJoinColumn(name = "id")
public class PerfilUsuarioProprietario extends PerfilUsuario {

	private static final long serialVersionUID = -4054210164748359343L;

	public PerfilUsuarioProprietario() {
		setTipoUsuario(DomTipoUsuario.PROPRIETARIO);
	}
}